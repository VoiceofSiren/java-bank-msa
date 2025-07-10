package org.example.bank.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.event.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisherImpl implements EventPublisher{

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        log.info("publish event: {}", event);
        try {
            applicationEventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.error("publish event error: {}", event, e);
        }
    }

    @Async("taskExecutor")
    @Override
    public void publishAsync(DomainEvent event) {
        log.info("publish event: {}", event);
        try {
            applicationEventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.error("publish event error: {}", event, e);
        }
    }

    @Override
    public void publishAll(List<DomainEvent> events) {
        events.forEach(applicationEventPublisher::publishEvent);
    }

    @Async("taskExecutor")
    @Override
    public void publishAllAsync(List<DomainEvent> events) {
        events.forEach(applicationEventPublisher::publishEvent);
    }


}
