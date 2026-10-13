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
import java.util.function.Supplier;

/**
 * Named locks that all Tiamat instances share, backed by PostgreSQL advisory locks.
 * <p>
 * A transaction lock is held until the surrounding transaction commits or rolls back. The next
 * holder therefore always reads the committed state. Use it for work that runs in one transaction.
 * <p>
 * A session lock is held on a connection of its own until the work returns. Use it for work that
 * runs in several transactions or threads, such as imports and background jobs. A session lock is
 * not reentrant: taking the same lock again inside the work waits for itself.
 * <p>
 * PostgreSQL releases both kinds when the connection closes, so a crashed instance does not keep
 * a lock.
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
     *
     * @throws LockException if the lock is not free within the wait timeout
     */
    public <T> T executeInSessionLock(String lockName, int waitTimeoutSeconds, Supplier<T> supplier) {
        try (Connection connection = dataSource.getConnection()) {
            // Outside a transaction, the connection does not sit idle in transaction while the work runs.
            connection.setAutoCommit(true);
            logger.info("Waiting for lock {}", lockName);
            awaitLock(lockName, waitTimeoutSeconds, () -> tryLock(connection, "pg_try_advisory_lock", lockName));
            logger.info("Got lock {}", lockName);
            try {
                return supplier.get();
            } finally {
                unlock(connection, lockName);
            }
        } catch (SQLException e) {
            throw new LockException("Could not use lock " + lockName, e);
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

    private static void unlock(Connection connection, String lockName) {
        try (PreparedStatement statement = connection.prepareStatement("select pg_advisory_unlock(?, hashtext(?))")) {
            statement.setInt(1, LOCK_NAMESPACE);
            statement.setString(2, lockName);
            statement.execute();
            logger.info("Released lock {}", lockName);
        } catch (SQLException e) {
            // Closing the connection releases the lock as well.
            logger.warn("Could not release lock {}", lockName, e);
        }
    }

    @FunctionalInterface
    private interface LockAttempt {
        boolean tryLock() throws SQLException;
    }
}
