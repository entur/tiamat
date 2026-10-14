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

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Named locks that all Tiamat instances share, backed by PostgreSQL advisory locks.
 * <p>
 * A transaction lock is held until the surrounding transaction commits or rolls back. The next
 * holder therefore always reads the committed state. Use it for work that runs in one transaction.
 * <p>
 * A session lock is held on a connection of its own until the work returns. Use it for work that
 * runs in several transactions or threads, such as imports and background jobs. A caller that
 * waits for a session lock holds no connection between attempts, so waiting callers cannot use up
 * the connection pool. A session lock is not reentrant: taking the same lock again inside the work
 * waits for itself.
 * <p>
 * PostgreSQL releases both kinds when the database session ends, so a crashed instance does not
 * keep a lock. A pooled connection keeps its session open after it is returned, so a session lock
 * that cannot be released normally is released by evicting its connection from the pool.
 */
@Component
public class AdvisoryLock {

    private static final Logger logger = LoggerFactory.getLogger(AdvisoryLock.class);

    /**
     * First key of every Tiamat lock ("tiam" in ASCII). Other users of advisory locks in the same
     * database cannot take one of these by accident.
     */
    public static final int LOCK_NAMESPACE = 0x7469616d;

    private static final long FIRST_POLL_INTERVAL_MILLIS = 10;
    private static final long MAX_POLL_INTERVAL_MILLIS = 200;

    private final EntityManager entityManager;
    private final DataSource dataSource;
    private final TransactionTemplate transactionTemplate;

    public AdvisoryLock(EntityManager entityManager, DataSource dataSource, PlatformTransactionManager transactionManager) {
        this.entityManager = entityManager;
        this.dataSource = dataSource;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * Runs the supplier while holding the lock until the current transaction ends. Without an
     * active transaction, a new one is started for the supplier.
     *
     * @throws LockException if the lock is not free within the wait timeout
     */
    public <T> T executeInTransactionLock(String lockName, int waitTimeoutSeconds, Supplier<T> supplier) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            return transactionTemplate.execute(status -> executeInTransactionLock(lockName, waitTimeoutSeconds, supplier));
        }
        Session session = entityManager.unwrap(Session.class);
        logger.info("Waiting for lock {}", lockName);
        awaitLock(lockName, waitTimeoutSeconds,
                () -> session.doReturningWork(connection -> tryLock(connection, "pg_try_advisory_xact_lock", lockName)));
        logger.info("Got lock {} until the transaction ends", lockName);
        return supplier.get();
    }

    /**
     * Runs the supplier while holding the lock on a connection of its own. Waits as long as it
     * takes to get the lock.
     */
    public <T> T executeInSessionLock(String lockName, Supplier<T> supplier) {
        return executeInSessionLock(lockName, Integer.MAX_VALUE, supplier);
    }

    /**
     * Runs the supplier while holding the lock on a connection of its own.
     * <p>
     * If the supplier throws, its exception is rethrown, with any failure to release the lock
     * attached as a suppressed exception.
     *
     * @throws LockException if the lock is not free within the wait timeout
     * @throws LockCleanupException if the lock could not be released, and the session holding it may still be open
     */
    public <T> T executeInSessionLock(String lockName, int waitTimeoutSeconds, Supplier<T> supplier) {
        logger.info("Waiting for lock {}", lockName);
        Connection connection = awaitSessionLock(lockName, waitTimeoutSeconds);
        logger.info("Got lock {}", lockName);
        T result;
        try {
            result = supplier.get();
        } catch (RuntimeException | Error workFailure) {
            releaseSessionLock(connection, lockName, workFailure);
            throw workFailure;
        }
        releaseSessionLock(connection, lockName, null);
        return result;
    }

    /**
     * Borrows a connection for each attempt and returns it to the pool when the lock is taken by
     * someone else, so that waiting does not hold a connection.
     *
     * @return the connection that holds the lock
     */
    private Connection awaitSessionLock(String lockName, int waitTimeoutSeconds) {
        AtomicReference<Connection> lockHolder = new AtomicReference<>();
        awaitLock(lockName, waitTimeoutSeconds, () -> {
            Connection connection = dataSource.getConnection();
            try {
                // Outside a transaction, the connection does not sit idle in transaction while the work runs.
                connection.setAutoCommit(true);
                if (tryLock(connection, "pg_try_advisory_lock", lockName)) {
                    lockHolder.set(connection);
                    return true;
                }
            } catch (SQLException | RuntimeException e) {
                // The attempt may have taken the lock before it failed, so the session must not return to the pool.
                try {
                    evict(connection, lockName);
                } catch (LockCleanupException cleanupFailure) {
                    cleanupFailure.addSuppressed(e);
                    throw cleanupFailure;
                }
                throw e;
            }
            connection.close();
            return false;
        });
        return lockHolder.get();
    }

    /**
     * Releases a session lock and returns its connection to the pool. If the lock was not held, or
     * the release fails, the connection is evicted instead. Closing the database session releases
     * the lock, and no later caller borrows a connection that still holds it.
     *
     * @param workFailure the exception from the work, if it failed. A failure to release the lock is
     *                    attached to it as a suppressed exception.
     * @throws LockCleanupException if the work succeeded but the connection could not be evicted
     */
    void releaseSessionLock(Connection connection, String lockName, Throwable workFailure) {
        LockException releaseFailure;
        try {
            if (unlock(connection, lockName)) {
                connection.close();
                logger.info("Released lock {}", lockName);
                return;
            }
            releaseFailure = new LockException("Lock " + lockName + " was not held when it was released");
        } catch (SQLException e) {
            releaseFailure = new LockException("Could not release lock " + lockName, e);
        }
        try {
            evict(connection, lockName);
        } catch (LockCleanupException cleanupFailure) {
            cleanupFailure.addSuppressed(releaseFailure);
            if (workFailure == null) {
                throw cleanupFailure;
            }
            workFailure.addSuppressed(cleanupFailure);
            return;
        }
        if (workFailure != null) {
            workFailure.addSuppressed(releaseFailure);
        } else {
            logger.error("Evicted the connection for lock {} instead of releasing the lock normally", lockName, releaseFailure);
        }
    }

    /**
     * Closes the database session of a connection, which releases any lock it holds.
     *
     * @throws LockCleanupException if the connection could not be evicted, so its session may still hold the lock
     */
    private void evict(Connection connection, String lockName) {
        try {
            if (dataSource.isWrapperFor(HikariDataSource.class)) {
                dataSource.unwrap(HikariDataSource.class).evictConnection(connection);
            } else {
                connection.abort(Runnable::run);
            }
        } catch (SQLException | RuntimeException e) {
            throw new LockCleanupException("Could not evict the connection for lock " + lockName
                    + ". Its database session may still hold the lock", e);
        }
    }

    private void awaitLock(String lockName, int waitTimeoutSeconds, LockAttempt attempt) {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(waitTimeoutSeconds);
        long pollIntervalMillis = FIRST_POLL_INTERVAL_MILLIS;
        try {
            while (!attempt.tryLock()) {
                if (System.nanoTime() - deadline >= 0) {
                    throw new LockException("Timed out waiting to aquire lock " + lockName + " after " + waitTimeoutSeconds + " seconds");
                }
                Thread.sleep(pollIntervalMillis);
                pollIntervalMillis = Math.min(pollIntervalMillis * 2, MAX_POLL_INTERVAL_MILLIS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockException("Interrupted while waiting for lock " + lockName, e);
        } catch (SQLException e) {
            throw new LockException("Could not take lock " + lockName, e);
        }
    }

    private static boolean tryLock(Connection connection, String lockFunction, String lockName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("select " + lockFunction + "(?, hashtext(?))")) {
            statement.setInt(1, LOCK_NAMESPACE);
            statement.setString(2, lockName);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getBoolean(1);
            }
        }
    }

    /**
     * @return false if the session did not hold the lock
     */
    private static boolean unlock(Connection connection, String lockName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("select pg_advisory_unlock(?, hashtext(?))")) {
            statement.setInt(1, LOCK_NAMESPACE);
            statement.setString(2, lockName);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getBoolean(1);
            }
        }
    }

    @FunctionalInterface
    private interface LockAttempt {
        boolean tryLock() throws SQLException;
    }
}
