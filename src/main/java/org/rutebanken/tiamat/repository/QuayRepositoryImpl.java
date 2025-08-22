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
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.dtoassembling.dto.JbvCodeMappingDto;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.MERGED_ID_KEY;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;
import static org.rutebanken.tiamat.repository.StopPlaceRepositoryImpl.SQL_LEFT_JOIN_PARENT_STOP;
import static org.rutebanken.tiamat.repository.StopPlaceRepositoryImpl.SQL_STOP_PLACE_OR_PARENT_IS_VALID_AT_POINT_IN_TIME;
import static org.rutebanken.tiamat.repository.StopPlaceRepositoryImpl.SQL_STOP_PLACE_OR_PARENT_IS_VALID_IN_INTERVAL;

@Transactional
public class QuayRepositoryImpl implements QuayRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(QuayRepositoryImpl.class);
    public static final String JBV_CODE = "jbvCode";

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This repository method does not use validity for returning the right version.
     * It should probably join stop place and parent stop place to check validity.
     */
    @Override
    public String findFirstByKeyValues(String key, Set<String> values) {

        Query query = entityManager.createNativeQuery("SELECT q.netex_id " +
                "FROM quay_key_values qkv " +
                "INNER JOIN value_items v " +
                "ON qkv.key_values_id = v.value_id " +
                "INNER JOIN quay q " +
                "ON quay_id = quay_id " +
                "WHERE qkv.key_values_key = :key " +
                "AND v.items IN ( :values ) " +
                "AND q.version = (SELECT MAX(qv.version) FROM quay qv WHERE q.netex_id = qv.netex_id)");

        query.setParameter("key", key);
        query.setParameter("values", values);

        try {
            @SuppressWarnings("unchecked")
            List<String> results = query.getResultList();
            if (results.isEmpty()) {
                return null;
            } else {
                return results.getFirst();
            }
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    @Override
    public List<IdMappingDto> findKeyValueMappingsForQuay(Instant validFrom, Instant validTo, int recordPosition, int recordsPerRoundTrip) {
        String sql = "SELECT vi.items, q.netex_id, s.stop_place_type, s.from_date sFrom, s.to_date sTo, p.from_date pFrom, p.to_date pTo " +
                "FROM quay_key_values qkv " +
                "	INNER JOIN stop_place_quays spq " +
                "		ON spq.quays_id = qkv.quay_id " +
                "	INNER JOIN quay q " +
                "		ON spq.quays_id = q.id " +
                "	INNER JOIN stop_place s " +
                "		ON s.id= spq.stop_place_id " +
                "	INNER JOIN value_items vi " +
                "		ON qkv.key_values_id = vi.value_id AND vi.items NOT LIKE '' AND qkv.key_values_key in (:mappingIdKeys) " +
                SQL_LEFT_JOIN_PARENT_STOP +
                "WHERE " +
                 SQL_STOP_PLACE_OR_PARENT_IS_VALID_IN_INTERVAL +
                "ORDER BY q.id,qkv.key_values_id";


        Query nativeQuery = entityManager.createNativeQuery(sql).setFirstResult(recordPosition).setMaxResults(recordsPerRoundTrip);

        if (validTo == null) {
            // Assuming 1000 years into the future is the same as forever
            validTo = Instant.from(ZonedDateTime.now().plusYears(1000).toInstant());
        }

        nativeQuery.setParameter("mappingIdKeys", Arrays.asList(ORIGINAL_ID_KEY, MERGED_ID_KEY));
        nativeQuery.setParameter("validFrom", Date.from(validFrom));
        nativeQuery.setParameter("validTo", Date.from(validTo));
        @SuppressWarnings("unchecked")
        List<Object[]> result = nativeQuery.getResultList();

        List<IdMappingDto> mappingResult = new ArrayList<>();
        for (Object[] row : result) {
            Instant mappingValidFrom = parseInstant(row[3]);
            Instant mappingValidTo = parseInstant(row[4]);
            if (mappingValidFrom == null && mappingValidTo == null) {
                mappingValidFrom = parseInstant(row[5]);
                mappingValidTo = parseInstant(row[6]);
            }
            mappingResult.add(new IdMappingDto(row[0].toString(), row[1].toString(), mappingValidFrom, mappingValidTo, parseStopType(row[2])));
        }

        return mappingResult;
    }

    private Instant parseInstant(Object timestampObject) {
        if (timestampObject instanceof Timestamp) {
            return ((Timestamp)timestampObject).toInstant();
        }
        return null;
    }

    @Override
    public Set<String> findUniqueQuayIds(Instant validFrom, Instant validTo) {
        String sql = "SELECT distinct q.netex_id " +
                "FROM quay q " +
                "INNER JOIN stop_place_quays spq " +
                "   ON spq.quays_id = q.id " +
                "INNER JOIN stop_place s " +
                "   ON spq.stop_place_id = s.id " +
                SQL_LEFT_JOIN_PARENT_STOP +
                "WHERE " +
                SQL_STOP_PLACE_OR_PARENT_IS_VALID_IN_INTERVAL +
                "ORDER BY q.netex_id";


        Query nativeQuery = entityManager.createNativeQuery(sql);

        if (validTo == null) {
            // Assuming 1000 years into the future is the same as forever
            validTo = Instant.from(ZonedDateTime.now().plusYears(1000).toInstant());
        }

        nativeQuery.setParameter("validFrom", Date.from(validFrom));
        nativeQuery.setParameter("validTo", Date.from(validTo));

        List<String> results = nativeQuery.getResultList();

        Set<String> ids = new HashSet<>();
        for(String result : results) {
            ids.add(result);
        }
        return ids;
    }

    private StopTypeEnumeration parseStopType(Object o) {
        if (o != null) {
            return StopTypeEnumeration.valueOf(o.toString());
        }
        return null;
    }

    /**
     * Return jbv code mapping for rail stations. The stop place contains jbc code mapping. The quay contains the public code.
     * @return
     */
    @Override
    public List<JbvCodeMappingDto> findJbvCodeMappingsForQuay() {
        String sql = "SELECT DISTINCT vi.items, q.public_code, q.netex_id " +
                "FROM stop_place_key_values skv " +
                "   INNER JOIN stop_place_quays spq " +
                "       ON spq.stop_place_id = skv.stop_place_id " +
                "   INNER JOIN stop_place s " +
                "       ON s.id = skv.stop_place_id AND s.stop_place_type = :stopPlaceType " +
                SQL_LEFT_JOIN_PARENT_STOP +
                "   INNER JOIN quay q " +
                "       ON spq.quays_id = q.id  AND q.public_code NOT LIKE '' " +
                "   INNER JOIN value_items vi " +
                "       ON skv.key_values_id = vi.value_id AND vi.items NOT LIKE '' AND skv.key_values_key = :mappingIdKeys " +
                "WHERE " + SQL_STOP_PLACE_OR_PARENT_IS_VALID_AT_POINT_IN_TIME +
                "ORDER BY items, public_code ";
        Query nativeQuery = entityManager.createNativeQuery(sql);

        nativeQuery.setParameter("stopPlaceType", StopTypeEnumeration.RAIL_STATION.toString());
        nativeQuery.setParameter("mappingIdKeys", Arrays.asList(JBV_CODE));
        nativeQuery.setParameter("pointInTime", Date.from(Instant.now()));

        @SuppressWarnings("unchecked")
        List<Object[]> result = nativeQuery.getResultList();

        List<JbvCodeMappingDto> mappingResult = new ArrayList<>();
        for (Object[] row : result) {
            mappingResult.add(new JbvCodeMappingDto(row[0].toString(), row[1].toString(), row[2].toString()));
        }

        return mappingResult;
    }

    @Override
    public Map<Long, List<Quay>> findQuaysByStopPlaceIds(Set<Long> stopPlaceIds) {
        Map<Long, List<Quay>> resultMap = new HashMap<>();

        if (stopPlaceIds == null || stopPlaceIds.isEmpty()) {
            return resultMap;
        }

        logger.debug("Batch loading quays for {} stop places", stopPlaceIds.size());

        // Use JPQL to properly initialize Hibernate proxies with a single query
        String jpql = "SELECT q, sp.id FROM Quay q JOIN StopPlace sp ON q MEMBER OF sp.quays WHERE sp.id IN :stopPlaceIds";

        jakarta.persistence.TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("stopPlaceIds", stopPlaceIds);

        List<Object[]> results = query.getResultList();

        // Group quays by stop place ID
        for (Object[] row : results) {
            Quay quay = (Quay) row[0];
            Long stopPlaceId = (Long) row[1];

            resultMap.computeIfAbsent(stopPlaceId, k -> new ArrayList<>()).add(quay);
        }

        // Ensure all requested stop place IDs have entries (even empty lists)
        for (Long stopPlaceId : stopPlaceIds) {
            resultMap.putIfAbsent(stopPlaceId, new ArrayList<>());
        }

        logger.debug("Found {} quays total for {} stop places",
            resultMap.values().stream().mapToInt(List::size).sum(),
            stopPlaceIds.size());

        return resultMap;
    }

    @Override
    public Map<Long, Map<String, Value>> findKeyValuesByIds(Set<Long> quayIds) {
        Map<Long, Map<String, Value>> resultMap = new HashMap<>();

        if (quayIds == null || quayIds.isEmpty()) {
            return resultMap;
        }

        logger.debug("Batch loading keyValues for {} quays", quayIds.size());

        // Query to get all keyValues for the requested quay IDs
        String sql = "SELECT qkv.quay_id, qkv.key_values_key, qkv.key_values_id " +
                     "FROM quay_key_values qkv " +
                     "WHERE qkv.quay_id IN :quayIds";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("quayIds", quayIds);

        List<Object[]> results = query.getResultList();

        // First get all value IDs
        Set<Long> valueIds = new HashSet<>();
        Map<Long, Map<String, Long>> quayKeyToValueId = new HashMap<>();

        for (Object[] row : results) {
            Long quayId = ((Number) row[0]).longValue();
            String key = (String) row[1];
            Long valueId = ((Number) row[2]).longValue();

            valueIds.add(valueId);
            quayKeyToValueId.computeIfAbsent(quayId, k -> new HashMap<>()).put(key, valueId);
        }

        // Now batch load all Value entities with their IDs
        Map<Long, Value> valuesById = new HashMap<>();
        if (!valueIds.isEmpty()) {
            String jpql = "SELECT v.id, v FROM Value v WHERE v.id IN :valueIds";
            jakarta.persistence.TypedQuery<Object[]> valueQuery = entityManager.createQuery(jpql, Object[].class);
            valueQuery.setParameter("valueIds", valueIds);

            List<Object[]> valueResults = valueQuery.getResultList();
            for (Object[] row : valueResults) {
                Long valueId = (Long) row[0];
                Value value = (Value) row[1];
                valuesById.put(valueId, value);
            }
        }

        // Build the final result map
        for (Map.Entry<Long, Map<String, Long>> quayEntry : quayKeyToValueId.entrySet()) {
            Long quayId = quayEntry.getKey();
            Map<String, Value> keyValuesForQuay = new HashMap<>();

            for (Map.Entry<String, Long> keyValueEntry : quayEntry.getValue().entrySet()) {
                String key = keyValueEntry.getKey();
                Long valueId = keyValueEntry.getValue();
                Value value = valuesById.get(valueId);

                if (value != null) {
                    keyValuesForQuay.put(key, value);
                }
            }

            resultMap.put(quayId, keyValuesForQuay);
        }

        // Ensure all requested quay IDs have entries (even empty maps)
        for (Long quayId : quayIds) {
            resultMap.putIfAbsent(quayId, new HashMap<>());
        }

        logger.debug("Found keyValues for {}/{} quays",
            resultMap.entrySet().stream().mapToInt(e -> e.getValue().size()).sum(),
            quayIds.size());

        return resultMap;
    }

}
