package org.rutebanken.tiamat.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A non-blocking mutual exclusion lock shared by every replica, backed by a PostgreSQL session-level
 * advisory lock.
 *
 * <p>Used where the work must happen on exactly one replica and skipping is the right response to
 * losing the race. Unlike {@link TimeoutMaxLeaseTimeLock}, this does not depend on the replicas
 * forming a Hazelcast cluster: Hazelcast member discovery is off by default
 * ({@code tiamat.hazelcast.kubernetes.enabled}) and is not enabled by the deployment, so each pod
 * runs a single-member cluster and a Hazelcast lock only excludes threads within one pod.
 *
 * <p>The lock is held on a dedicated connection for the whole of {@code work}, so it can span
 * operations that must not run inside a database transaction - a slow HTTP fetch, or several
 * separate transactions. It is released in a finally block, and the server releases it anyway if the
 * connection drops, so a replica dying mid-work cannot strand it.
 */
@Component
public class PostgresAdvisoryLock {

    private static final Logger logger = LoggerFactory.getLogger(PostgresAdvisoryLock.class);

    private final DataSource dataSource;

    public PostgresAdvisoryLock(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Run {@code work} while holding the advisory lock identified by {@code lockKey}.
     *
     * @param lockKey  application-wide constant identifying the lock
     * @param lockName human readable name, for logging only
     * @param work     the work to run under the lock; must not return null, since an empty result is
     *                 how this method reports that the lock could not be taken
     * @return the result of {@code work}, or {@link Optional#empty()} if the lock is held elsewhere
     */
    public <T> Optional<T> tryWithLock(long lockKey, String lockName, Supplier<T> work) {
        try (Connection connection = dataSource.getConnection()) {
            if (!tryAcquire(connection, lockKey)) {
                logger.info("Advisory lock {} ({}) is held elsewhere", lockName, lockKey);
                return Optional.empty();
            }
            logger.info("Acquired advisory lock {} ({})", lockName, lockKey);
            try {
                return Optional.of(Objects.requireNonNull(work.get(),
                        "Work under lock " + lockName + " returned null, which is indistinguishable from not holding the lock"));
            } finally {
                release(connection, lockKey, lockName);
            }
        } catch (SQLException e) {
            throw new LockException("Failed to obtain advisory lock " + lockName, e);
        }
    }

    private boolean tryAcquire(Connection connection, long lockKey) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT pg_try_advisory_lock(?)")) {
            statement.setLong(1, lockKey);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getBoolean(1);
            }
        }
    }

    private void release(Connection connection, long lockKey, String lockName) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT pg_advisory_unlock(?)")) {
            statement.setLong(1, lockKey);
            statement.execute();
            logger.info("Released advisory lock {} ({})", lockName, lockKey);
        } catch (SQLException e) {
            // An unlock that fails on a live connection is not something Postgres does; the realistic
            // cause is a dead connection, and the server drops the session's locks with the session.
            logger.warn("Could not release advisory lock {} ({})", lockName, lockKey, e);
        }
    }
}
