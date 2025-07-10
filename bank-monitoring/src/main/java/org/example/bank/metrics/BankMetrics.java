package org.example.bank.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

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

}
