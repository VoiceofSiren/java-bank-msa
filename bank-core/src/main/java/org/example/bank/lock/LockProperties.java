package org.example.bank.lock;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bank.lock")
@Getter
public class LockProperties {
    private Long timeout = 5000L;
    private Long leaseTime = 10000L;
    private Long retryInterval = 100L;
    private Long maxRetryAttempts = 50L;
}
