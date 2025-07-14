package org.example.bank.common;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import java.util.function.Function;
import java.util.function.Supplier;

public class CircuitBreakerUtils {

    // **Utils 클래스: 인스턴스화 방지
    private CircuitBreakerUtils() {
    }

    public static <T> T execute(CircuitBreaker circuitBreaker,
                     Supplier<T> operation,
                     Function<Exception, T> fallback) {
        try {
            return CircuitBreaker
                    .decorateSupplier(circuitBreaker, operation)
                    .get();
        } catch (Exception e) {
            return fallback
                    .apply(e);
        }
    }
}
