package org.example.bank.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.common.TxAdvice;
import org.example.bank.entity.Account;
import org.example.bank.entity.AccountReadView;
import org.example.bank.event.AccountCreatedEvent;
import org.example.bank.event.TransactionCreatedEvent;
import org.example.bank.metrics.BankMetrics;
import org.example.bank.repository.account.AccountRepository;
import org.example.bank.repository.accountReadView.AccountReadViewRepository;
import org.example.bank.repository.transaction.TransactionRepository;
import org.example.bank.repository.transactionReadView.TransactionReadViewRepository;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    private final AccountReadViewRepository accountReadViewRepository;
    private final TransactionReadViewRepository transactionReadViewRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BankMetrics bankMetrics;
    private final TxAdvice txAdvice;

    @EventListener
    @Async(value = "taskExecutor")
    @Retryable(value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000L))
    public void handleAccountCreated(AccountCreatedEvent event) {
        Instant startTime = Instant.now();
        String eventType = "AccountCreatedEvent";
        log.info("event received: {}", eventType);

        try {
            // runNew: @Transactional(propagation = Propagation.REQUIRES_NEW)
            txAdvice.runNew( () -> {
                // 계좌 찾기
                Account account = accountRepository.findById(event.getAccountId()).orElseThrow(() ->
                        new IllegalStateException("Account with id " + event.getAccountId() + " Not Found"));

                // CQRS 패턴
                AccountReadView accountReadView = AccountReadView.builder()
                        .id(account.getId())
                        .accountNumber(account.getAccountNumber())
                        .accountHolderName(account.getAccountHolderName())
                        .balance(account.getBalance())
                        .createdAt(account.getCreatedAt())
                        .updatedAt(LocalDateTime.now())
                        .transactionCount(0)
                        .totalDeposits(BigDecimal.ZERO)
                        .totalWithdrawals(BigDecimal.ZERO)
                        .build();

                accountReadViewRepository.save(accountReadView);
                log.info("account created: {}", accountReadView);
                return null;
            });

            // 로직 성공 시 처리에 소요된 시간을 계산하여 기록
            Duration duration = Duration.between(startTime, Instant.now());
            bankMetrics.recordEventProcessingTime(duration, eventType);
            bankMetrics.incrementEventSuccess(eventType);
        } catch (Exception e) {
            log.error("Error occurred while processing event: {}", event, e);
            bankMetrics.incrementEventFailed(eventType);
            throw e;
        }
    }

    @EventListener
    @Async(value = "taskExecutor")
    @Retryable(value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000L))
    public void handleTransactionCreated(TransactionCreatedEvent event) {
        Instant startTime = Instant.now();
        String eventType = "TransactionCreatedEvent";
        log.info("event received: {}", eventType);

        try {
            // runNew: @Transactional(propagation = Propagation.REQUIRES_NEW)
            txAdvice.runNew(() -> {
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
