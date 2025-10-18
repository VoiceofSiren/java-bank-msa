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
import org.example.bank.jwt.JwtConstants;
import org.example.bank.jwt.JwtUtils;
import org.example.bank.jwt.RefreshTokenService;
import org.example.bank.jwt.TokenResponse;
import org.example.bank.repository.userReadView.UserReadViewRepository;
import org.example.bank.request.UserLoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserReadService {
    private final TxAdvice txAdvice;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final UserReadViewRepository userReadViewRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    private final CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("userRead");
    private final RefreshTokenService refreshTokenService;

    public ResponseEntity<ApiResponse<TokenResponse>> getAuth(UserLoginRequest userLoginRequest) {
        String email = userLoginRequest.getEmail();
        String password = userLoginRequest.getPassword();
        return CircuitBreakerUtils.execute(
                circuitBreaker,
                () -> txAdvice.readOnly(() -> {
                    Optional<UserReadView> userReadView = userReadViewRepository.findByEmail(email);

                    if (userReadView.isEmpty() ||
                        !passwordEncoder.matches(password, userReadView.get().getPassword())
                    ) {
                        return new ApiResponse<TokenResponse>().error("User Not Found");
                    } else {

                        String accessToken = jwtUtils.issueAccessToken(userReadView.get().getId(),
                                                                        userReadView.get().getUsername(),
                                                                        userReadView.get().getEmail(),
                                                                        JwtConstants.ACCESS_TOKEN_EXPIRATION);
                        String refreshToken = UUID.randomUUID().toString();

                        refreshTokenService.saveRefreshToken(refreshToken, userReadView.get().getUsername());

                        return new ApiResponse<TokenResponse>().success(
                                new TokenResponse(accessToken, refreshToken)
                        );
                    }
                }),
                exception -> {
                    log.warn("Get User Failed: {}", exception.getMessage());
                    return new ApiResponse<TokenResponse>().error("Get User Failed");
                }
        );
    }


    public ResponseEntity<ApiResponse<UserReadView>> getUserInfo(String userId) {
        return CircuitBreakerUtils.execute(
                circuitBreaker,
                () -> txAdvice.readOnly(() -> {
                    Optional<UserReadView> userReadView = userReadViewRepository.findById(userId);

                    if (userReadView.isEmpty()) {
                        return new ApiResponse<UserReadView>().error("User Not Found");
                    } else {
                        return new ApiResponse<UserReadView>().success(userReadView.get());
                    }
                }),
                exception -> {
                    log.warn("Get User Failed: {}", exception.getMessage());
                    return new ApiResponse<UserReadView>().error("Get User Failed");
                }
        );
    }
}
