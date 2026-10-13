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

package org.rutebanken.tiamat.netex.id;

import org.rutebanken.tiamat.lock.AdvisoryLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Hands out gap-free numeric IDs per entity type, for instance 1, 2, 3 for StopPlace.
 * <p>
 * The id_generator table holds every ID in use, so an ID is recorded before it is returned and all
 * Tiamat instances see the same state. Each call is one statement on a connection of its own, in a
 * transaction of its own.
 * <p>
 * The database function next_gapless_id generates an ID. It searches from the last generated ID,
 * which id_generator_position holds per entity type, and holds an advisory lock for the entity type
 * while it moves that position. IDs that are claimed explicitly, for instance by an import, are
 * skipped.
 */
@Service
public class GaplessIdGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(GaplessIdGeneratorService.class);

    /**
     * Number of candidate IDs that one search looks at.
     */
    static final int SEARCH_WINDOW = 2000;

    private static final String GENERATE_ID = "select next_gapless_id(?, ?, ?, ?)";

    private static final String CLAIM_ID =
            "insert into id_generator (table_name, id_value) values (?, ?) on conflict (table_name, id_value) do nothing";

    private final DataSource dataSource;

    public GaplessIdGeneratorService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Get a new ID for a certain entity.
     *
     * @param entityTypeName Name of the entity, for instance StopPlace
     * @return the generated long value
     */
    public long getNextIdForEntity(String entityTypeName) {
        return getNextIdForEntity(entityTypeName, 0);
    }

    /**
     * Claim an ID for a certain entity, or get a new one if the claimed ID is zero.
     * A claimed ID is recorded as in use, so it is never generated.
     *
     * @param entityTypeName Name of the entity, for instance StopPlace
     * @param claimedId The ID to claim, or zero for a new ID
     * @return the claimed or generated ID
     */
    public long getNextIdForEntity(String entityTypeName, long claimedId) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            return claimedId > 0 ? claimId(connection, entityTypeName, claimedId) : generateId(connection, entityTypeName);
        } catch (SQLException e) {
            throw new IdGeneratorException("Caught exception when generating IDs for entity " + entityTypeName, e);
        }
    }

    private static long claimId(Connection connection, String entityTypeName, long claimedId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CLAIM_ID)) {
            statement.setString(1, entityTypeName);
            statement.setLong(2, claimedId);
            statement.executeUpdate();
        }
        logger.trace("Claimed ID {} for {}", claimedId, entityTypeName);
        return claimedId;
    }

    private static long generateId(Connection connection, String entityTypeName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(GENERATE_ID)) {
            statement.setString(1, entityTypeName);
            statement.setInt(2, AdvisoryLock.LOCK_NAMESPACE);
            statement.setString(3, "id-generator-" + entityTypeName);
            statement.setInt(4, SEARCH_WINDOW);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                long id = resultSet.getLong(1);
                logger.trace("Generated ID {} for {}", id, entityTypeName);
                return id;
            }
        }
    }
}
