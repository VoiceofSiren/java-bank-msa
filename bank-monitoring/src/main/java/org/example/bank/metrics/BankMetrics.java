package org.example.bank.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class BankMetrics {

    private final MeterRegistry meterRegistry;
    private final AtomicLong accountGauge = new AtomicLong(0);

    public BankMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        Gauge
                .builder("bank.account.total",
                accountGauge,
                AtomicLong::get)
                .register(meterRegistry);
    }

    public void incrementAccountCreated() {
        Counter.builder("bank.account.created")
                .description("Number of accounts created")
                .register(meterRegistry)
                .increment();
    }

    public void updateAccountCount(Long count) {
        accountGauge.set(count);
    }

    public void incrementTransaction(String type) {
        Counter.builder("bank.transaction.count")
                .tag("type", type)
                .description("Number of transactions")
                .register(meterRegistry)
                .increment();
    }

    public void recordTransactionAmount(BigDecimal amount, String type) {
        DistributionSummary.builder("bank.transaction.amount")
                .tag("type", type)
                .description("Transaction amounts distribution")
                .register(meterRegistry)
                .record(amount.toBigInteger().doubleValue());
    }

    public void incrementEventSuccess(String eventType) {
        Counter.builder("bank.event.success")
                .tag("type", eventType)
                .tag("status", "success")
                .description("Number of events succeeded")
                .register(meterRegistry)
                .increment();
    }

    public void incrementEventFailed(String eventType) {
        Counter.builder("bank.event.failed")
                .tag("type", eventType)
                .tag("status", "failed")
                .description("Number of events failed")
                .register(meterRegistry)
                .increment();
    }

    public void recordEventProcessingTime(Duration duration, String eventType) {
        Timer.builder("bank.event.processing.time")
                .tag("type", eventType)
                .description("Event processing time")
                .register(meterRegistry)
                .record(duration);
    }

    public void incrementLockAcquisitionSuccess(String lockKey) {
        Counter.builder("bank.lock.acquisition.success")
                .description("Number of lock acquisition successes")
                .tag("lockKey", lockKey)
                .register(meterRegistry)
                .increment();
    }

    public void incrementLockAcquisitionFailure(String lockKey) {
        Counter.builder("bank.lock.acquisition.failed")
                .description("Number of lock acquisition failures")
                .tag("lockKey", lockKey)
                .register(meterRegistry)
                .increment();
    }

    public void recordApiResponseTime(Duration duration, String endpoint, String method) {
        Timer.builder("bank.api.response.time")
                .description("API response time")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .register(meterRegistry)
                .record(duration);
    }

}
