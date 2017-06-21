package org.rutebanken.tiamat.repository;


import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.hibernate.*;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.*;

import static java.util.stream.Collectors.toSet;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

@Repository
@Transactional
public class StopPlaceRepositoryImpl implements StopPlaceRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceRepositoryImpl.class);

    private static final int SCROLL_FETCH_SIZE = 100;

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

        String queryString = "SELECT s FROM StopPlace s " +
                                     "WHERE within(s.centroid, :filter) = true " +
                                     "AND s.version = (SELECT MAX(sv.version) FROM StopPlace sv WHERE sv.netexId = s.netexId) " +
                                     "AND (:ignoreStopPlaceId IS NULL OR s.netexId != :ignoreStopPlaceId) ";
        if (pointInTime != null) {
            queryString += "AND ((s.validBetween.fromDate IS NULL OR s.validBetween.fromDate <= :pointInTime) AND (s.validBetween.toDate IS NULL OR s.validBetween.toDate > :pointInTime))";
        }

        final TypedQuery<StopPlace> query = entityManager.createQuery(queryString, StopPlace.class);
        query.setParameter("filter", geometryFilter);
        query.setParameter("ignoreStopPlaceId", ignoreStopPlaceId);

        if (pointInTime != null) {
            query.setParameter("pointInTime", pointInTime);
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
                             "WHERE ST_Within(s.centroid, :filter) = true " +
                             "AND s.version = (SELECT MAX(sv.version) FROM stop_place sv WHERE sv.netex_id = s.netex_id) " +
                             "AND s.stop_place_type = :stopPlaceType) sub " +
                             "WHERE sub.sim > 0.6 " +
                             "ORDER BY sub.sim DESC LIMIT 1";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("filter", geometryFilter);
        query.setParameter("stopPlaceType", stopTypeEnumeration.toString());
        query.setParameter("name", name);
        return getOneOrNull(query);
    }

    @Override
    public String findNearbyStopPlace(Envelope envelope, String name) {
        Geometry geometryFilter = geometryFactory.toGeometry(envelope);

        TypedQuery<String> query = entityManager
                                           .createQuery("SELECT s.netexId FROM StopPlace s " +
                                                                "WHERE within(s.centroid, :filter) = true " +
                                                                "AND s.version = (SELECT MAX(sv.version) FROM StopPlace sv WHERE sv.netexId = s.netexId) " +
                                                                "AND s.name.value = :name ",
                                                   String.class);
        query.setParameter("filter", geometryFilter);
        query.setParameter("name", name);
        return getOneOrNull(query);
    }

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

    @Override
    public List<String> findNearbyStopPlace(Envelope envelope, StopTypeEnumeration stopTypeEnumeration) {
        Geometry geometryFilter = geometryFactory.toGeometry(envelope);

        TypedQuery<String> query = entityManager
                                           .createQuery("SELECT s.netexId FROM StopPlace s " +
                                                                "WHERE within(s.centroid, :filter) = true " +
                                                                "AND s.version = (SELECT MAX(sv.version) FROM StopPlace sv WHERE sv.netexId = s.netexId) " +
                                                                "AND s.stopPlaceType = :stopPlaceType", String.class);
        query.setParameter("filter", geometryFilter);
        query.setParameter("stopPlaceType", stopTypeEnumeration);
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

        StringBuilder sqlQuery = new StringBuilder("SELECT s.netex_id " +
                                                           "FROM stop_place s " +
                                                           "INNER JOIN stop_place_key_values spkv " +
                                                           "ON spkv.stop_place_id = s.id " +
                                                           "INNER JOIN value_items v " +
                                                           "ON spkv.key_values_id = v.value_id " +
                                                           "WHERE spkv.key_values_key = :key " +
                                                           "AND s.version = (SELECT MAX(sv.version) FROM stop_place sv WHERE sv.netex_id = s.netex_id) ");


        List<String> parameters = new ArrayList<>(values.size());
        List<String> parametervalues = new ArrayList<>(values.size());
        final String parameterPrefix = "value";
        sqlQuery.append(" AND (");
        Iterator<String> valuesIterator = values.iterator();
        for (int parameterCounter = 0; parameterCounter < values.size(); parameterCounter++) {
            sqlQuery.append(" v.items LIKE :value").append(parameterCounter);
            parameters.add(parameterPrefix + parameterCounter);
            parametervalues.add("%" + valuesIterator.next());
            if (parameterCounter + 1 < values.size()) {
                sqlQuery.append(" OR ");
            }
        }

        sqlQuery.append(" )");

        Query query = entityManager.createNativeQuery(sqlQuery.toString());

        Iterator<String> iterator = parametervalues.iterator();
        parameters.forEach(parameter -> query.setParameter(parameter, iterator.next()));
        query.setParameter("key", key);

        return getSetResult(query);
    }

    public List<String> searchByKeyValue(String key, String value) {

        Query query = entityManager.createNativeQuery("SELECT s.netex_id " +
                                                              "FROM stop_place_key_values spkv " +
                                                              "INNER JOIN value_items v " +
                                                              "ON spkv.key_values_id = v.value_id " +
                                                              "INNER JOIN stop_place s " +
                                                              "ON spkv.stop_place_id = s.id " +
                                                              "WHERE  spkv.key_values_key = :key " +
                                                              "AND v.items LIKE ( :value ) " +
                                                              "AND s.version = (SELECT MAX(sv.version) FROM stop_place sv WHERE sv.netex_id = s.netex_id)");

        query.setParameter("key", key);
        query.setParameter("value", "%" + value + "%");

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

    // Does not belong here. Move it to QuayRepository.
    @Override
    public List<IdMappingDto> findKeyValueMappingsForQuay(int recordPosition, int recordsPerRoundTrip) {
        String sql = "SELECT vi.items, q.netex_id " +
                             "FROM quay_key_values qkv " +
                             "INNER JOIN stop_place_quays spq " +
                             "ON spq.quays_id = qkv.quay_id " +
                             "INNER JOIN quay q " +
                             "ON (spq.quays_id = q.id " +
                             "AND q.version = (SELECT MAX(qv.version) FROM quay qv WHERE qv.netex_id = q.netex_id)) " +
                             "INNER JOIN value_items vi " +
                             "ON qkv.key_values_id = vi.value_id AND vi.items NOT LIKE '' AND qkv.key_values_key = :originalIdKey";
        Query nativeQuery = entityManager.createNativeQuery(sql).setFirstResult(recordPosition).setMaxResults(recordsPerRoundTrip);

        nativeQuery.setParameter("originalIdKey", ORIGINAL_ID_KEY);

        @SuppressWarnings("unchecked")
        List<Object[]> result = nativeQuery.getResultList();

        List<IdMappingDto> mappingResult = new ArrayList<>();
        for (Object[] row : result) {
            mappingResult.add(new IdMappingDto((String) row[0].toString(), (String) row[1].toString()));
        }

        return mappingResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IdMappingDto> findKeyValueMappingsForStop(int recordPosition, int recordsPerRoundTrip) {
        String sql = "SELECT v.items, s.netex_id " +
                             "FROM stop_place_key_values spkv " +
                             "INNER JOIN value_items v " +
                             "ON spkv.key_values_key = :originalIdKey AND spkv.key_values_id = v.value_id AND v.items NOT LIKE '' " +
                             "INNER JOIN stop_place s " +
                             "ON s.id = spkv.stop_place_id " +
                             "AND s.version = (SELECT MAX(sv.version) FROM stop_place sv WHERE sv.netex_id = s.netex_id)";

        Query nativeQuery = entityManager.createNativeQuery(sql).setFirstResult(recordPosition).setMaxResults(recordsPerRoundTrip);

        nativeQuery.setParameter("originalIdKey", ORIGINAL_ID_KEY);

        List<Object[]> result = nativeQuery.getResultList();

        List<IdMappingDto> mappingResult = new ArrayList<>();
        for (Object[] row : result) {
            mappingResult.add(new IdMappingDto((String) row[0], (String) row[1]));
        }

        return mappingResult;
    }

    @Override
    public List<String> findStopPlaceFromQuayOriginalId(String quayOriginalId) {
        String sql = "SELECT DISTINCT s.netex_id " +
                             "FROM stop_place s " +
                             "  INNER JOIN stop_place_quays spq " +
                             "    ON s.id = spq.stop_place_id " +
                             "  INNER JOIN quay q " +
                             "    ON spq.quays_id = q.id " +
                             "  INNER JOIN quay_key_values qkv " +
                             "    ON q.id = qkv.quay_id AND qkv.key_values_key = :originalIdKey " +
                             "  INNER JOIN value_items vi " +
                             "    ON vi.value_id = qkv.key_values_id AND vi.items LIKE :value ";

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("value", "%:" + quayOriginalId);
        query.setParameter("originalIdKey", ORIGINAL_ID_KEY);

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


    @Override
    public Iterator<StopPlace> scrollStopPlaces() {
        Session session = entityManager.getEntityManagerFactory().createEntityManager().unwrap(Session.class);

        Criteria query = session.createCriteria(StopPlace.class);

        query.setReadOnly(true);
        query.setFetchSize(SCROLL_FETCH_SIZE);
        query.setCacheable(false);
        ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

        ScrollableResultIterator<StopPlace> stopPlaceEntityIterator = new ScrollableResultIterator<>(results, SCROLL_FETCH_SIZE, session);

        return stopPlaceEntityIterator;
    }

    @Override
    public Iterator<StopPlace> scrollStopPlaces(StopPlaceSearch stopPlaceSearch) {

        Session session = entityManager.getEntityManagerFactory().createEntityManager().unwrap(Session.class);

        Pair<String, Map<String, Object>> queryWithParams = stopPlaceQueryFromSearchBuilder.buildQueryString(stopPlaceSearch);
        SQLQuery sqlQuery = session.createSQLQuery(queryWithParams.getFirst());
        queryWithParams.getSecond().forEach((parameter, value) -> {
                    if (value instanceof Collection) {
                        sqlQuery.setParameterList(parameter, (Collection) value);
                    } else {
                        sqlQuery.setParameter(parameter, value);
                    }
                });

        sqlQuery.addEntity(StopPlace.class);
        sqlQuery.setReadOnly(true);
        sqlQuery.setFetchSize(SCROLL_FETCH_SIZE);
        sqlQuery.setCacheable(false);
        ScrollableResults results = sqlQuery.scroll(ScrollMode.FORWARD_ONLY);

        ScrollableResultIterator<StopPlace> stopPlaceEntityIterator = new ScrollableResultIterator<>(results, SCROLL_FETCH_SIZE, session);

        return stopPlaceEntityIterator;
    }

    @Override
    public Page<StopPlace> findStopPlace(StopPlaceSearch stopPlaceSearch) {
        Pair<String, Map<String, Object>> queryWithParams = stopPlaceQueryFromSearchBuilder.buildQueryString(stopPlaceSearch);

        final Query nativeQuery = entityManager.createNativeQuery(queryWithParams.getFirst(), StopPlace.class);

        queryWithParams.getSecond().forEach(nativeQuery::setParameter);
        nativeQuery.setFirstResult(stopPlaceSearch.getPageable().getOffset());
        nativeQuery.setMaxResults(stopPlaceSearch.getPageable().getPageSize());

        List<StopPlace> stopPlaces = nativeQuery.getResultList();
        return new PageImpl<>(stopPlaces, stopPlaceSearch.getPageable(), stopPlaces.size());

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

    public Page<StopPlace> findStopPlacesWithEffectiveChangeInPeriod(ChangedStopPlaceSearch search) {
        final String queryString = "select sp.* " + STOP_PLACE_WITH_EFFECTIVE_CHANGE_QUERY_BASE + " order by sp.from_Date";
        List<StopPlace> stopPlaces = entityManager.createNativeQuery(queryString, StopPlace.class)
                                             .setParameter("from", Date.from(search.getFrom()))
                                             .setParameter("to", Date.from(search.getTo()))
                                             .setFirstResult(search.getPageable().getOffset())
                                             .setMaxResults(search.getPageable().getPageSize())
                                             .getResultList();

        int totalCnt = stopPlaces.size();
        if (totalCnt == search.getPageable().getPageSize()) {
            totalCnt = countStopPlacesWithEffectiveChangeInPeriod(search);
        }

        return new PageImpl<>(stopPlaces, search.getPageable(), totalCnt);
    }

    private int countStopPlacesWithEffectiveChangeInPeriod(ChangedStopPlaceSearch search) {
        String queryString = "select count(sp.id) " + STOP_PLACE_WITH_EFFECTIVE_CHANGE_QUERY_BASE;
        return ((Number)entityManager.createNativeQuery(queryString).setParameter("from", Date.from(search.getFrom()))
                       .setParameter("to",  Date.from(search.getTo())).getSingleResult()).intValue();
    }

    private static final String STOP_PLACE_WITH_EFFECTIVE_CHANGE_QUERY_BASE = " from  stop_place sp inner join " +
                                                                                      "(select spinner.netex_id, max(spinner.from_date) as maxFromDate from stop_place spinner " +
                                                                                      " where (spinner.from_date between  :from and :to or spinner.to_date between  :from and :to ) " +
                                                                                      " group by  spinner.netex_id" +
                                                                                      ") sub on sub.netex_id=sp.netex_id and sub.maxFromDate=sp.from_date";
}

