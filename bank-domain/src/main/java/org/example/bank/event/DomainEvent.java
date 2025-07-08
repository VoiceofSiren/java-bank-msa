package org.example.bank.event;

import java.time.LocalDateTime;

// 기본 도메인 이벤트 인터페이스
public interface DomainEvent {
    LocalDateTime getOccurredOn();
    String getEventId();
}
