package org.example.bank.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bank.common.ApiResponse;
import org.example.bank.common.CircuitBreakerUtils;
import org.example.bank.common.TxAdvice;
import org.example.bank.dto.UserView;
import org.example.bank.entity.UserReadView;
import org.example.bank.repository.userReadView.UserReadViewRepository;
import org.example.bank.request.UserLoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserReadService {
    private final TxAdvice txAdvice;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final UserReadViewRepository userReadViewRepository;

    private final CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("userRead");

    public ResponseEntity<ApiResponse<UserView>> getAuth(UserLoginRequest userLoginRequest) {
        String email = userLoginRequest.getEmail();
        return CircuitBreakerUtils.execute(
                circuitBreaker,
                () -> txAdvice.readOnly(() -> {
                    Optional<UserReadView> response = userReadViewRepository.findByEmail(email);

                    if (response.isEmpty()) {
                        return new ApiResponse<UserView>().error("User Not Found");
                    } else {
                        return new ApiResponse<UserView>().success(
                                UserView.fromReadView(response.get())
                        );
                    }
                }),
                exception -> {
                    log.warn("Get User Failed: {}", exception.getMessage());
                    return new ApiResponse<UserView>().error("Get User Failed");
                }
        );
    }


}
