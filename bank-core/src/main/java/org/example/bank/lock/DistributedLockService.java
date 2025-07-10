package org.example.bank.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@EnableConfigurationProperties(LockProperties.class)
@RequiredArgsConstructor
@Slf4j
public class DistributedLockService<T> {

    private final RedissonClient redissonClient;
    private final LockProperties lockProperties;

    private T executeWithLock(String lockKey, Supplier<T> action) throws InterruptedException {
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(
                    lockProperties.getTimeout(),
                    lockProperties.getLeaseTime(),
                    TimeUnit.MILLISECONDS
            );

            if (!acquired) {
                log.error("Acquiring lock for {}", lockKey);
                throw new LockAcquisitionException("Acquiring lock for " + lockKey, null);
            }

            try {
                return action.get();
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            log.error("Lock [{}] failed to acquire lock", lockKey, e);
            throw e;
        }
    }

    public T executeWithAccountLock(String accountNumber, Supplier<T> action) throws InterruptedException {
        String lockKey = "account:lock:" + accountNumber;
        return executeWithLock(lockKey, action);
    }

    public T executeWithTransactionLock(String from, String to, Supplier<T> action) throws InterruptedException {
        List<String> sorted = Stream.of(from, to).sorted().toList();
        String lockKey = "transaction:lock:" + sorted.get(0) + sorted.get(1);
        return executeWithLock(lockKey, action);
    }
}
