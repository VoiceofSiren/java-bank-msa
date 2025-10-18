package org.example.bank.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.example.bank.common.ApiResponse;
import org.example.bank.common.CircuitBreakerUtils;
import org.example.bank.common.TxAdvice;
import org.example.bank.dto.AccountView;
import org.example.bank.entity.Account;
import org.example.bank.entity.Transaction;
import org.example.bank.entity.TransactionType;
import org.example.bank.entity.UserReadView;
import org.example.bank.event.AccountCreatedEvent;
import org.example.bank.event.TransactionCreatedEvent;
import org.example.bank.lock.DistributedLockService;
import org.example.bank.metrics.BankMetrics;
import org.example.bank.publisher.EventPublisher;
import org.example.bank.repository.account.AccountRepository;
import org.example.bank.repository.transaction.TransactionRepository;
import org.example.bank.repository.user.UserRepository;
import org.example.bank.repository.userReadView.UserReadViewRepository;
import org.example.bank.request.AccountCreateRequest;
import org.example.bank.request.TransferRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountWriteService {
    private final TxAdvice txAdvice;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserReadViewRepository userReadViewRepository;
    private final DistributedLockService<ResponseEntity<ApiResponse<AccountView>>> accountLockService;
    private final DistributedLockService<ResponseEntity<ApiResponse<String>>> transferLockService;
    private final EventPublisher eventPublisher;
    private final BankMetrics bankMetrics;

    private final CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("accountWrite");

    private String randomAccountNumber() {
        return System.currentTimeMillis() + "";
    }

    public ResponseEntity<ApiResponse<AccountView>> createAccount(AccountCreateRequest accountCreateRequest) {

        String userId = accountCreateRequest.getUserId();
        String name = accountCreateRequest.getUsername();
        BigDecimal balance = accountCreateRequest.getInitialBalance();
        String accountNumber = randomAccountNumber();

        String email = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication().getName();
        try {
            return accountLockService.executeWithAccountLock(
                    accountNumber,
                    () ->  {
                        return CircuitBreakerUtils.execute(
                                // parameter1: CircuitBreaker circuitBreaker
                                circuitBreaker,
                                // parameter2: Supplier<T> operation
                                () -> {
                                    Account savedAccount = txAdvice.run(() -> {

                                        if (accountRepository.findByAccountNumber(accountNumber) != null) {
                                            throw new IllegalStateException("Account with accountNumber " + accountNumber + " already exists");
                                        }

                                        Account account = Account.builder()
                                                .accountNumber(accountNumber)
                                                .balance(balance)
                                                .userId(userId)
                                                .accountHolderName(name)
                                                .build();

                                        Optional<UserReadView> userReadView = userReadViewRepository.findByEmail(email);
                                        userReadView.ifPresent(userReadViewEntity -> {
                                            userReadViewEntity.createAccount(balance);
                                            userReadViewRepository.save(userReadViewEntity);
                                        });

                                        return accountRepository.save(account);
                                    });

                                    bankMetrics.incrementAccountCreated();
                                    bankMetrics.updateAccountCount(accountRepository.count());
                                    eventPublisher.publishAsync(
                                            AccountCreatedEvent.builder()
                                                    .accountId(savedAccount.getId())
                                                    .accountNumber(savedAccount.getAccountNumber())
                                                    .userId(savedAccount.getUserId())
                                                    .accountHolderName(savedAccount.getAccountHolderName())
                                                    .initialBalance(savedAccount.getBalance())
                                                    .build()
                                    );
                                    return new ApiResponse<AccountView>().success(
                                            AccountView.from(savedAccount),
                                            "Account Created Successfully");
                                },
                                // parameter3: Function<Exception, T> fallback
                                exception -> {
                                    log.warn("Create Account Failed: {}", exception.getMessage());
                                    return new ApiResponse<AccountView>().error("Create Account Failed");
                                }
                        );
                    }
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ApiResponse<AccountView>().error("Account creation interrupted");
        } catch (Exception e) {
            log.error("Account creation failed", e);
            return new ApiResponse<AccountView>().error("Account creation failed: " + e.getMessage());
        }

    }

    public ResponseEntity<ApiResponse<String>> transferInternal(TransferRequest transferRequest) {
        String fromAccountNumber = transferRequest.getFromAccountNumber();
        String toAccountNumber = transferRequest.getToAccountNumber();
        BigDecimal amount = transferRequest.getAmount();

        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber);
        if (fromAccount == null) {
            throw new IllegalStateException("Account with accountNumber " + fromAccountNumber + " Not Found");
        }

        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber);
        if (toAccount == null) {
            throw new IllegalStateException("Account with accountNumber " + toAccountNumber + " Not Found");
        }

        try {
            return transferLockService.executeWithTransactionLock(
                    fromAccountNumber,
                    toAccountNumber,
                    () -> {
                        Pair<List<Pair<Transaction, Account>>, String> transactionResult = txAdvice.run( () -> {
                            // [1] 송금
                            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
                            toAccount.setBalance(toAccount.getBalance().add(amount));

                            // JPA - dirty checking
                            Account fromAccountSaved = accountRepository.save(fromAccount);
                            Account toAccountSaved = accountRepository.save(toAccount);

                            // 읽기 전용 필드 최적화
                            String fromUserId = fromAccountSaved.getUserId();
                            String toUserId = toAccountSaved.getUserId();

                            userReadViewRepository.findById(fromUserId).ifPresent(userReadViewEntity -> {
                                userReadViewEntity.decreaseBalance(amount);
                                userReadViewRepository.save(userReadViewEntity);
                            });

                            userReadViewRepository.findById(toUserId).ifPresent(userReadViewEntity -> {
                                userReadViewEntity.increaseBalance(amount);
                                userReadViewRepository.save(userReadViewEntity);
                            });

                            // [2] 거래 이벤트 처리: fromAccount
                            Transaction fromTransaction = Transaction.builder()
                                    .account(fromAccount)
                                    .amount(amount)
                                    .type(TransactionType.TRANSFER)
                                    .description("Transfer from " + fromAccountNumber + " to " + toAccountNumber)
                                    .build();
                            Transaction fromTransactionSaved = transactionRepository.save(fromTransaction);
                            bankMetrics.incrementTransaction("TRANSFER");

                            // [3] 거래 이벤트 처리: toAccount
                            Transaction toTransaction = Transaction.builder()
                                    .account(toAccount)
                                    .amount(amount)
                                    .type(TransactionType.TRANSFER)
                                    .description("Transfer from " + toAccountNumber + " to " + fromAccountNumber)
                                    .build();
                            Transaction toTransactionSaved = transactionRepository.save(toTransaction);
                            bankMetrics.incrementTransaction("TRANSFER");

                            List<Pair<Transaction, Account>> resultList = new ArrayList<>();
                            resultList.add(Pair.of(fromTransactionSaved, fromAccountSaved));
                            resultList.add(Pair.of(toTransactionSaved, toAccountSaved));

                            return Pair.of(resultList, null);
                        });

                        if (transactionResult.getLeft() == null) {
                            return new ApiResponse<String>().error(transactionResult.getValue());
                        }


                        transactionResult.getLeft().forEach(pair -> {
                            Transaction transactionSaved = pair.getKey();
                            Account accountSaved = pair.getValue();

                            eventPublisher.publishAsync(
                                    TransactionCreatedEvent.builder()
                                            .transactionId(transactionSaved.getId())
                                            .accountId(accountSaved.getId())
                                            .type(transactionSaved.getType())
                                            .amount(amount)
                                            .description("Transaction Created")
                                            .balanceAfter(accountSaved.getBalance())
                                            .build()
                            );
                        });

                        return new ApiResponse<String>().success("Transfer Successful", "Transfer Successful");
                    }
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ApiResponse<String>().error("Transfer interrupted");
        } catch (Exception e) {
            log.error("Transfer failed", e);
            return new ApiResponse<String>().error("Transfer failed: " + e.getMessage());
        }

    }
}


