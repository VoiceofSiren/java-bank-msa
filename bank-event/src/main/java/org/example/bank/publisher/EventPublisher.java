package org.example.bank.publisher;

import org.example.bank.event.DomainEvent;

import java.util.List;

public interface EventPublisher {
    void publish(DomainEvent event);
    void publishAsync(DomainEvent event);
    void publishAll(List<DomainEvent> events);
    void publishAllAsync(List<DomainEvent> events);
}
