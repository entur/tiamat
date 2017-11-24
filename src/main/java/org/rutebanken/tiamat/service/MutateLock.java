/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class MutateLock {

    public static final int WAIT_FOR_LOCK_SECONDS = 15;

    /**
     * Prevent too long running mutations.
     */
    public static final int LOCK_MAX_LEASE_TIME_SECONDS = 60;

    private static final Logger logger = LoggerFactory.getLogger(MutateLock.class);

    private final HazelcastInstance hazelcastInstance;
    private final int waitTimeoutSeconds;
    public static final String LOCK_NAME = "mutate-lock";


    @Autowired
    public MutateLock(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
        this.waitTimeoutSeconds = WAIT_FOR_LOCK_SECONDS;
    }

    public MutateLock(HazelcastInstance hazelcastInstance, int waitTimeoutSeconds) {
        this.hazelcastInstance = hazelcastInstance;
        this.waitTimeoutSeconds = waitTimeoutSeconds;
    }

    public <T> T executeInLock(Supplier<T> supplier) {

        final ILock lock = hazelcastInstance.getLock(LOCK_NAME);

        try {
            logger.info("Waiting for mutation lock {}", LOCK_NAME);
            if (lock.tryLock(waitTimeoutSeconds, TimeUnit.SECONDS, LOCK_MAX_LEASE_TIME_SECONDS, TimeUnit.SECONDS)) {
                long started = System.currentTimeMillis();
                try {
                    logger.info("Got mutation lock {}", LOCK_NAME);
                    return supplier.get();
                } finally {
                    try {
                        lock.unlock();
                    } catch (IllegalMonitorStateException ex) {
                        long timeSpent = System.currentTimeMillis()-started;
                        logger.warn("Could not unlock '{}'. Lease time could have been exeeded. Time spent {}ms",
                                LOCK_NAME, timeSpent, ex);
                    }
                }
            } else {
                throw new MutateLockException("Timed out waiting to aquire lock " + LOCK_NAME + " after " + waitTimeoutSeconds + " seconds");
            }
        } catch (InterruptedException e) {
            throw new MutateLockException("Interrupted while waiting for lock: " + LOCK_NAME, e);
        }
    }

}
