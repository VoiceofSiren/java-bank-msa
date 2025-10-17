package org.example.bank.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.common.TxAdvice;
import org.example.bank.entity.User;
import org.example.bank.entity.UserReadView;
import org.example.bank.event.UserCreatedEvent;
import org.example.bank.repository.user.UserRepository;
import org.example.bank.repository.userReadView.UserReadViewRepository;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final UserReadViewRepository userReadViewRepository;
    private final UserRepository userRepository;
    private final TxAdvice txAdvice;


    @EventListener
    @Async(value = "taskExecutor")
    @Retryable(value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000L))
    public void handleUserCreated(UserCreatedEvent event) {
        String eventType = "UserCreatedEvent";

        try {
            // runNew: @Transactional(propagation = Propagation.REQUIRES_NEW)
            txAdvice.runNew( () -> {
                // 회원 찾기
                User user = userRepository.findById(event.getUserId()).orElseThrow( () ->
                    new IllegalStateException("User with id " + event.getUserId() + " does not exist."));

                // CQRS 패턴
                UserReadView userReadView = UserReadView.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(LocalDateTime.now())
                        .accountCount(0)
                        .totalBalance(BigDecimal.ZERO)
                        .build();

                userReadViewRepository.save(userReadView);
                log.info("user created: {}", userReadView);
                return null;
            });


        }  catch (Exception e) {
            log.error("Error occurred while processing event: {}", event, e);
            throw e;
        }
    }
}
