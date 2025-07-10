package org.example.bank.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.event.AccountCreatedEvent;
import org.example.bank.event.TransactionCreatedEvent;
import org.example.bank.repository.account.AccountRepository;
import org.example.bank.repository.accountReadView.AccountReadViewRepository;
import org.example.bank.repository.transaction.TransactionRepository;
import org.example.bank.repository.transactionReadView.TransactionReadViewRepository;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    private final AccountReadViewRepository accountReadViewRepository;
    private final TransactionReadViewRepository transactionReadViewRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    // TODO -> metrics, txAdvice

    @EventListener
    @Async(value = "taskExecutor")
    @Retryable(value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000L))
    public void handleAccountCreated(AccountCreatedEvent event) {
        Instant startTime = Instant.now();
        String eventType = "AccountCreatedEvent";
        log.info("event received: {}", event);

    }

    @EventListener
    @Async(value = "taskExecutor")
    @Retryable(value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000L))
    public void handleTransactionCreated(TransactionCreatedEvent event) {
        Instant startTime = Instant.now();
        String eventType = "TransactionCreatedEvent";
        log.info("event received: {}", event);

    }
}
