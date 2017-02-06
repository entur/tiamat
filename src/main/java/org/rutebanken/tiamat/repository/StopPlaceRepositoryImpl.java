package org.rutebanken.tiamat.repository;


import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

@Repository
@Transactional
public class StopPlaceRepositoryImpl implements StopPlaceRepositoryCustom {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(StopPlaceRepositoryImpl.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private NetexMapper netexMapper;

    @Override
    public StopPlace findStopPlaceDetailed(Long stopPlaceId) {

        EntityGraph<StopPlace> graph = entityManager.createEntityGraph(StopPlace.class);

        graph.addAttributeNodes("accessSpaces");
        graph.addAttributeNodes("equipmentPlaces");
//        graph.addAttributeNodes("validityConditions");
        graph.addAttributeNodes("accessibilityAssessment");
//        graph.addAttributeNodes("levels");
        graph.addAttributeNodes("alternativeNames");
//        graph.addAttributeNodes("otherTransportModes");
//        graph.addAttributeNodes("roadAddress");
        graph.addAttributeNodes("parentSiteRef");

        // Be aware of https://hibernate.atlassian.net/browse/HHH-10261

        return entityManager.find(StopPlace.class, stopPlaceId, hints(graph));
    }

    private Map<String, Object> hints(EntityGraph<StopPlace> graph) {
        Map<String, Object> hints = new HashMap<>();
        hints.put("javax.persistence.loadgraph", graph);
        return hints;
    }


    /**
     * Find nearby stop places, specifying a bounding box.
     * Optionally, a stop place ID to ignore can be defined.
     */
    @Override
    public Page<StopPlace> findStopPlacesWithin(double xMin, double yMin, double xMax, double yMax, Long ignoreStopPlaceId, Pageable pageable) {
        Envelope envelope = new Envelope(xMin, xMax, yMin, yMax);

        Geometry geometryFilter = geometryFactory.toGeometry(envelope);

        String queryString = "SELECT s FROM StopPlace s " +
                "WHERE within(s.centroid, :filter) = true " +
                "AND (:ignoreStopPlaceId IS NULL OR s.id != :ignoreStopPlaceId)";

        final TypedQuery<StopPlace> query = entityManager.createQuery(queryString, StopPlace.class);
        query.setParameter("filter", geometryFilter);
        query.setParameter("ignoreStopPlaceId", ignoreStopPlaceId);
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<StopPlace> stopPlaces = query.getResultList();
        return new PageImpl<>(stopPlaces, pageable, stopPlaces.size());
    }

    @Override
    public Long findNearbyStopPlace(Envelope envelope, String name, StopTypeEnumeration stopTypeEnumeration) {
        Geometry geometryFilter = geometryFactory.toGeometry(envelope);

        TypedQuery<Long> query = entityManager
                .createQuery("SELECT s.id FROM StopPlace s " +
                             "WHERE within(s.centroid, :filter) = true " +
                            "AND s.name.value = :name " +
                            "AND s.stopPlaceType = :stopPlaceType", Long.class);
        query.setParameter("filter", geometryFilter);
        query.setParameter("stopPlaceType", stopTypeEnumeration);
        query.setParameter("name", name);
        try {
            List<Long> resultList = query.getResultList();
            return  resultList.isEmpty() ? null : resultList.get(0);
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Long> findNearbyStopPlace(Envelope envelope, StopTypeEnumeration stopTypeEnumeration) {
        Geometry geometryFilter = geometryFactory.toGeometry(envelope);

        TypedQuery<Long> query = entityManager
                .createQuery("SELECT s.id FROM StopPlace s " +
                        "WHERE within(s.centroid, :filter) = true " +
                        "AND s.stopPlaceType = :stopPlaceType", Long.class);
        query.setParameter("filter", geometryFilter);
        query.setParameter("stopPlaceType", stopTypeEnumeration);
        try {
            List<Long> resultList = query.getResultList();
            return resultList;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Long findByKeyValue(String key, Set<String> values) {

        Query query = entityManager.createNativeQuery("SELECT stop_place_id " +
                                                        "FROM stop_place_key_values spkv " +
                                                            "INNER JOIN value_items v " +
                                                            "ON spkv.key_values_id = v.value_id " +
                                                        "WHERE  spkv.key_values_key = :key " +
                                                            "AND v.items IN ( :values ) ");

        query.setParameter("key", key);
        query.setParameter("values", values);

        try {
            @SuppressWarnings("unchecked")
            List<BigInteger> results = query.getResultList();
            if(results.isEmpty()) {
                return null;
            } else {
                return results.get(0).longValue();
            }
        } catch (NoResultException noResultException) {
            return null;
        }
    }
    public List<Long> searchByKeyValue(String key, String value) {

        Query query = entityManager.createNativeQuery("SELECT stop_place_id " +
                                                        "FROM stop_place_key_values spkv " +
                                                          "INNER JOIN value_items v " +
                                                            "ON spkv.key_values_id = v.value_id " +
                                                        "WHERE  spkv.key_values_key = :key "+
                                                        "AND v.items LIKE ( :value ) ");

        query.setParameter("key", key);
        query.setParameter("value", "%"+value+"%");

        try {
            @SuppressWarnings("unchecked")
            List<BigInteger> results = query.getResultList();
            if(results.isEmpty()) {
                return null;
            } else {
                return results.stream().map(id -> id.longValue()).collect(Collectors.toList());
            }
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    @Override
    public List<IdMappingDto> findKeyValueMappingsForQuay(int recordPosition, int recordsPerRoundTrip) {
        String sql = "SELECT vi.items, q.quay_id FROM quay_key_values q INNER JOIN stop_place_quays spq on spq.quays_id = q.quay_id INNER JOIN value_items vi ON q.key_values_id = vi.value_id ORDER BY q.quay_id";
        Query nativeQuery = entityManager.createNativeQuery(sql).setFirstResult(recordPosition).setMaxResults(recordsPerRoundTrip);

        @SuppressWarnings("unchecked")
        List<Object[]> result = nativeQuery.getResultList();

        List<IdMappingDto> mappingResult = new ArrayList<>();
        for (Object[] row : result) {
            mappingResult.add(new IdMappingDto("Quay", (String)row[0], (BigInteger)row[1]));
        }

        return mappingResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IdMappingDto> findKeyValueMappingsForStop(int recordPosition, int recordsPerRoundTrip) {
        String sql = "select v.items, spkv.stop_place_id from stop_place_key_values spkv inner join value_items v on spkv.key_values_id = v.value_id";
        Query nativeQuery = entityManager.createNativeQuery(sql).setFirstResult(recordPosition).setMaxResults(recordsPerRoundTrip);

        List<Object[]> result = nativeQuery.getResultList();

        List<IdMappingDto> mappingResult = new ArrayList<>();
        for (Object[] row : result) {
            mappingResult.add(new IdMappingDto("StopPlace", (String)row[0], (BigInteger)row[1]));
        }

        return mappingResult;
    }

    public static final org.rutebanken.netex.model.StopPlace POISON_PILL = new org.rutebanken.netex.model.StopPlace();
    static {
        POISON_PILL.setId("POISON");
    }

    @Override
    public BlockingQueue<org.rutebanken.netex.model.StopPlace> scrollStopPlaces() throws InterruptedException {

        final int fetchSize = 100;

        BlockingQueue<org.rutebanken.netex.model.StopPlace> blockingQueue = new ArrayBlockingQueue<>(fetchSize);

        Session session = entityManager.getEntityManagerFactory().createEntityManager().unwrap(Session.class);

        Criteria query = session.createCriteria(StopPlace.class);

        query.setReadOnly(true);
        query.setFetchSize(fetchSize);

        ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

        Thread thread = new Thread(() -> {
            int counter = 0;
            try {
                while (results.next()) {
                    Object row = results.get()[0];
                    StopPlace stopPlace = (StopPlace) row;

                    if(++counter % fetchSize == 0) {
                        logger.info("Scrolling stop places. Counter is currently at {}", counter);
                    }


                    blockingQueue.put(netexMapper.mapToNetexModel(stopPlace));
                }
            } catch (InterruptedException e) {
                logger.warn("Got interupted while scrolling stop place results", e);
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e) {
                logger.warn("Got exception while scrolling stop place results", e);
            } finally {
                logger.info("Closing scrollable results and adding poison pill to queue. Counter ended at {}", counter);
                results.close();
                blockingQueue.add(POISON_PILL);
            }
        });
        thread.setName("scroll-results");
        thread.start();
        return blockingQueue;
    }

    @Override
    public Page<StopPlace> findStopPlace(StopPlaceSearch stopPlaceSearch) {

        StringBuilder queryString = new StringBuilder("select stopPlace from StopPlace stopPlace ");

        List<String> wheres = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        List<String> operators = new ArrayList<>();

        boolean hasIdFilter = stopPlaceSearch.getIdList() != null && !stopPlaceSearch.getIdList().isEmpty();

        if(hasIdFilter) {
            wheres.add("stopPlace.id in :idList");
            parameters.put("idList", stopPlaceSearch.getIdList());
        } else {
            if (stopPlaceSearch.getQuery() != null) {
                parameters.put("query", stopPlaceSearch.getQuery());
                if (Longs.tryParse(stopPlaceSearch.getQuery()) != null) {
                    wheres.add("concat('', id) like concat('%', :query, '%')");
                } else {
                    if (stopPlaceSearch.getQuery().length() <= 3) {
                        wheres.add("lower(stopPlace.name.value) like concat(lower(:query), '%')");
                    } else {
                        wheres.add("lower(stopPlace.name.value) like concat('%', lower(:query), '%')");
                    }
                }
                operators.add("and");
            }

            if (stopPlaceSearch.getStopTypeEnumerations() != null && !stopPlaceSearch.getStopTypeEnumerations().isEmpty()) {
                wheres.add("stopPlace.stopPlaceType in :stopPlaceTypes");
                parameters.put("stopPlaceTypes", stopPlaceSearch.getStopTypeEnumerations());
                operators.add("and");
            }

            boolean hasMunicipalityFilter = stopPlaceSearch.getMunicipalityIds() != null && !stopPlaceSearch.getMunicipalityIds().isEmpty();
            boolean hasCountyFilter = stopPlaceSearch.getCountyIds() != null && !stopPlaceSearch.getCountyIds().isEmpty();

            if (hasMunicipalityFilter && !hasIdFilter) {
                String prefix;
                if (hasCountyFilter) {
                    operators.add("or");
                    prefix = "(";
                } else prefix = "";

                wheres.add(prefix + "stopPlace.topographicPlace.id in :municipalityId");
                parameters.put("municipalityId", Lists.transform(stopPlaceSearch.getMunicipalityIds(), Long::valueOf));
            }

            if (hasCountyFilter && !hasIdFilter) {
                String suffix = hasMunicipalityFilter ? ")" : "";
                wheres.add("stopPlace.topographicPlace.id in (select concat('', municipality.id) from TopographicPlace municipality where municipality.parentTopographicPlace.id in :countyId)" + suffix);
                parameters.put("countyId", Lists.transform(stopPlaceSearch.getCountyIds(), Long::valueOf));
            }
        }



        for(int i = 0; i < wheres.size(); i++) {
            if(i > 0) {
                queryString.append(operators.get(i-1));
            } else {
                queryString.append("where");
            }
            queryString.append(' ').append(wheres.get(i)).append(' ');
        }


        logger.debug("{}", queryString);
        final TypedQuery<StopPlace> typedQuery = entityManager.createQuery(queryString.toString(), StopPlace.class);

        parameters.forEach(typedQuery::setParameter);

        typedQuery.setFirstResult(stopPlaceSearch.getPageable().getOffset());
        typedQuery.setMaxResults(stopPlaceSearch.getPageable().getPageSize());

        List<StopPlace> stopPlaces = typedQuery.getResultList();
        return new PageImpl<>(stopPlaces, stopPlaceSearch.getPageable(), stopPlaces.size());

    }

}
