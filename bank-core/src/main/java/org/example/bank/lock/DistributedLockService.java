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
/*
분산락
    1. Lettuce: Key가 있는지 주기적으로 확인
        - 일반적으로 세밀한 제어가 가능하지만, 에러 핸들링과 재시도 로직을 개발자가 직접 관리해야 함
    2. Redisson:
        - 다중 노드에서도 안정적이며, 락 자동 해제, 락 연장 기능까지 제공
 */
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
                    lockProperties.getTimeout(),    // 5 seconds
                    lockProperties.getLeaseTime(),  // 10 seconds
                    TimeUnit.MILLISECONDS
            );

            // lock 미획득 시 에러 처리
            if (!acquired) {
                log.error("Acquiring lock for {}", lockKey);
                throw new LockAcquisitionException("Acquiring lock for " + lockKey, null);
            }

            try {
                return action.get();
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    // lock 사용 여부에 관계 없이 unlock 처리
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
        // 데드락 방지를 위하여 정렬
        List<String> sorted = Stream.of(from, to).sorted().toList();
        String lockKey = "transaction:lock:" + sorted.get(0) + sorted.get(1);
        return executeWithLock(lockKey, action);
    }
}
