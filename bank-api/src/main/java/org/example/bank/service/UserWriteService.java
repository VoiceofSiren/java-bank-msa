package org.example.bank.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.common.ApiResponse;
import org.example.bank.common.CircuitBreakerUtils;
import org.example.bank.common.TxAdvice;
import org.example.bank.dto.UserView;
import org.example.bank.entity.User;
import org.example.bank.event.UserCreatedEvent;
import org.example.bank.lock.DistributedLockService;
import org.example.bank.publisher.EventPublisher;
import org.example.bank.repository.user.UserRepository;
import org.example.bank.request.UserCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserWriteService {

    private final TxAdvice txAdvice;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final UserRepository userRepository;
    private final DistributedLockService<ResponseEntity<ApiResponse<UserView>>> userLockService;
    private final EventPublisher eventPublisher;

    private final CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("userWrite");

    public ResponseEntity<ApiResponse<UserView>> createUser(UserCreateRequest userCreateRequest) {
        String email = userCreateRequest.getEmail();
        try {
            return userLockService.executeWithUserLock(
                    email,
                    () -> {
                        return CircuitBreakerUtils.execute(
                                // parameter1: CircuitBreaker circuitBreaker
                                circuitBreaker,
                                // parameter2: Supplier<T> operation
                                (() -> {
                                    User savedUser = txAdvice.run(() -> {

                                        if (userRepository.findByEmail(email) != null) {
                                            throw new IllegalStateException("User with email " + email + " already exists");
                                        }

                                        User user = User.builder()
                                                .username(userCreateRequest.getUsername())
                                                .email(email)
                                                .password(userCreateRequest.getPassword())
                                                .build();
                                        return userRepository.save(user);
                                    });

                                    eventPublisher.publishAsync(
                                            UserCreatedEvent.builder()
                                                    .userId(savedUser.getId())
                                                    .username(savedUser.getUsername())
                                                    .email(savedUser.getEmail())
                                                    .build()
                                    );
                                    return new ApiResponse<UserView>().success(
                                            UserView.from(savedUser),
                                            "User Created Successfully");
                                }),
                                // parameter3: Function<Exception, T> fallback
                                exception -> {
                                    log.warn("Create User Failed: {}", exception.getMessage());
                                    return new ApiResponse<UserView>().error("Create User Failed");
                                }
                        );
                    }
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ApiResponse<UserView>().error("User creation interrupted");
        } catch (Exception e) {
            log.error("User creation failed", e);
            return new ApiResponse<UserView>().error("User creation failed: " + e.getMessage());
        }
    }
}
