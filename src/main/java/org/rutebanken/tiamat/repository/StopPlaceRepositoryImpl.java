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


import com.google.common.collect.Sets;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.query.NativeQuery;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.dtoassembling.dto.JbvCodeMappingDto;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.repository.iterator.ScrollableResultIterator;
import org.rutebanken.tiamat.repository.search.ChangedStopPlaceSearch;
import org.rutebanken.tiamat.repository.search.SearchHelper;
import org.rutebanken.tiamat.repository.search.StopPlaceQueryFromSearchBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.MERGED_ID_KEY;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;
import static org.rutebanken.tiamat.repository.QuayRepositoryImpl.JBV_CODE;

@Transactional
public class StopPlaceRepositoryImpl implements StopPlaceRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceRepositoryImpl.class);

    private static final int SCROLL_FETCH_SIZE = 1000;

    private static BasicFormatterImpl basicFormatter = new BasicFormatterImpl();

    /**
     * Part of SQL that checks that either the stop place named as *s* or the parent named *p* is valid at the point in time.
     * The parameter "pointInTime" must be set.
     * The parent stop must be joined in as 'p' to allow checking the validity.
     */
    protected static final String SQL_STOP_PLACE_OR_PARENT_IS_VALID_AT_POINT_IN_TIME =
            " ((p.netex_id IS NOT NULL AND (p.from_date IS NULL OR p.from_date <= :pointInTime) AND (p.to_date IS NULL OR p.to_date > :pointInTime))" +
                    "  OR (p.netex_id IS NULL AND (s.from_date IS NULL OR s.from_date <= :pointInTime) AND (s.to_date IS NULL OR s.to_date > :pointInTime))) ";

    /**
     * Part of SQL that checks that either the stop place named as *s* or the parent named *p* is valid within a provided interval
     * The parameters "validFrom" and "validTo" must be set.
     * The parent stop must be joined in as 'p' to allow checking the validity.
     */
    protected static final String SQL_STOP_PLACE_OR_PARENT_IS_VALID_IN_INTERVAL =
            " ((p.netex_id IS NOT NULL AND (p.from_date IS NULL OR p.from_date <= :validTo) AND (p.to_date IS NULL OR p.to_date > :validFrom))" +
                    "  OR (p.netex_id IS NULL AND (s.from_date IS NULL OR s.from_date <= :validTo) AND (s.to_date IS NULL OR s.to_date > :validFrom))) ";

    /**
     * Left join parent stop place p with stop place s on parent site ref and parent site ref version.
     */
    public static final String SQL_LEFT_JOIN_PARENT_STOP =
            createLeftJoinParentStopQuery("p");

    public static final String SQL_LEFT_JOIN_PARENT_STOP_TEMPLATE =
            "LEFT JOIN stop_place %s ON s.parent_site_ref = %s.netex_id AND CAST(s.parent_site_ref_version as bigint) = %s.version ";

    public static String createLeftJoinParentStopQuery(String parentAlias) {
        return String.format(SQL_LEFT_JOIN_PARENT_STOP_TEMPLATE, Collections.nCopies(3, parentAlias).toArray());
    }

    /**
     * When selecting stop places and there are multiple versions of the same stop place, and you only need the highest version by number.
     */
    protected static final String SQL_MAX_VERSION_OF_STOP_PLACE = "s.version = (select max(sv.version) from stop_place sv where sv.netex_id = s.netex_id) ";

    /**
     * Check stop place or it's parent for match in geometry filter.
     */
    protected static final String SQL_CHILD_OR_PARENT_WITHIN = "(ST_within(s.centroid, :filter) = true OR ST_within(p.centroid, :filter) = true) ";

    /**
     * SQL for making sure the stop selected is not a parent stop place.
     */
    public static final String SQL_NOT_PARENT_STOP_PLACE = "s.parent_stop_place = false ";

    /**
     * Ignore netex id for both stop place and its parent
     */
    protected static final String SQL_IGNORE_STOP_PLACE_ID = "(s.netex_id != :ignoreStopPlaceId AND (p.netex_id IS NULL OR p.netex_id != :ignoreStopPlaceId)) ";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private StopPlaceQueryFromSearchBuilder stopPlaceQueryFromSearchBuilder;

    @Autowired
    private SearchHelper searchHelper;

    /**
     * Find nearby stop places that are valid 'now', specifying a bounding box.
     * Optionally, a stop place ID to ignore can be defined.
     */
    @Override
    public Page<StopPlace> findStopPlacesWithin(double xMin, double yMin, double xMax, double yMax, String ignoreStopPlaceId, Pageable pageable) {
        return findStopPlacesWithin(xMin, yMin, xMax, yMax, ignoreStopPlaceId, Instant.now(), pageable);
    }

    /**
     * Find nearby stop places that are valid at the given point in time, specifying a bounding box.
     * Optionally, a stop place ID to ignore can be defined.
     */
    @Override
    public Page<StopPlace> findStopPlacesWithin(double xMin, double yMin, double xMax, double yMax, String ignoreStopPlaceId, Instant pointInTime, Pageable pageable) {
        return findStopPlacesWithinJPQL(xMin, yMin, xMax, yMax, ignoreStopPlaceId, pointInTime, pageable);
    }

    /**
     * JPQL implementation of bbox query that properly supports lazy loading for topographic places.
     * This replaces the native SQL implementation to enable proper lazy loading of TopographicPlace entities.
     * The native SQL was causing eager loading because SELECT s.* included topographic_place_id column.
     */
    private Page<StopPlace> findStopPlacesWithinJPQL(double xMin, double yMin, double xMax, double yMax, String ignoreStopPlaceId, Instant pointInTime, Pageable pageable) {
        Envelope envelope = new Envelope(xMin, xMax, yMin, yMax);
        Geometry geometryFilter = geometryFactory.toGeometry(envelope);

        if (pointInTime != null) {
            return findStopPlacesWithinAtPointInTime(geometryFilter, ignoreStopPlaceId, pointInTime, pageable);
        } else {
            return findStopPlacesWithinMaxVersion(geometryFilter, ignoreStopPlaceId, pageable);
        }
    }

    /**
     * Find stop places within bounding box with max version (no point in time)
     */
    private Page<StopPlace> findStopPlacesWithinMaxVersion(Geometry geometryFilter, String ignoreStopPlaceId, Pageable pageable) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT s FROM StopPlace s ")
            .append("WHERE within(s.centroid, :geometryFilter) = true ")
            .append("AND s.parentStopPlace = false ")
            .append("AND NOT EXISTS (SELECT 1 FROM StopPlace sv WHERE sv.netexId = s.netexId AND sv.version > s.version) ");

        if (ignoreStopPlaceId != null) {
            jpql.append("AND s.netexId != :ignoreStopPlaceId ");
        }

        logger.debug("Finding stops within bounding box with JPQL: {}", jpql);

        TypedQuery<StopPlace> query = entityManager.createQuery(jpql.toString(), StopPlace.class);
        query.setParameter("geometryFilter", geometryFilter);

        if (ignoreStopPlaceId != null) {
            query.setParameter("ignoreStopPlaceId", ignoreStopPlaceId);
        }

        query.setFirstResult(Math.toIntExact(pageable.getOffset()));
        query.setMaxResults(pageable.getPageSize());
        
        List<StopPlace> stopPlaces = query.getResultList();
        return new PageImpl<>(stopPlaces, pageable, stopPlaces.size());
    }

    /**
     * Find stop places within bounding box at a specific point in time (includes parent logic)
     */
    private Page<StopPlace> findStopPlacesWithinAtPointInTime(Geometry geometryFilter, String ignoreStopPlaceId, Instant pointInTime, Pageable pageable) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT DISTINCT s FROM StopPlace s ")
            .append("LEFT JOIN StopPlace p ON s.parentSiteRef.ref = p.netexId AND CAST(s.parentSiteRef.version AS long) = p.version ")
            .append("WHERE (within(s.centroid, :geometryFilter) = true OR within(p.centroid, :geometryFilter) = true) ")
            .append("AND s.parentStopPlace = false ")
            .append("AND ((p.netexId IS NOT NULL AND (p.validBetween.fromDate IS NULL OR p.validBetween.fromDate <= :pointInTime) AND (p.validBetween.toDate IS NULL OR p.validBetween.toDate > :pointInTime)) ")
            .append("OR (p.netexId IS NULL AND (s.validBetween.fromDate IS NULL OR s.validBetween.fromDate <= :pointInTime) AND (s.validBetween.toDate IS NULL OR s.validBetween.toDate > :pointInTime))) ");

        if (ignoreStopPlaceId != null) {
            jpql.append("AND (s.netexId != :ignoreStopPlaceId AND (p.netexId IS NULL OR p.netexId != :ignoreStopPlaceId)) ");
        }

        logger.debug("Finding stops within bounding box at point in time with JPQL: {}", jpql);

        TypedQuery<StopPlace> query = entityManager.createQuery(jpql.toString(), StopPlace.class);
        query.setParameter("geometryFilter", geometryFilter);
        query.setParameter("pointInTime", pointInTime);

        if (ignoreStopPlaceId != null) {
            query.setParameter("ignoreStopPlaceId", ignoreStopPlaceId);
        }

        query.setFirstResult(Math.toIntExact(pageable.getOffset()));
        query.setMaxResults(pageable.getPageSize());
        
        List<StopPlace> stopPlaces = query.getResultList();
        return new PageImpl<>(stopPlaces, pageable, stopPlaces.size());
    }


    private static String getSubQueryString(String ignoreStopPlaceId) {
        String subQueryString = "SELECT s.netex_id,max(s.version) FROM stop_place s " +
                SQL_LEFT_JOIN_PARENT_STOP +
                "WHERE " +
                SQL_CHILD_OR_PARENT_WITHIN +
                "AND "
                + SQL_NOT_PARENT_STOP_PLACE;


        if (ignoreStopPlaceId != null) {
            subQueryString += "AND " + SQL_IGNORE_STOP_PLACE_ID + "group by s.netex_id";
        } else {
            subQueryString += "group by s.netex_id" ;
        }
        return subQueryString;
    }

    /**
     * This query contains a fuzzy similarity check on name.
     *
     * @param envelope            bounding box
     * @param name                name to fuzzy match
     * @param stopTypeEnumeration stop place type
     * @return the stop place within bounding box if equal type, within envelope and closest similarity in name
     */
    @Override
    public String findNearbyStopPlace(Envelope envelope, String name, StopTypeEnumeration stopTypeEnumeration) {
        Geometry geometryFilter = geometryFactory.toGeometry(envelope);

        String sql = "SELECT sub.netex_id FROM " +
                "(SELECT s.netex_id AS netex_id, similarity(s.name_value, :name) AS sim FROM stop_place s " +
                SQL_LEFT_JOIN_PARENT_STOP +
                "WHERE ST_Within(s.centroid, :filter) = true " +
                "AND " + SQL_STOP_PLACE_OR_PARENT_IS_VALID_AT_POINT_IN_TIME +
                "AND s.stop_place_type = :stopPlaceType) sub " +
                "WHERE sub.sim > 0.6 " +
                "ORDER BY sub.sim DESC LIMIT 1";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("pointInTime", Date.from(Instant.now()));
        query.setParameter("filter", geometryFilter);
        query.setParameter("stopPlaceType", stopTypeEnumeration.toString());
        query.setParameter("name", name);
        return getOneOrNull(query);
    }

    @Override
    public String findNearbyStopPlace(Envelope envelope, String name) {
        Geometry geometryFilter = geometryFactory.toGeometry(envelope);

        Query query = entityManager.createNativeQuery("SELECT s.netex_id FROM stop_place s " +
                                                           SQL_LEFT_JOIN_PARENT_STOP +
                                                           "WHERE ST_Within(s.centroid, :filter) = true " +
                                                           "AND " + SQL_STOP_PLACE_OR_PARENT_IS_VALID_AT_POINT_IN_TIME +
                                                           "AND s.name_value = :name ");
        query.setParameter("filter", geometryFilter);
        query.setParameter("name", name);
        query.setParameter("pointInTime", Date.from(Instant.now()));
        return getOneOrNull(query);
    }



    @Override
    public List<String> findNearbyStopPlace(Envelope envelope, StopTypeEnumeration stopTypeEnumeration) {
        Geometry geometryFilter = geometryFactory.toGeometry(envelope);

        Query query = entityManager.createNativeQuery("SELECT s.netex_id FROM stop_place s " +
                                                                SQL_LEFT_JOIN_PARENT_STOP +
                                                                "WHERE ST_within(s.centroid, :filter) = true " +
                                                                "AND " + SQL_STOP_PLACE_OR_PARENT_IS_VALID_AT_POINT_IN_TIME +
                                                                "AND s.stop_place_type = :stopPlaceType");

        query.setParameter("filter", geometryFilter);
        query.setParameter("stopPlaceType", stopTypeEnumeration.toString());
        query.setParameter("pointInTime", Date.from(Instant.now()));
        try {
            List<String> resultList = query.getResultList();
            return resultList;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public String findFirstByKeyValues(String key, Set<String> values) {
        Set<String> matches = findByKeyValues(key, values);
        if(matches.isEmpty()) {
            return null;
        }
        return matches.iterator().next();

    }

    /**
     * Find stop place netex IDs by key value
     *
     * @param key key in key values for stop
     * @param values list of values to check for
     * @return set of stop place's netex IDs
     */
    @Override
    public Set<String> findByKeyValues(String key, Set<String> values) {
        return findByKeyValues(key, values, false);
    }

    /**
     * Find stop place netex IDs by key value
     *
     * @param key key in key values for stop
     * @param values list of values to check for
     * @param exactMatch set to <code>true</code> to perform lookup instead of search
     * @return set of stop place's netex IDs
     */
    @Override
    public Set<String> findByKeyValues(String key, Set<String> values, boolean exactMatch) {

        StringBuilder sqlQuery = new StringBuilder("SELECT s.netex_id " +
                                                           "FROM stop_place s " +
                                                            "INNER JOIN stop_place_key_values spkv " +
                                                           "ON spkv.stop_place_id = s.id " +
                                                           "INNER JOIN value_items v " +
                                                           "ON spkv.key_values_id = v.value_id " +
                                                           SQL_LEFT_JOIN_PARENT_STOP +
                                                           "WHERE spkv.key_values_key = :key " +
                                                           "AND " + SQL_STOP_PLACE_OR_PARENT_IS_VALID_AT_POINT_IN_TIME);


        List<String> parameters = new ArrayList<>(values.size());
        List<String> parametervalues = new ArrayList<>(values.size());
        final String parameterPrefix = "value";
        sqlQuery.append(" AND (");
        Iterator<String> valuesIterator = values.iterator();
        for (int parameterCounter = 0; parameterCounter < values.size(); parameterCounter++) {
            sqlQuery.append(" v.items LIKE :value").append(parameterCounter);
            parameters.add(parameterPrefix + parameterCounter);
            parametervalues.add((exactMatch ? "":"%") + valuesIterator.next());
            if (parameterCounter + 1 < values.size()) {
                sqlQuery.append(" OR ");
            }
        }

        sqlQuery.append(" )");

        Query query = entityManager.createNativeQuery(sqlQuery.toString());

        Iterator<String> iterator = parametervalues.iterator();
        parameters.forEach(parameter -> query.setParameter(parameter, iterator.next()));
        query.setParameter("key", key);
        query.setParameter("pointInTime", Date.from(Instant.now()));


        return getSetResult(query);
    }

    public List<String> searchByKeyValue(String key, String value) {

        Query query = entityManager.createNativeQuery("SELECT s.netex_id " +
                                                              "FROM stop_place_key_values spkv " +
                                                              "INNER JOIN value_items v " +
                                                              "ON spkv.key_values_id = v.value_id " +
                                                              "INNER JOIN stop_place s " +
                                                              "ON spkv.stop_place_id = s.id " +
                                                               SQL_LEFT_JOIN_PARENT_STOP +
                                                              "WHERE  spkv.key_values_key = :key " +
                                                              "AND v.items LIKE ( :value ) " +
                                                              "AND " + SQL_STOP_PLACE_OR_PARENT_IS_VALID_AT_POINT_IN_TIME);

        query.setParameter("key", key);
        query.setParameter("value", "%" + value + "%");
        query.setParameter("pointInTime", Date.from(Instant.now()));

        try {
            @SuppressWarnings("unchecked")
            List<String> results = query.getResultList();
            if (results.isEmpty()) {
                return null;
            } else {
                return results;
            }
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    private StopTypeEnumeration parseStopType(Object o) {
        if (o != null) {
            return StopTypeEnumeration.valueOf(o.toString());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IdMappingDto> findKeyValueMappingsForStop(Instant validFrom, Instant validTo, int recordPosition, int recordsPerRoundTrip) {
        String sql = "SELECT v.items, s.netex_id, s.stop_place_type, s.from_date sFrom, s.to_date sTo, p.from_date pFrom, p.to_date pTo " +
                             "FROM stop_place_key_values spkv " +
                             "  INNER JOIN value_items v " +
                             "      ON spkv.key_values_key in (:mappingIdKeys) AND spkv.key_values_id = v.value_id AND v.items NOT LIKE '' " +
                             "  INNER JOIN stop_place s ON s.id = spkv.stop_place_id " +
                             SQL_LEFT_JOIN_PARENT_STOP +
                             "WHERE " +
                              SQL_STOP_PLACE_OR_PARENT_IS_VALID_IN_INTERVAL +
                             "ORDER BY s.id,spkv.key_values_id";


        Query nativeQuery = entityManager.createNativeQuery(sql).setFirstResult(recordPosition).setMaxResults(recordsPerRoundTrip);

        if (validTo == null) {
            // Assuming 1000 years into the future is the same as forever
            validTo = Instant.from(ZonedDateTime.now().plusYears(1000).toInstant());
        }

        nativeQuery.setParameter("mappingIdKeys", Arrays.asList(ORIGINAL_ID_KEY, MERGED_ID_KEY));
        nativeQuery.setParameter("validFrom", Date.from(validFrom));
        nativeQuery.setParameter("validTo", Date.from(validTo));

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
    public Set<String> findUniqueStopPlaceIds(Instant validFrom, Instant validTo) {
        String sql = "SELECT DISTINCT s.netex_id FROM stop_place s " +
                        SQL_LEFT_JOIN_PARENT_STOP +
                        "WHERE " +
                        SQL_STOP_PLACE_OR_PARENT_IS_VALID_IN_INTERVAL +
                        "ORDER BY s.netex_id";


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


    @Override
    public List<String> findStopPlaceFromQuayOriginalId(String quayOriginalId, Instant pointInTime) {
        String sql = "SELECT DISTINCT s.netex_id " +
                             "FROM stop_place s " +
                             "  INNER JOIN stop_place_quays spq " +
                             "    ON s.id = spq.stop_place_id " +
                             "  INNER JOIN quay q " +
                             "    ON spq.quays_id = q.id " +
                             "  INNER JOIN quay_key_values qkv " +
                             "    ON q.id = qkv.quay_id AND qkv.key_values_key in (:originalIdKey) " +
                             "  INNER JOIN value_items vi " +
                             "    ON vi.value_id = qkv.key_values_id AND vi.items LIKE :value " +
                             SQL_LEFT_JOIN_PARENT_STOP +
                             " WHERE " +
                SQL_STOP_PLACE_OR_PARENT_IS_VALID_AT_POINT_IN_TIME;

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("value", "%:" + quayOriginalId);
        query.setParameter("originalIdKey", ORIGINAL_ID_KEY);
        query.setParameter("pointInTime",  Date.from(pointInTime));

        try {
            @SuppressWarnings("unchecked")
            List<String> results = query.getResultList();
            if (results.isEmpty()) {
                return new ArrayList<>();
            } else {
                return results;
            }
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    @Override
    public Map<String, Set<String>> listStopPlaceIdsAndQuayIds(Instant validFrom, Instant validTo) {
        String sql = "SELECT DISTINCT s.netex_id as stop_place_id, q.netex_id as quay_id " +
                "FROM stop_place s " +
                "  INNER JOIN stop_place_quays spq " +
                "    ON s.id = spq.stop_place_id " +
                "  INNER JOIN quay q " +
                "    ON spq.quays_id = q.id " +
                SQL_LEFT_JOIN_PARENT_STOP +
                " WHERE " +
                SQL_STOP_PLACE_OR_PARENT_IS_VALID_IN_INTERVAL;

        if (validTo == null) {
            // Assuming 1000 years into the future is the same as forever
            validTo = Instant.from(ZonedDateTime.now().plusYears(1000).toInstant());
        }

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("validTo", Date.from(validTo));
        query.setParameter("validFrom",  Date.from(validFrom));

        try {
            @SuppressWarnings("unchecked")
            List<String[]> results = query.getResultList();
            if (results.isEmpty()) {
                return Collections.emptyMap();
            } else {
                HashMap<String, Set<String>> result = new HashMap<>();
                for (Object[] strings : results) {
                    String stopplaceId = (String) strings[0];
                    String quayId = (String) strings[1];
                    Set<String> quays = result.computeIfAbsent(stopplaceId, s -> new HashSet<>());
                    quays.add(quayId);
                }
                return result;
            }
        } catch (NoResultException noResultException) {
            return null;
        }
    }


    @Override
    public Iterator<StopPlace> scrollStopPlaces() {
        Session session = entityManager.unwrap(Session.class);

        final String queryString = "select s.* from stop_place s";
        final NativeQuery<StopPlace> nativeQuery = session.createNativeQuery(queryString, StopPlace.class);

        nativeQuery.setReadOnly(true);
        nativeQuery.setFetchSize(SCROLL_FETCH_SIZE);
        nativeQuery.setCacheable(false);

        ScrollableResults results = nativeQuery.scroll(ScrollMode.FORWARD_ONLY);

       return new ScrollableResultIterator<>(results, SCROLL_FETCH_SIZE, session);

    }

    @Override
    public Iterator<StopPlace> scrollStopPlaces(ExportParams exportParams) {

        Session session = entityManager.unwrap(Session.class);

        Pair<String, Map<String, Object>> queryWithParams = stopPlaceQueryFromSearchBuilder.buildQueryString(exportParams);
        NativeQuery<StopPlace> sqlQuery = session.createNativeQuery(queryWithParams.getFirst(),StopPlace.class);
        searchHelper.addParams(sqlQuery, queryWithParams.getSecond());

        return scrollStopPlaces(sqlQuery, session);
    }

    @Override
    public Iterator<StopPlace> scrollStopPlaces(Set<Long> stopPlacePrimaryIds) {
        Session session = entityManager.unwrap(Session.class);

        NativeQuery<StopPlace> sqlQuery = session.createNativeQuery(generateStopPlaceQueryFromStopPlaceIds(stopPlacePrimaryIds), StopPlace.class);

        logger.info("Scrolling {} stop places", stopPlacePrimaryIds.size());
       return scrollStopPlaces(sqlQuery, session);
    }

    public Iterator<StopPlace> scrollStopPlaces(NativeQuery<StopPlace> sqlQuery, Session session) {

        sqlQuery.addEntity(StopPlace.class);
        sqlQuery.setReadOnly(true);
        sqlQuery.setFetchSize(SCROLL_FETCH_SIZE);
        sqlQuery.setCacheable(false);
        ScrollableResults results = sqlQuery.scroll(ScrollMode.FORWARD_ONLY);

        return new ScrollableResultIterator<>(results, SCROLL_FETCH_SIZE, session);
    }

    private String generateStopPlaceQueryFromStopPlaceIds(Set<Long> stopPlacePrimaryIds) {

        Set<String> stopPlacePrimaryIdStrings = stopPlacePrimaryIds.stream().map(lvalue -> String.valueOf(lvalue)).collect(Collectors.toSet());
        String joinedStopPlaceDbIds = String.join(",", stopPlacePrimaryIdStrings);
        StringBuilder sql = new StringBuilder("SELECT s.* FROM stop_place s WHERE s.id IN(");
        sql.append(joinedStopPlaceDbIds);
        sql.append(")");
        return sql.toString();
    }

    @Override
    public Set<String> getNetexIds(ExportParams exportParams) {
        Pair<String, Map<String, Object>> pair = stopPlaceQueryFromSearchBuilder.buildQueryString(exportParams);
        Session session = entityManager.unwrap(Session.class);
        NativeQuery query = session.createNativeQuery("SELECT sub.netex_id from (" + pair.getFirst() + ") sub");

        searchHelper.addParams(query, pair.getSecond());

        @SuppressWarnings("unchecked")
        Set<String> result =  new HashSet<>(query.list());
        return result;
    }

    @Override
    public Set<Long> getDatabaseIds(ExportParams exportParams, boolean ignorePaging) {
        Pair<String, Map<String, Object>> pair = stopPlaceQueryFromSearchBuilder.buildQueryString(exportParams);
        Session session = entityManager.unwrap(Session.class);
        NativeQuery query = session.createNativeQuery("SELECT sub.id from (" + pair.getFirst() + ") sub");

        if(!ignorePaging) {
            long firstResult = exportParams.getStopPlaceSearch().getPageable().getOffset();
            query.setFirstResult(Math.toIntExact(firstResult));
            query.setMaxResults(exportParams.getStopPlaceSearch().getPageable().getPageSize());
        }
        searchHelper.addParams(query, pair.getSecond());
        Set<Long> result = new HashSet<>();
        for(Object object : query.list()) {
            result.add((Long) object);

        }

        return result;
    }

    @Override
    public Page<StopPlace> findStopPlace(ExportParams exportParams) {
        Pair<String, Map<String, Object>> queryWithParams = stopPlaceQueryFromSearchBuilder.buildQueryString(exportParams);

        final Query nativeQuery = entityManager.createNativeQuery(queryWithParams.getFirst(), StopPlace.class);

        queryWithParams.getSecond().forEach(nativeQuery::setParameter);
        long firstResult = exportParams.getStopPlaceSearch().getPageable().getOffset();
        nativeQuery.setFirstResult(Math.toIntExact(firstResult));
        nativeQuery.setMaxResults(exportParams.getStopPlaceSearch().getPageable().getPageSize());

        List<StopPlace> stopPlaces = nativeQuery.getResultList();
        return new PageImpl<>(stopPlaces, exportParams.getStopPlaceSearch().getPageable(), stopPlaces.size());

    }

    @Override
    public List<StopPlace> findAll(List<String> stopPlacesNetexIds) {
        final String queryString = "SELECT stopPlace FROM StopPlace stopPlace WHERE stopPlace.netexId IN :netexIds";
        final TypedQuery<StopPlace> typedQuery = entityManager.createQuery(queryString, StopPlace.class);
        typedQuery.setParameter("netexIds", stopPlacesNetexIds);
        return typedQuery.getResultList();
    }

    @Override
    public StopPlace findByQuay(Quay quay) {
        final String queryString = "select s from StopPlace s where :quay member of s.quays";
        final TypedQuery<StopPlace> typedQuery = entityManager.createQuery(queryString, StopPlace.class);
        typedQuery.setParameter("quay", quay);
        return getOneOrNull(typedQuery);
    }

    /**
     * Returns parent stops only if multi modal stops
     * @param search
     * @return
     */
    public Page<StopPlace> findStopPlacesWithEffectiveChangeInPeriod(ChangedStopPlaceSearch search) {
        final String queryString = "select sp.* " + STOP_PLACE_WITH_EFFECTIVE_CHANGE_QUERY_BASE + " order by sp.from_Date";

        long firstResult = search.getPageable().getOffset();

        List<StopPlace> stopPlaces = entityManager.createNativeQuery(queryString, StopPlace.class)
                                             .setParameter("from", Date.from(search.getFrom()))
                                             .setParameter("to", Date.from(search.getTo()))
                                             .setFirstResult(Math.toIntExact(firstResult))
                                             .setMaxResults(search.getPageable().getPageSize())
                                             .getResultList();


        if(logger.isDebugEnabled()) {
            final String generatedSql = basicFormatter.format(queryString.toString());
            logger.debug("sql: {}Search object: {}", generatedSql, search);
        }

        int totalCnt = stopPlaces.size();
        if (totalCnt == search.getPageable().getPageSize()) {
            totalCnt = countStopPlacesWithEffectiveChangeInPeriod(search);
        }

        return new PageImpl<>(stopPlaces, search.getPageable(), totalCnt);
    }

    /**
     * Return jbv code mapping for rail stations. The stop place contains jbc code mapping. The quay contains the public code.
     * @return
     */
    @Override
    public List<JbvCodeMappingDto> findJbvCodeMappingsForStopPlace() {
        String sql = "SELECT DISTINCT vi.items, s.netex_id " +
                "FROM stop_place_key_values skv " +
                "   INNER JOIN stop_place s " +
                "       ON s.id = skv.stop_place_id AND s.stop_place_type = :stopPlaceType " +
                SQL_LEFT_JOIN_PARENT_STOP +
                "   INNER JOIN value_items vi " +
                "       ON skv.key_values_id = vi.value_id AND vi.items NOT LIKE '' AND skv.key_values_key = :mappingIdKeys " +
                "WHERE " + SQL_STOP_PLACE_OR_PARENT_IS_VALID_AT_POINT_IN_TIME +
                "ORDER BY items ";
        Query nativeQuery = entityManager.createNativeQuery(sql);

        nativeQuery.setParameter("stopPlaceType", StopTypeEnumeration.RAIL_STATION.toString());
        nativeQuery.setParameter("mappingIdKeys", Arrays.asList(JBV_CODE));
        nativeQuery.setParameter("pointInTime", Date.from(Instant.now()));

        @SuppressWarnings("unchecked")
        List<Object[]> result = nativeQuery.getResultList();

        List<JbvCodeMappingDto> mappingResult = new ArrayList<>();
        for (Object[] row : result) {
            mappingResult.add(new JbvCodeMappingDto(row[0].toString(), null, row[1].toString()));
        }

        return mappingResult;
    }

    private int countStopPlacesWithEffectiveChangeInPeriod(ChangedStopPlaceSearch search) {
        String queryString = "select count(sp.id) " + STOP_PLACE_WITH_EFFECTIVE_CHANGE_QUERY_BASE;
        return ((Number)entityManager.createNativeQuery(queryString).setParameter("from", Date.from(search.getFrom()))
                       .setParameter("to",  Date.from(search.getTo())).getSingleResult()).intValue();
    }

    private static final String STOP_PLACE_WITH_EFFECTIVE_CHANGE_QUERY_BASE =
            " from stop_place sp INNER JOIN " +
                    "(SELECT spinner.netex_id, MAX(spinner.version) AS maxVersion " +
                    "   FROM stop_place spinner " +
                    " WHERE " +
                    "   (spinner.from_date BETWEEN :from AND :to OR spinner.to_date BETWEEN :from AND :to ) " +
                    "   AND spinner.parent_site_ref IS NULL " +
                    // Make sure we do not fetch stop places that have become children of parent stops in "future" versions
                    "   AND NOT EXISTS( " +
                    "      SELECT sp2.id FROM stop_place sp2 " +
                    "      INNER JOIN stop_place parent " +
                    "        ON parent.netex_id = sp2.parent_site_ref " +
                    "          AND parent.version = CAST(sp2.parent_site_ref_version AS bigint) " +
                    "          AND (parent.from_date BETWEEN :from AND :to OR parent.to_date BETWEEN :from AND :to ) " +
                    "        WHERE sp2.netex_id = spinner.netex_id " +
                    "          AND sp2.version > spinner.version " +
                    "  )" +
                    " GROUP BY spinner.netex_id " +
                    ") sub " +
                    "   ON sub.netex_id = sp.netex_id " +
                    "   AND sub.maxVersion = sp.version";

    private <T> T getOneOrNull(TypedQuery<T> query) {
        try {
            List<T> resultList = query.getResultList();
            return resultList.isEmpty() ? null : resultList.getFirst();
        } catch (NoResultException e) {
            return null;
        }
    }

    private String getOneOrNull(Query query) {
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

    private Set<String> getSetResult(Query query) {
        try {
            @SuppressWarnings("unchecked")
            List<String> results = query.getResultList();
            if (results.isEmpty()) {
                return Sets.newHashSet();
            } else {
                return new HashSet<>(results);
            }
        } catch (NoResultException noResultException) {
            return Sets.newHashSet();
        }
    }

    public int deleteStopPlaceTariffZoneRefs() {
        String sql = "DELETE " +
                        "FROM STOP_PLACE_TARIFF_ZONES " +
                            "WHERE STOP_PLACE_ID IN " +
                                "(SELECT S.ID " +
                                    "FROM STOP_PLACE S " +
                                "LEFT JOIN STOP_PLACE P ON S.PARENT_SITE_REF = P.NETEX_ID " +
                                "AND CAST(S.PARENT_SITE_REF_VERSION AS bigint) = P.VERSION " +
                                    "WHERE ((S.FROM_DATE <= NOW() " +
                                    "AND (S.TO_DATE >= NOW() " +
                                    "OR S.TO_DATE IS NULL)) " +
                                    "OR (P.FROM_DATE <= NOW() " +
                                    "AND (P.TO_DATE >= NOW() " +
                                    "OR P.TO_DATE IS NULL))))";

        final Query nativeQuery = entityManager.createNativeQuery(sql);
        return nativeQuery.executeUpdate();
    }

    @Override
    public Map<String, Map<Long, StopPlace>> findByNetexIdsAndVersions(Map<String, Set<Long>> netexIdToVersions) {
        Map<String, Map<Long, StopPlace>> resultMap = new HashMap<>();
        
        if (netexIdToVersions.isEmpty()) {
            return resultMap;
        }

        logger.debug("Batch loading stop places for {} unique netexIds", netexIdToVersions.size());

        try {
            // Build list of exact (netexId, version) pairs to query for
            List<String> whereClauses = new ArrayList<>();
            Map<String, Object> parameters = new HashMap<>();
            int paramIndex = 0;
            
            for (Map.Entry<String, Set<Long>> entry : netexIdToVersions.entrySet()) {
                String netexId = entry.getKey();
                Set<Long> versions = entry.getValue();
                
                for (Long version : versions) {
                    String netexIdParam = "netexId" + paramIndex;
                    String versionParam = "version" + paramIndex;
                    
                    whereClauses.add("(sp.netexId = :" + netexIdParam + " AND sp.version = :" + versionParam + ")");
                    parameters.put(netexIdParam, netexId);
                    parameters.put(versionParam, version);
                    paramIndex++;
                }
            }
            
            // Create optimized query that hits the composite index directly
            String jpql = "SELECT sp FROM StopPlace sp WHERE " + String.join(" OR ", whereClauses);
            TypedQuery<StopPlace> query = entityManager.createQuery(jpql, StopPlace.class);
            
            // Set all parameters
            for (Map.Entry<String, Object> param : parameters.entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }

            List<StopPlace> stopPlaces = query.getResultList();
            
            // Organize results by netexId and version - no filtering needed since we queried exact pairs
            for (StopPlace stopPlace : stopPlaces) {
                String netexId = stopPlace.getNetexId();
                Long version = stopPlace.getVersion();
                resultMap.computeIfAbsent(netexId, k -> new HashMap<>()).put(version, stopPlace);
            }
            
            logger.debug("Successfully loaded stop places for {}/{} requested netexId/version combinations", 
                resultMap.values().stream().mapToInt(Map::size).sum(), 
                netexIdToVersions.values().stream().mapToInt(Set::size).sum());
                
        } catch (Exception e) {
            logger.error("Batch query failed for findByNetexIdsAndVersions", e);
            throw e;
        }

        return resultMap;
    }
}

