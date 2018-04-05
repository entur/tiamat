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

package org.rutebanken.tiamat.lock;

import com.hazelcast.core.HazelcastInstance;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;


public class TimeoutMaxLeaseTimeLockTest extends TiamatIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(TimeoutMaxLeaseTimeLockTest.class);

    private static final String TEST_LOCK_NAME = "test-lock-name";

    @Autowired
    private HazelcastInstance hazelcastInstance;


    @Test
    public void testWaitingForLock() throws InterruptedException {
        TimeoutMaxLeaseTimeLock lock = new TimeoutMaxLeaseTimeLock(hazelcastInstance);

        long sleep = 1000;

        AtomicBoolean threadGotLock = new AtomicBoolean(false);
        Thread t1 = new Thread(() -> {
            lock.executeInLock(() -> {
                threadGotLock.set(true);
                try {
                    logger.info("Sleeping for " + sleep + " millis");
                    Thread.sleep(sleep);
                    logger.info("Slept" + sleep + " millis");
                    return null;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }, TEST_LOCK_NAME);
        });
        long started = System.currentTimeMillis();
        t1.start();
        // Make sure the thread gets the lock first
        while (!threadGotLock.get()) {
        }
        long gotLock = lock.executeInLock(() -> System.currentTimeMillis(), TEST_LOCK_NAME);

        long waited = gotLock - started;
        Assertions.assertThat(waited)
                .as("waited ms")
                .isGreaterThanOrEqualTo(sleep)
                .as("ms thread slept within lock");
    }

    @Test(expected = LockException.class)
    public void testWaitingForLocktTimeout() throws InterruptedException {

        int waitTimeoutSeconds = 1;
        TimeoutMaxLeaseTimeLock timeoutMaxLeaseTimeLock = new TimeoutMaxLeaseTimeLock(hazelcastInstance);

        // Sleep more than the wait time to trigger exception
        long sleep = (waitTimeoutSeconds * 3 * 1000);
        AtomicBoolean threadGotLock = new AtomicBoolean(false);

        Thread t1 = new Thread(() -> {
            timeoutMaxLeaseTimeLock.executeInLock(() -> {
                try {
                    threadGotLock.set(true);
                    logger.info("Sleeping " + sleep + " millis");
                    Thread.sleep(sleep);
                    logger.info("Slept " + sleep + " millis");
                    return null;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }, TEST_LOCK_NAME, waitTimeoutSeconds, 10);
        });

        t1.start();

        logger.info("thread started");

        logger.info("Make sure the thread gets the lock first");
        while (!threadGotLock.get()) {
        }
        logger.info("Thread did get the lock");

        logger.info("expecting exception");
        // Should throw exception because the wait time was too long
        timeoutMaxLeaseTimeLock.executeInLock(() -> System.currentTimeMillis(), TEST_LOCK_NAME, waitTimeoutSeconds, 10);
    }
}