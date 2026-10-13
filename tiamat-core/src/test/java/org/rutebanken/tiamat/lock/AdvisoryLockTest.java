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

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AdvisoryLockTest extends TiamatIntegrationTest {

    private static final String TEST_LOCK_NAME = "test-lock-name";

    @Autowired
    private AdvisoryLock advisoryLock;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    public void sessionLockWaitsForTheHolder() throws Exception {
        long holdMillis = 1000;
        CountDownLatch holderHasLock = new CountDownLatch(1);
        AtomicLong holderReleasedAt = new AtomicLong();

        CompletableFuture<Void> holder = CompletableFuture.runAsync(() -> advisoryLock.executeInSessionLock(TEST_LOCK_NAME, 5, () -> {
            holderHasLock.countDown();
            sleep(holdMillis);
            holderReleasedAt.set(System.currentTimeMillis());
            return null;
        }));
        assertThat(holderHasLock.await(10, TimeUnit.SECONDS)).isTrue();

        long gotLockAt = advisoryLock.executeInSessionLock(TEST_LOCK_NAME, 5, System::currentTimeMillis);

        holder.get(10, TimeUnit.SECONDS);
        assertThat(gotLockAt).isGreaterThanOrEqualTo(holderReleasedAt.get());
    }

    @Test
    public void sessionLockTimesOutWhileHeld() throws Exception {
        CountDownLatch holderHasLock = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        CompletableFuture<Void> holder = CompletableFuture.runAsync(() -> advisoryLock.executeInSessionLock(TEST_LOCK_NAME, 5, () -> {
            holderHasLock.countDown();
            await(release);
            return null;
        }));
        assertThat(holderHasLock.await(10, TimeUnit.SECONDS)).isTrue();

        try {
            assertThatThrownBy(() -> advisoryLock.executeInSessionLock(TEST_LOCK_NAME, 1, () -> null))
                    .isInstanceOf(LockException.class)
                    .hasMessageContaining("Timed out waiting to aquire lock " + TEST_LOCK_NAME);
        } finally {
            release.countDown();
            holder.get(10, TimeUnit.SECONDS);
        }
    }

    @Test
    public void transactionLockIsHeldUntilTheTransactionCommits() throws Exception {
        long workAfterLockMillis = 1000;
        CountDownLatch holderHasLock = new CountDownLatch(1);
        AtomicLong holderCommittingAt = new AtomicLong();
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        CompletableFuture<Void> holder = CompletableFuture.runAsync(() -> transactionTemplate.executeWithoutResult(status -> {
            advisoryLock.executeInTransactionLock(TEST_LOCK_NAME, 5, () -> {
                holderHasLock.countDown();
                return null;
            });
            // The supplier has returned, but the transaction is still open: the lock must stay taken.
            sleep(workAfterLockMillis);
            holderCommittingAt.set(System.currentTimeMillis());
        }));
        assertThat(holderHasLock.await(10, TimeUnit.SECONDS)).isTrue();

        long gotLockAt = transactionTemplate.execute(status ->
                advisoryLock.executeInTransactionLock(TEST_LOCK_NAME, 5, System::currentTimeMillis));

        holder.get(10, TimeUnit.SECONDS);
        assertThat(gotLockAt).isGreaterThanOrEqualTo(holderCommittingAt.get());
    }

    @Test
    public void transactionLockStartsATransactionWhenNoneIsActive() {
        boolean transactionActive = advisoryLock.executeInTransactionLock(TEST_LOCK_NAME, 5,
                TransactionSynchronizationManager::isActualTransactionActive);

        assertThat(transactionActive).isTrue();
    }

    @Test
    public void transactionLockAndSessionLockExcludeEachOther() throws Exception {
        CountDownLatch holderHasLock = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        CompletableFuture<Void> holder = CompletableFuture.runAsync(() -> advisoryLock.executeInSessionLock(TEST_LOCK_NAME, 5, () -> {
            holderHasLock.countDown();
            await(release);
            return null;
        }));
        assertThat(holderHasLock.await(10, TimeUnit.SECONDS)).isTrue();

        try {
            assertThatThrownBy(() -> advisoryLock.executeInTransactionLock(TEST_LOCK_NAME, 1, () -> null))
                    .isInstanceOf(LockException.class);
        } finally {
            release.countDown();
            holder.get(10, TimeUnit.SECONDS);
        }
    }

    @Test
    public void locksWithDifferentNamesDoNotBlockEachOther() throws Exception {
        CountDownLatch holderHasLock = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        CompletableFuture<Void> holder = CompletableFuture.runAsync(() -> advisoryLock.executeInSessionLock(TEST_LOCK_NAME, 5, () -> {
            holderHasLock.countDown();
            await(release);
            return null;
        }));
        assertThat(holderHasLock.await(10, TimeUnit.SECONDS)).isTrue();

        try {
            String result = advisoryLock.executeInSessionLock("another-lock-name", 1, () -> "got it");
            assertThat(result).isEqualTo("got it");
        } finally {
            release.countDown();
            holder.get(10, TimeUnit.SECONDS);
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
