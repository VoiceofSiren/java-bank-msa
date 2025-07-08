package org.example.bank.event;

import lombok.Getter;
import org.example.bank.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionCreatedEvent implements DomainEvent {

    @Getter
    private final Long transactionId;

    @Getter
    private final Long accountId;

    @Getter
    private final TransactionType type;

    @Getter
    private final BigDecimal amount;

    @Getter
    private final String description;

    @Getter
    private final BigDecimal balanceAfter;

    private final LocalDateTime occurredOn;

    private final String eventId;

    public TransactionCreatedEvent(Long transactionId, Long accountId, TransactionType type, BigDecimal amount, String description, BigDecimal balanceAfter) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.balanceAfter = balanceAfter;
        this.occurredOn = LocalDateTime.now();
        this.eventId = UUID.randomUUID().toString();
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

}