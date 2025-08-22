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

package org.rutebanken.tiamat.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.rutebanken.tiamat.model.PathLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Optimized version of PathLinkRepository queries
 * This class provides optimized query alternatives that can be used
 * to improve performance of path link lookups.
 */
@Component
public class PathLinkRepositoryOptimized {

    private static final Logger logger = LoggerFactory.getLogger(PathLinkRepositoryOptimized.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Optimized version of findByStopPlaceIds using UNION instead of OR conditions
     * and separating stop place and quay lookups for better index usage
     */
    public List<PathLink> findByStopPlaceIdsOptimized(Set<Long> stopPlaceIds) {
        if (stopPlaceIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Split the complex query into two simpler queries combined with UNION
        // This allows better index usage on each part
        String sql = createOptimizedFindPathLinkSQL(stopPlaceIds);

        logger.debug("Executing optimized path link query for {} stop places", stopPlaceIds.size());
        Query query = entityManager.createNativeQuery(sql, PathLink.class);

        @SuppressWarnings("unchecked")
        List<PathLink> results = query.getResultList();
        logger.debug("Found {} path links", results.size());
        
        return results;
    }

    /**
     * Creates an optimized SQL query that uses UNION to combine results
     * from stop place and quay path links separately
     */
    private String createOptimizedFindPathLinkSQL(Set<Long> stopPlaceIds) {
        String stopPlaceIdList = StringUtils.join(stopPlaceIds, ',');
        
        return """
            SELECT DISTINCT pl.*
            FROM path_link pl
            WHERE pl.id IN (
                -- Path links connected directly to stop places
                SELECT DISTINCT pl2.id
                FROM stop_place s
                INNER JOIN path_link_end ple ON ple.place_ref = s.netex_id
                INNER JOIN path_link pl2 ON (ple.id = pl2.from_id OR ple.id = pl2.to_id)
                WHERE s.id IN (%s)
                  AND (ple.place_version = CAST(s.version AS TEXT) OR ple.place_version IS NULL)
                
                UNION
                
                -- Path links connected to quays of the stop places
                SELECT DISTINCT pl2.id
                FROM stop_place s
                INNER JOIN stop_place_quays spq ON spq.stop_place_id = s.id
                INNER JOIN quay q ON spq.quays_id = q.id
                INNER JOIN path_link_end ple ON ple.place_ref = q.netex_id
                INNER JOIN path_link pl2 ON (ple.id = pl2.from_id OR ple.id = pl2.to_id)
                WHERE s.id IN (%s)
                  AND (ple.place_version = CAST(q.version AS TEXT) OR ple.place_version IS NULL)
            )
            ORDER BY pl.netex_id, pl.version
            """.formatted(stopPlaceIdList, stopPlaceIdList);
    }

    /**
     * Alternative optimized version using CTEs (Common Table Expressions)
     * for better readability and potential performance improvements
     */
    public List<PathLink> findByStopPlaceIdsWithCTE(Set<Long> stopPlaceIds) {
        if (stopPlaceIds.isEmpty()) {
            return new ArrayList<>();
        }

        String sql = createCTEBasedPathLinkSQL(stopPlaceIds);

        logger.debug("Executing CTE-based path link query for {} stop places", stopPlaceIds.size());
        Query query = entityManager.createNativeQuery(sql, PathLink.class);

        @SuppressWarnings("unchecked")
        List<PathLink> results = query.getResultList();
        logger.debug("Found {} path links", results.size());
        
        return results;
    }

    /**
     * Creates a CTE-based query for better performance with large datasets
     */
    private String createCTEBasedPathLinkSQL(Set<Long> stopPlaceIds) {
        String stopPlaceIdList = StringUtils.join(stopPlaceIds, ',');
        
        return """
            WITH relevant_stops AS (
                -- Get all relevant stop places with their netex_ids and versions
                SELECT s.id, s.netex_id, s.version
                FROM stop_place s
                WHERE s.id IN (%s)
            ),
            relevant_quays AS (
                -- Get all quays for the relevant stop places
                SELECT q.id, q.netex_id, q.version
                FROM relevant_stops rs
                INNER JOIN stop_place_quays spq ON spq.stop_place_id = rs.id
                INNER JOIN quay q ON spq.quays_id = q.id
            ),
            relevant_path_link_ends AS (
                -- Get all path link ends connected to relevant stops or quays
                SELECT DISTINCT ple.id
                FROM path_link_end ple
                WHERE EXISTS (
                    SELECT 1 FROM relevant_stops rs
                    WHERE ple.place_ref = rs.netex_id
                      AND (ple.place_version = CAST(rs.version AS TEXT) OR ple.place_version IS NULL)
                )
                OR EXISTS (
                    SELECT 1 FROM relevant_quays rq
                    WHERE ple.place_ref = rq.netex_id
                      AND (ple.place_version = CAST(rq.version AS TEXT) OR ple.place_version IS NULL)
                )
            )
            SELECT DISTINCT pl.*
            FROM path_link pl
            WHERE EXISTS (
                SELECT 1 FROM relevant_path_link_ends rple
                WHERE rple.id = pl.from_id OR rple.id = pl.to_id
            )
            ORDER BY pl.netex_id, pl.version
            """.formatted(stopPlaceIdList);
    }

    /**
     * Batch loading method for DataLoader - efficiently loads path links by stop place IDs
     * This method is designed to be used with GraphQL DataLoader for batch loading
     */
    public List<PathLink> findPathLinksByStopPlaceIdsBatch(Set<Long> stopPlaceIds) {
        if (stopPlaceIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Use the optimized query for batch loading
        return findByStopPlaceIdsOptimized(stopPlaceIds);
    }
}