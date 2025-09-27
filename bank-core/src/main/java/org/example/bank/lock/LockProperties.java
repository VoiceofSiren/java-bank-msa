package org.example.bank.lock;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bank.lock")
@Getter
public class LockProperties {
    // 락을 획득하기 위해 기다릴 최대 시간
    private Long timeout = 5000L;
    // 락을 점유할 최대 시간
    private Long leaseTime = 10000L;
    // 락 획득 재시도 간격
    private Long retryInterval = 100L;
    // 락 획득 시 재시도할 최대 횟수
    private Long maxRetryAttempts = 50L;
}
