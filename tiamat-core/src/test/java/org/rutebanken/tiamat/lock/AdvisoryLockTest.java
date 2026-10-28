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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    private DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;

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

    @Test
    public void waitingCallersDoNotKeepConnections() throws Exception {
        try (HikariDataSource pool = smallPool(2)) {
            AdvisoryLock lock = new AdvisoryLock(entityManager, pool, transactionManager);
            CountDownLatch holderHasLock = new CountDownLatch(1);
            CountDownLatch waitersStarted = new CountDownLatch(1);

            CompletableFuture<Void> holder = CompletableFuture.runAsync(() -> lock.executeInSessionLock(TEST_LOCK_NAME, 30, () -> {
                holderHasLock.countDown();
                await(waitersStarted);
                sleep(500);
                // The work needs a second connection while the waiters are waiting.
                try (Connection connection = pool.getConnection(); Statement statement = connection.createStatement()) {
                    statement.execute("select 1");
                } catch (SQLException e) {
                    throw new IllegalStateException("The holder could not get a connection while others waited", e);
                }
                return null;
            }));
            assertThat(holderHasLock.await(10, TimeUnit.SECONDS)).isTrue();

            List<CompletableFuture<Void>> waiters = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                waiters.add(CompletableFuture.runAsync(() -> lock.executeInSessionLock(TEST_LOCK_NAME, 30, () -> null)));
            }
            waitersStarted.countDown();

            holder.get(30, TimeUnit.SECONDS);
            for (CompletableFuture<Void> waiter : waiters) {
                waiter.get(30, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    public void failingWorkReleasesTheLock() {
        IllegalStateException workFailure = new IllegalStateException("work failed");

        assertThatThrownBy(() -> advisoryLock.executeInSessionLock(TEST_LOCK_NAME, 5, () -> {
            throw workFailure;
        })).isSameAs(workFailure);

        assertThat(heldLocks(TEST_LOCK_NAME)).isZero();
        assertThat(advisoryLock.executeInSessionLock(TEST_LOCK_NAME, 1, () -> "got it")).isEqualTo("got it");
    }

    @Test
    public void releasingALockThatIsNotHeldEvictsTheConnection() throws Exception {
        try (HikariDataSource pool = smallPool(1)) {
            AdvisoryLock lock = new AdvisoryLock(entityManager, pool, transactionManager);
            Connection connection = pool.getConnection();
            int backendBefore = backendPid(connection);

            IllegalStateException workFailure = new IllegalStateException("work failed");
            lock.releaseSessionLock(connection, TEST_LOCK_NAME, workFailure);

            assertThat(workFailure.getSuppressed()).singleElement()
                    .isInstanceOf(LockException.class)
                    .extracting(Throwable::getMessage).asString().contains("was not held");
            try (Connection next = pool.getConnection()) {
                assertThat(backendPid(next)).as("the connection must not go back to the pool").isNotEqualTo(backendBefore);
            }
        }
    }

    @Test
    public void failingReleaseEvictsTheConnectionAndFreesTheLock() throws Exception {
        try (HikariDataSource pool = smallPool(1)) {
            AdvisoryLock lock = new AdvisoryLock(entityManager, pool, transactionManager);
            Connection connection = pool.getConnection();
            int backendBefore = backendPid(connection);
            try (Statement statement = connection.createStatement()) {
                statement.execute("select pg_advisory_lock(" + AdvisoryLock.LOCK_NAMESPACE + ", hashtext('" + TEST_LOCK_NAME + "'))");
                // An aborted transaction makes the unlock statement fail while the session still holds the lock.
                connection.setAutoCommit(false);
                assertThatThrownBy(() -> statement.execute("select 1/0")).isInstanceOf(SQLException.class);
            }
            assertThat(heldLocks(TEST_LOCK_NAME)).isOne();

            lock.releaseSessionLock(connection, TEST_LOCK_NAME, null);

            assertThat(awaitNoHeldLocks(TEST_LOCK_NAME)).as("evicting the connection ends the session and its lock").isZero();
            try (Connection next = pool.getConnection()) {
                assertThat(backendPid(next)).isNotEqualTo(backendBefore);
            }
        }
    }

    @Test
    public void releaseFailureIsSuppressedByTheWorkFailure() {
        IllegalStateException workFailure = new IllegalStateException("work failed");

        assertThatThrownBy(() -> advisoryLock.executeInSessionLock(TEST_LOCK_NAME, 5, () -> {
            // Ends the session that holds the lock, so releasing it fails.
            terminateBackendHolding(TEST_LOCK_NAME);
            throw workFailure;
        })).isSameAs(workFailure);

        assertThat(workFailure.getSuppressed()).singleElement()
                .isInstanceOf(LockException.class)
                .extracting(Throwable::getCause).isInstanceOf(SQLException.class);
        assertThat(awaitNoHeldLocks(TEST_LOCK_NAME)).isZero();
        assertThat(advisoryLock.executeInSessionLock(TEST_LOCK_NAME, 1, () -> "got it")).isEqualTo("got it");
    }

    @Test
    public void failingEvictionIsThrownWhenTheWorkSucceeded() throws Exception {
        try (HikariDataSource pool = poolThatCannotEvict()) {
            AdvisoryLock lock = new AdvisoryLock(entityManager, pool, transactionManager);
            Connection connection = pool.getConnection();

            assertThatThrownBy(() -> lock.releaseSessionLock(connection, TEST_LOCK_NAME, null))
                    .isInstanceOf(LockCleanupException.class)
                    .satisfies(e -> assertThat(e.getSuppressed()).singleElement()
                            .extracting(Throwable::getMessage).asString().contains("was not held"));
        }
    }

    @Test
    public void failingEvictionIsSuppressedByTheWorkFailure() throws Exception {
        try (HikariDataSource pool = poolThatCannotEvict()) {
            AdvisoryLock lock = new AdvisoryLock(entityManager, pool, transactionManager);
            Connection connection = pool.getConnection();

            IllegalStateException workFailure = new IllegalStateException("work failed");
            lock.releaseSessionLock(connection, TEST_LOCK_NAME, workFailure);

            assertThat(workFailure.getSuppressed()).singleElement().isInstanceOf(LockCleanupException.class);
        }
    }

    private HikariDataSource poolThatCannotEvict() {
        return new HikariDataSource(smallPoolConfig(1)) {
            @Override
            public void evictConnection(Connection connection) {
                throw new IllegalStateException("eviction failed");
            }
        };
    }

    private HikariDataSource smallPool(int size) {
        return new HikariDataSource(smallPoolConfig(size));
    }

    private HikariConfig smallPoolConfig(int size) {
        HikariDataSource shared = (HikariDataSource) dataSource;
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(shared.getJdbcUrl());
        config.setUsername(shared.getUsername());
        config.setPassword(shared.getPassword());
        config.setMaximumPoolSize(size);
        config.setConnectionTimeout(3000);
        return config;
    }

    private int heldLocks(String lockName) {
        return queryInt("select count(*) from pg_locks where locktype = 'advisory' and granted and classid = "
                + AdvisoryLock.LOCK_NAMESPACE + " and objid = (hashtext('" + lockName + "'))::oid and objsubid = 2");
    }

    /**
     * Returns the held lock count once it is 0, or the last count after 5 seconds.
     * The server frees a session lock only when the backend ends, and that happens after the client call returns.
     */
    private int awaitNoHeldLocks(String lockName) {
        long deadline = System.currentTimeMillis() + 5000;
        int held = heldLocks(lockName);
        while (held > 0 && System.currentTimeMillis() < deadline) {
            sleep(50);
            held = heldLocks(lockName);
        }
        return held;
    }

    private void terminateBackendHolding(String lockName) {
        queryInt("select count(pg_terminate_backend(pid)) from pg_locks where locktype = 'advisory' and granted and classid = "
                + AdvisoryLock.LOCK_NAMESPACE + " and objid = (hashtext('" + lockName + "'))::oid and objsubid = 2");
    }

    private int queryInt(String sql) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static int backendPid(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("select pg_backend_pid()")) {
            resultSet.next();
            return resultSet.getInt(1);
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
