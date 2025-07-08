package org.example.bank.event;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class AccountCreatedEvent implements DomainEvent {

    @Getter
    private final Long accountId;

    @Getter
    private final String accountNumber;

    @Getter
    private final String accountHolderName;

    @Getter
    private final BigDecimal initialBalance;

    private final LocalDateTime occurredOn;

    private final String eventId;

    public AccountCreatedEvent(Long accountId, String accountNumber, String accountHolderName, BigDecimal initialBalance) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.initialBalance = initialBalance;
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