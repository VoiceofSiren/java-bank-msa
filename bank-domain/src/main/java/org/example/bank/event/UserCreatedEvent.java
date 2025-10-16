package org.example.bank.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent implements DomainEvent {

    @Getter
    private String userId;

    @Getter
    private String email;

    @Getter
    private String username;

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
