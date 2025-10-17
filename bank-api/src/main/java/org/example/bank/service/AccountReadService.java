package org.example.bank.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.common.ApiResponse;
import org.example.bank.common.CircuitBreakerUtils;
import org.example.bank.common.TxAdvice;
import org.example.bank.dto.AccountView;
import org.example.bank.dto.TransactionView;
import org.example.bank.entity.AccountReadView;
import org.example.bank.entity.TransactionReadView;
import org.example.bank.repository.accountReadView.AccountReadViewRepository;
import org.example.bank.repository.transactionReadView.TransactionReadViewRepository;
import org.springdoc.core.service.OperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountReadService {
    private final TxAdvice txAdvice;
    private final AccountReadViewRepository accountReadViewRepository;
    private final TransactionReadViewRepository transactionReadViewRepository;
    private final CircuitBreakerRegistry circuitBreakerRegistry;;
    private final OperationService operationService;

    private final CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("accountRead");

    public ResponseEntity<ApiResponse<AccountView>> getAccount(String accountNumber) {
        return CircuitBreakerUtils.execute(
                circuitBreaker,
                () -> txAdvice.readOnly(() -> {
                    Optional<AccountReadView> response = accountReadViewRepository.findByAccountNumber(accountNumber);

                    if (response.isEmpty()) {
                        return new ApiResponse<AccountView>().error("Account Not Found");
                    } else {
                        return new ApiResponse<AccountView>().success(
                                AccountView.fromReadView(response.get())
                        );
                    }
                }),
                exception -> {
                    log.warn("Get Account Failed: {}", exception.getMessage());
                    return new ApiResponse<AccountView>().error("Get Account Failed");
                }
        );
    }

    public ResponseEntity<ApiResponse<List<TransactionView>>> transactionHistory(String accountNumber, Integer limit) {
        return CircuitBreakerUtils.execute(
                circuitBreaker,
                () -> (ResponseEntity<ApiResponse<List<TransactionView>>>) txAdvice.readOnly(() -> {
                    Optional<AccountReadView> accountReadViewEntity = accountReadViewRepository.findByAccountNumber(accountNumber);

                    if (accountReadViewEntity.isEmpty()) {
                        return new ApiResponse<List<TransactionView>>().error("Account Not Found");
                    }

                    List<TransactionReadView> response = null;
                    if (limit != null) {
                        response = transactionReadViewRepository.findByAccountNumberOrderByCreatedAtDesc(accountNumber)
                                .stream()
                                .limit(limit)
                                .toList();
                    } else {
                        response = transactionReadViewRepository.findByAccountNumberOrderByCreatedAtDesc(accountNumber);
                    }
                    return new ApiResponse<List<TransactionView>>().success(
                            response.stream()
                                    .map(TransactionView::fromReadView)
                                    .toList());
                }),
                exception -> {
                    log.warn("Get Transaction History Failed: {}", exception.getMessage());
                    return new ApiResponse<List<TransactionView>>().error("Get Transaction History Failed");
                }
        );
    }

    public ResponseEntity<ApiResponse<List<AccountView>>> getAllAccounts() {
        return CircuitBreakerUtils.execute(
                circuitBreaker,
                () -> (ResponseEntity<ApiResponse<List<AccountView>>>) txAdvice.readOnly(() -> {
                    List<AccountView> response = accountReadViewRepository.findAll()
                            .stream()
                            .map(AccountView::fromReadView)
                            .toList();
                    return new ApiResponse<List<AccountView>>().success(response);
                }),
                exception -> {
                    log.warn("Get All Accounts Failed: {}", exception.getMessage());
                    return new ApiResponse<List<AccountView>>().error("Get All Accounts Failed");
                }
        );
    }

}
