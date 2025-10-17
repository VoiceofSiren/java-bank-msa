package org.example.bank.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreatedEvent implements DomainEvent {

    @Getter
    private Long accountId;

    @Getter
    private String accountNumber;

    @Getter
    private String userId;

    @Getter
    private String accountHolderName;

    @Getter
    private BigDecimal initialBalance;

    private final LocalDateTime occurredOn = LocalDateTime.now();

    private final String eventId = UUID.randomUUID().toString();


    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

}