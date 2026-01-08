package org.rutebanken.tiamat.ext.fintraffic.api.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rutebanken.netex.model.EntityInVersionStructure;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.PassengerStopAssignment;
import org.rutebanken.netex.model.ScheduledStopPoint;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiEntityInRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractNetexRepository implements NetexRepository {
    protected final JdbcTemplate jdbc;
    protected final ObjectMapper objectMapper;
    protected final DataSource dataSource;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected AbstractNetexRepository(JdbcTemplate jdbc, ObjectMapper objectMapper) {
        this.jdbc = jdbc;
        this.objectMapper = objectMapper;
        this.dataSource = jdbc.getDataSource();
    }

    protected record StopPlaceVersionInfo(String id, int version, long changed, String status) {
    }

    protected String getStopPlaceVersionInfoJson(Collection<ReadApiEntityInRecord> entityRecords) {
        List<StopPlaceVersionInfo> stopPlaceVersionInfoList = entityRecords.stream()
                .filter(record -> record.type().equals(StopPlace.class.getSimpleName()))
                .map(record -> new StopPlaceVersionInfo(
                        record.id(),
                        (int) record.version(),
                        record.changed(),
                        record.status().name()
                ))
                .collect(Collectors.toList());

        try {
            return objectMapper.writeValueAsString(stopPlaceVersionInfoList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize StopPlace version info to JSON", e);
        }
    }

    @Override
    public void upsertEntities(Collection<ReadApiEntityInRecord> entityRecords) {
        if (entityRecords.isEmpty()) {
            return;
        }

        String stopPlaceVersionInfoJson = this.getStopPlaceVersionInfoJson(entityRecords);

        Connection conn = DataSourceUtils.getConnection(dataSource);
        boolean previousAutoCommit = false;

        try {
            previousAutoCommit = conn.getAutoCommit();

            // Disable auto-commit and set isolation level
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            // Disable JIT for this transaction
            // Time spent in JIT compilation is larger than the time saved by JIT
            try (PreparedStatement jitStmt = conn.prepareStatement("SET LOCAL jit = off")) {
                jitStmt.execute();
            }

            // Mark dependant entities as DELETED based on parent StopPlace version and changed timestamp
            // 1. For ScheduledStopPoint and PassengerStopAssignment entities, mark as DELETED if their parent StopPlace has a lower version and changed timestamp.
            // 2. For Parking entities, remove references to DELETED parent StopPlaces and mark as DELETED if no parent references remain
            String markPossibleDependantDeletedSql = """
                    UPDATE ext_fintraffic_netex_entity e
                    SET
                        parent_refs = CASE
                            WHEN e.type = 'Parking' AND parent_elements->>'status' = 'DELETED'
                                THEN array_remove(parent_refs, parent_elements->>'id')
                            ELSE parent_refs
                        END,
                        updated_at = CURRENT_TIMESTAMP,
                        status = CASE
                            WHEN e.type IN ('ScheduledStopPoint', 'PassengerStopAssignment')
                                AND EXISTS(
                                    SELECT 1 FROM ext_fintraffic_netex_entity p
                                    WHERE p.id = parent_elements->>'id'
                                        AND p.version < (parent_elements->>'version')::int
                                        AND p.changed < (parent_elements->>'changed')::bigint
                                )
                                THEN 'DELETED'
                            WHEN e.type = 'Parking'
                                AND parent_elements->>'status' = 'DELETED'
                                AND array_length(array_remove(parent_refs, parent_elements->>'id'), 1) IS NULL
                                THEN 'DELETED'
                            ELSE e.status
                        END
                    FROM json_array_elements(?::json) parent_elements
                    WHERE
                        parent_refs @> ARRAY[(parent_elements->>'id')]
                        AND e.status != 'DELETED'
                        AND (
                            (e.type IN ('ScheduledStopPoint', 'PassengerStopAssignment'))
                            OR (e.type = 'Parking' AND parent_elements->>'status' = 'DELETED')
                        )
                    """;

            try (PreparedStatement updateStmt = conn.prepareStatement(markPossibleDependantDeletedSql)) {
                updateStmt.setString(1, stopPlaceVersionInfoJson);
                updateStmt.executeUpdate();
            }

            // Insert new entities or update existing ones based on version and changed timestamp
            String sql = """
                    INSERT INTO ext_fintraffic_netex_entity (id, type, search_key, xml, version, changed, status, parent_refs)
                    VALUES (?, ?, ?::jsonb, ?, ?, ?, ?, ?)
                    ON CONFLICT (id) DO UPDATE SET
                        type = EXCLUDED.type,
                        search_key = EXCLUDED.search_key,
                        xml = EXCLUDED.xml,
                        version = EXCLUDED.version,
                        changed = EXCLUDED.changed,
                        status = EXCLUDED.status,
                        updated_at = CURRENT_TIMESTAMP,
                        parent_refs = EXCLUDED.parent_refs
                     WHERE (EXCLUDED.status = 'DELETED' AND EXCLUDED.type = 'Parking') OR (ext_fintraffic_netex_entity.version < EXCLUDED.version AND ext_fintraffic_netex_entity.changed < EXCLUDED.changed)
                    """;

            try (PreparedStatement insertStmt = conn.prepareStatement(sql)) {
                for (ReadApiEntityInRecord entityRecord : entityRecords) {
                    insertStmt.setString(1, entityRecord.id());
                    insertStmt.setString(2, entityRecord.type());
                    insertStmt.setString(3, entityRecord.searchKey());
                    insertStmt.setString(4, entityRecord.xml());
                    insertStmt.setLong(5, entityRecord.version());
                    insertStmt.setLong(6, entityRecord.changed());
                    insertStmt.setString(7, entityRecord.status().name());
                    insertStmt.setArray(8, conn.createArrayOf("text", entityRecord.parentRefs()));
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
                logger.error("Transaction rolled back due to error", e);
            } catch (SQLException rollbackEx) {
                logger.error("Failed to rollback transaction", rollbackEx);
            }
            throw new RuntimeException("Failed to upsert entities", e);
        } finally {
            try {
                // Restore original auto-commit state
                conn.setAutoCommit(previousAutoCommit);
            } catch (SQLException e) {
                logger.warn("Failed to restore auto-commit state", e);
            }
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public void checkDatabaseConsistency() {
        checkDatabaseConsistency(StopPlace.class);
        checkDatabaseConsistency(Parking.class);
        checkDatabaseConsistency(ScheduledStopPoint.class);
        checkDatabaseConsistency(PassengerStopAssignment.class);
    }

    private <T extends EntityInVersionStructure> void checkDatabaseConsistency(Class<T> entityClass) {
        String sql = ReadApiDbConsistency.getDbConsistencySql(entityClass);
        int maxIssues = 10;
        jdbc.query(sql, rs -> {
            int issueCount = 0;
            StringBuilder firstIssues = new StringBuilder();
            while (rs.next()) {
                issueCount++;
                if (issueCount <= maxIssues) {
                    String netexId = rs.getString("netex_id");
                    int version = rs.getInt("version");
                    String issue = rs.getString("issue");
                    firstIssues.append(String.format("Issue: %s, StopPlace ID: %s, Version: %d%n", issue, netexId, version));
                }
            }
            if (issueCount > 0) {
                logger.warn("StopPlace drift check found {} issues. First {} issues:\n{}", issueCount, Math.min(maxIssues, issueCount), firstIssues);
            } else {
                logger.info("StopPlace drift check found no issues.");
            }
        });
    }
}
