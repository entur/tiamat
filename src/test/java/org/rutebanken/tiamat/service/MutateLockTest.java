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
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.service.MutateLock.LOCK_MAX_LEASE_TIME_SECONDS;
import static org.rutebanken.tiamat.service.MutateLock.WAIT_FOR_LOCK_SECONDS;


public class MutateLockTest extends TiamatIntegrationTest {

    private static final int WAIT_AFTER_THREAD_START_MILLIS = 10;

    @Autowired
    private HazelcastInstance hazelcastInstance;


    @Test
    public void testWaitingForLock() throws InterruptedException {
        MutateLock mutateLock = new MutateLock(hazelcastInstance);

        long sleep = 1000;

        Thread t1 = new Thread(() -> {
            mutateLock.executeInLock(() -> {

                try {
                    System.out.println("Sleeping");
                    Thread.sleep(sleep);
                    System.out.println("Slept");
                    return null;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });
        });
        long started = System.currentTimeMillis();
        t1.start();
        Thread.sleep(WAIT_AFTER_THREAD_START_MILLIS);
        long gotLock = mutateLock.executeInLock(() -> System.currentTimeMillis());

        long waited = gotLock - started;
        assertThat(waited).isGreaterThanOrEqualTo(sleep);
    }

    @Test(expected = MutateLockException.class)
    public void testWaitingForLocktTimeout() throws InterruptedException {

        int waitTimeoutSeconds = 1;
        MutateLock mutateLock = new MutateLock(hazelcastInstance, waitTimeoutSeconds);

        long waitTimeoutMillis = waitTimeoutSeconds * 1000;
        long sleep = waitTimeoutMillis + 100;

        Thread t1 = new Thread(() -> {
            mutateLock.executeInLock(() -> {
                try {
                    System.out.println("Sleeping " + sleep);
                    Thread.sleep(sleep);
                    System.out.println("Slept " + sleep);
                    return null;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });
        });
        long started = System.currentTimeMillis();
        t1.start();
        Thread.sleep(WAIT_AFTER_THREAD_START_MILLIS);

        long gotLock = mutateLock.executeInLock(() -> System.currentTimeMillis());

        long waited = gotLock - started;
        assertThat(waited).as("waited for lock millis").isGreaterThanOrEqualTo(waitTimeoutSeconds);
    }
}