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
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.rutebanken.tiamat.model.PathLink;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PathLinkRepositoryImpl implements PathLinkRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    public Long findByKeyValue(String key, Set<String> values) {

        Query query = entityManager.createNativeQuery("SELECT path_link_id " +
                "FROM path_link_key_values plkv " +
                    "INNER JOIN value_items v " +
                        "ON plkv.key_values_id = v.value_id " +
                    "INNER JOIN path_link pl " +
                        "ON path_link_id = pl.id " +
                "WHERE plkv.key_values_key = :key " +
                    "AND v.items IN ( :values ) " +
                    "AND pl.version = (SELECT MAX(plv.version) FROM path_link plv WHERE plv.netex_id = pl.netex_id)");

        query.setParameter("key", key);
        query.setParameter("values", values);

        try {
            @SuppressWarnings("unchecked")
            List<BigInteger> results = query.getResultList();
            if (results.isEmpty()) {
                return null;
            } else {
                return results.getFirst().longValue();
            }
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    @Override
    public List<String> findByStopPlaceNetexId(String netexStopPlaceId) {

        String sql = "SELECT pl.netex_id " +
                "FROM path_link pl " +
                "       INNER JOIN path_link_end ple " +
                "               ON pl.from_id = ple.id " +
                "                  OR pl.to_id = ple.id " +
                "       INNER JOIN quay q " +
                "               ON q.netex_id = ple.place_ref " +
                "WHERE q.id IN (SELECT spq.quays_id " +
                "                FROM stop_place_quays spq " +
                "                   INNER JOIN stop_place s ON s.id = spq.stop_place_id " +
                "                WHERE s.netex_id = :netexStopPlaceId)";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("netexStopPlaceId", netexStopPlaceId);

        try {
            @SuppressWarnings("unchecked")
            List<String> results = query.getResultList();
            return results;

        } catch (NoResultException noResultException) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<PathLink> findByStopPlaceIds(Set<Long> stopPlaceIds) {

        if(stopPlaceIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Use optimized query with UNION to separate stop place and quay lookups
        // This allows better index usage and avoids complex OR conditions
        String sql = createOptimizedFindPathLinkFromStopPlaceIdsSQL(stopPlaceIds);

        Query query = entityManager.createNativeQuery(sql, PathLink.class);

        try {
            @SuppressWarnings("unchecked")
            List<PathLink> results = query.getResultList();
            return results;

        } catch (NoResultException noResultException) {
            return new ArrayList<>();
        }
    }

    /**
     * Optimized SQL query using UNION to combine results from stop place and quay path links.
     * This approach allows the database to use indexes more effectively by avoiding complex OR conditions.
     */
    private String createOptimizedFindPathLinkFromStopPlaceIdsSQL(Set<Long> stopPlaceIds) {
        String stopPlaceIdList = StringUtils.join(stopPlaceIds, ',');
        
        return "SELECT DISTINCT pl.* " +
               "FROM path_link pl " +
               "WHERE pl.id IN ( " +
               "    -- Path links connected directly to stop places " +
               "    SELECT DISTINCT pl2.id " +
               "    FROM stop_place s " +
               "    INNER JOIN path_link_end ple ON ple.place_ref = s.netex_id " +
               "    INNER JOIN path_link pl2 ON (ple.id = pl2.from_id OR ple.id = pl2.to_id) " +
               "    WHERE s.id IN (" + stopPlaceIdList + ") " +
               "      AND (ple.place_version = CAST(s.version AS TEXT) OR ple.place_version IS NULL) " +
               "    " +
               "    UNION " +
               "    " +
               "    -- Path links connected to quays of the stop places " +
               "    SELECT DISTINCT pl2.id " +
               "    FROM stop_place s " +
               "    INNER JOIN stop_place_quays spq ON spq.stop_place_id = s.id " +
               "    INNER JOIN quay q ON spq.quays_id = q.id " +
               "    INNER JOIN path_link_end ple ON ple.place_ref = q.netex_id " +
               "    INNER JOIN path_link pl2 ON (ple.id = pl2.from_id OR ple.id = pl2.to_id) " +
               "    WHERE s.id IN (" + stopPlaceIdList + ") " +
               "      AND (ple.place_version = CAST(q.version AS TEXT) OR ple.place_version IS NULL) " +
               ") " +
               "ORDER BY pl.netex_id, pl.version";
    }

    /**
     * @deprecated Use createOptimizedFindPathLinkFromStopPlaceIdsSQL instead
     */
    @Deprecated
    public String createFindPathLinkFromStopPlaceIdsSQL(Set<Long> stopPlaceIds) {
        return new StringBuilder(
                "SELECT pl.* " +
                        "FROM (" +
                        "   SELECT pl2.id" +
                        "   FROM stop_place s" +
                        "       LEFT OUTER JOIN stop_place_quays spq ON spq.stop_place_id = s.id" +
                        "       LEFT OUTER JOIN quay q ON spq.quays_id = q.id" +
                        "       INNER JOIN path_link_end ple" +
                        "           ON (ple.place_ref = s.netex_id" +
                        "               AND (ple.place_version = cast(s.version AS TEXT) OR ple.place_version is NULL))" +
                        "           OR (ple.place_ref = q.netex_id" +
                        "               AND (ple.place_version = cast(q.version AS TEXT) OR ple.place_version is NULL))" +
                        "       INNER JOIN path_link pl2 ON ple.id = pl2.from_id" +
                        "           OR ple.id = pl2.to_id" +
                        "   WHERE s.id IN(")
                .append(StringUtils.join(stopPlaceIds, ','))
                .append(") ")
                .append("GROUP BY pl2.id) pl2 ")
                .append("JOIN path_link pl ON pl.id = pl2.id ")
                .append("ORDER by pl.netex_id, pl.version")
                .toString();
    }
}
