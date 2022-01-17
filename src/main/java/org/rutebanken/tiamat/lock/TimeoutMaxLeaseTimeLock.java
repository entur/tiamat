package org.rutebanken.tiamat.lock;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * A generic lock that waits for aquiring lock with a timeout. If the lock was aquired, there is a timeout for maximum lease time.
 * Current implementation is using hazelcast.
 */
@Component
public class TimeoutMaxLeaseTimeLock {

    public static final int DEFAULT_WAIT_FOR_LOCK_SECONDS = 15;

    public static final int DEFAULT_LOCK_MAX_LEASE_TIME_SECONDS = 60;

    private static final Logger logger = LoggerFactory.getLogger(TimeoutMaxLeaseTimeLock.class);

    private final HazelcastInstance hazelcastInstance;


    @Autowired
    public TimeoutMaxLeaseTimeLock(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    public <T> T executeInLock(Supplier<T> supplier, String lockName) {
        return executeInLock(supplier, lockName, DEFAULT_WAIT_FOR_LOCK_SECONDS, DEFAULT_LOCK_MAX_LEASE_TIME_SECONDS);
    }

    public <T> T executeInLock(Supplier<T> supplier, String lockName, int waitTimeoutSeconds, int maxLeaseTimeSeconds) {

        final ILock lock = hazelcastInstance.getLock(lockName);

        try {
            logger.info("Waiting for lock {}", lockName);
            if (lock.tryLock(waitTimeoutSeconds, TimeUnit.SECONDS, maxLeaseTimeSeconds, TimeUnit.SECONDS)) {
                long started = System.currentTimeMillis();
                try {
                    logger.info("Got lock {}", lockName);
                    return supplier.get();
                } finally {
                    try {
                        logger.info("Unlocking {}", lockName);
                        lock.unlock();
                    } catch (IllegalMonitorStateException ex) {
                        long timeSpent = System.currentTimeMillis() - started;
                        logger.warn("Could not unlock '{}'. Lease time could have been exeeded. Time spent {}ms",
                                lockName, timeSpent, ex);
                    }
                }
            } else {
                throw new LockException("Timed out waiting to aquire lock " + lockName + " after " + waitTimeoutSeconds + " seconds");
            }
        } catch (InterruptedException e) {
            throw new LockException("Interrupted while waiting for lock: " + lockName, e);
        }
    }

}

