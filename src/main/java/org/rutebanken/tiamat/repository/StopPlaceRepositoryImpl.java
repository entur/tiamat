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
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.hibernate.*;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.dtoassembling.dto.JbvCodeMappingDto;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;

import static java.util.stream.Collectors.toSet;
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
     * Left join parent stop place p with stop place s on parent site ref and parent site ref version.
     */
    protected static final String SQL_LEFT_JOIN_PARENT_STOP =
            createLeftJoinParentStopQuery("p");

    protected static final String SQL_LEFT_JOIN_PARENT_STOP_TEMPLATE =
            "LEFT JOIN stop_place %s ON s.parent_site_ref = %s.netex_id AND s.parent_site_ref_version = CAST(%s.version as text) ";

    protected static String createLeftJoinParentStopQuery(String parentAlias) {
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
    protected static final String SQL_NOT_PARENT_STOP_PLACE = "s.parent_stop_place = false ";

    /**
     * Ignore netex id for both stop place and its parent
     */
    protected static final String SQL_IGNORE_STOP_PLACE_ID = "(s.netex_id != :ignoreStopPlaceId AND (p.netex_id IS NULL OR p.netex_id != :ignoreStopPlaceId)) ";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private StopPlaceQueryFromSearchBuilder stopPlaceQueryFromSearchBuilder;

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
        Envelope envelope = new Envelope(xMin, xMax, yMin, yMax);

        Geometry geometryFilter = geometryFactory.toGeometry(envelope);

        String queryString = "SELECT s.* FROM stop_place s " +
                                     SQL_LEFT_JOIN_PARENT_STOP +
                                     "WHERE " +
                                        SQL_CHILD_OR_PARENT_WITHIN +
                                        "AND "
                                        + SQL_NOT_PARENT_STOP_PLACE;

        if (pointInTime != null) {
            queryString += "AND " + SQL_STOP_PLACE_OR_PARENT_IS_VALID_AT_POINT_IN_TIME;
        } else {
            // If no point in time is set, use max version to only get one version per stop place
            queryString += "AND " + SQL_MAX_VERSION_OF_STOP_PLACE;
        }

        if(ignoreStopPlaceId != null) {
            queryString += "AND " + SQL_IGNORE_STOP_PLACE_ID;

        }

        logger.debug("finding stops within bounding box with query: {}", queryString);

        final Query query = entityManager.createNativeQuery(queryString, StopPlace.class);
        query.setParameter("filter", geometryFilter);

        if(ignoreStopPlaceId != null) {
            query.setParameter("ignoreStopPlaceId", ignoreStopPlaceId);
        }

        if (pointInTime != null) {
            query.setParameter("pointInTime", Date.from(pointInTime));
        }

        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<StopPlace> stopPlaces = query.getResultList();
        return new PageImpl<>(stopPlaces, pageable, stopPlaces.size());
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
    public List<IdMappingDto> findKeyValueMappingsForStop(Instant pointInTime, int recordPosition, int recordsPerRoundTrip) {
        String sql = "SELECT v.items, s.netex_id, s.stop_place_type " +
                             "FROM stop_place_key_values spkv " +
                             "  INNER JOIN value_items v " +
                             "      ON spkv.key_values_key in (:mappingIdKeys) AND spkv.key_values_id = v.value_id AND v.items NOT LIKE '' " +
                             "  INNER JOIN stop_place s ON s.id = spkv.stop_place_id " +
                             SQL_LEFT_JOIN_PARENT_STOP +
                             "WHERE " +
                              SQL_STOP_PLACE_OR_PARENT_IS_VALID_AT_POINT_IN_TIME +
                             "ORDER BY s.id,spkv.key_values_id";


        Query nativeQuery = entityManager.createNativeQuery(sql).setFirstResult(recordPosition).setMaxResults(recordsPerRoundTrip);

        nativeQuery.setParameter("mappingIdKeys", Arrays.asList(ORIGINAL_ID_KEY, MERGED_ID_KEY));
        nativeQuery.setParameter("pointInTime", Date.from(pointInTime));

        List<Object[]> result = nativeQuery.getResultList();

        List<IdMappingDto> mappingResult = new ArrayList<>();
        for (Object[] row : result) {
            mappingResult.add(new IdMappingDto(row[0].toString(), row[1].toString(), parseStopType(row[2])));
        }

        return mappingResult;
    }


    @Override
    public Set<String> findUniqueStopPlaceIds(Instant pointInTime) {
        String sql = "SELECT DISTINCT s.netex_id FROM stop_place s " +
                        SQL_LEFT_JOIN_PARENT_STOP +
                        "WHERE " +
                        SQL_STOP_PLACE_OR_PARENT_IS_VALID_AT_POINT_IN_TIME +
                        "ORDER BY s.netex_id";


        Query nativeQuery = entityManager.createNativeQuery(sql);

        nativeQuery.setParameter("pointInTime", Date.from(pointInTime));

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
    public Iterator<StopPlace> scrollStopPlaces() {
        Session session = entityManager.unwrap(Session.class);

        Criteria query = session.createCriteria(StopPlace.class);

        query.setReadOnly(true);
        query.setFetchSize(SCROLL_FETCH_SIZE);
        query.setCacheable(false);
        ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

        ScrollableResultIterator<StopPlace> stopPlaceEntityIterator = new ScrollableResultIterator<>(results, SCROLL_FETCH_SIZE, session);

        return stopPlaceEntityIterator;
    }

    @Override
    public Iterator<StopPlace> scrollStopPlaces(ExportParams exportParams) {

        Session session = entityManager.unwrap(Session.class);

        Pair<String, Map<String, Object>> queryWithParams = stopPlaceQueryFromSearchBuilder.buildQueryString(exportParams);
        SQLQuery sqlQuery = session.createSQLQuery(queryWithParams.getFirst());
        stopPlaceQueryFromSearchBuilder.addParams(sqlQuery, queryWithParams.getSecond());;

        sqlQuery.addEntity(StopPlace.class);
        sqlQuery.setReadOnly(true);
        sqlQuery.setFetchSize(SCROLL_FETCH_SIZE);
        sqlQuery.setCacheable(false);
        ScrollableResults results = sqlQuery.scroll(ScrollMode.FORWARD_ONLY);

        ScrollableResultIterator<StopPlace> stopPlaceEntityIterator = new ScrollableResultIterator<>(results, SCROLL_FETCH_SIZE, session);

        return stopPlaceEntityIterator;
    }

    @Override
    public Set<String> getNetexIds(ExportParams exportParams) {
        Pair<String, Map<String, Object>> pair = stopPlaceQueryFromSearchBuilder.buildQueryString(exportParams);
        Session session = entityManager.unwrap(Session.class);
        SQLQuery query = session.createSQLQuery("SELECT sub.netex_id from (" + pair.getFirst() + ") sub");

        stopPlaceQueryFromSearchBuilder.addParams(query, pair.getSecond());

        @SuppressWarnings("unchecked")
        Set<String> result =  new HashSet<>(query.list());
        return result;
    }

    @Override
    public Set<Long> getDatabaseIds(ExportParams exportParams) {
        Pair<String, Map<String, Object>> pair = stopPlaceQueryFromSearchBuilder.buildQueryString(exportParams);
        Session session = entityManager.unwrap(Session.class);
        SQLQuery query = session.createSQLQuery("SELECT sub.id from (" + pair.getFirst() + ") sub");

        stopPlaceQueryFromSearchBuilder.addParams(query, pair.getSecond());

        Set<Long> result = new HashSet<>();
        for(Object object : query.list()) {
            BigInteger bigInteger = (BigInteger) object;
            result.add(bigInteger.longValue());

        }

        return result;
    }

    @Override
    public Page<StopPlace> findStopPlace(ExportParams exportParams) {
        Pair<String, Map<String, Object>> queryWithParams = stopPlaceQueryFromSearchBuilder.buildQueryString(exportParams);

        final Query nativeQuery = entityManager.createNativeQuery(queryWithParams.getFirst(), StopPlace.class);

        queryWithParams.getSecond().forEach(nativeQuery::setParameter);
        nativeQuery.setFirstResult(exportParams.getStopPlaceSearch().getPageable().getOffset());
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
        List<StopPlace> stopPlaces = entityManager.createNativeQuery(queryString, StopPlace.class)
                                             .setParameter("from", Date.from(search.getFrom()))
                                             .setParameter("to", Date.from(search.getTo()))
                                             .setFirstResult(search.getPageable().getOffset())
                                             .setMaxResults(search.getPageable().getPageSize())
                                             .getResultList();


        if(logger.isDebugEnabled()) {
            final String generatedSql = basicFormatter.format(queryString.toString());
            logger.debug("sql: {}\nSearch object: {}", generatedSql, search);
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
                    "          AND cast(parent.version AS TEXT) = sp2.parent_site_ref_version " +
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
            return resultList.isEmpty() ? null : resultList.get(0);
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
                return results.get(0);
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
                return results.stream().collect(toSet());
            }
        } catch (NoResultException noResultException) {
            return Sets.newHashSet();
        }
    }
}

