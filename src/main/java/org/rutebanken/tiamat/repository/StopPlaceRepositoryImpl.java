package org.rutebanken.tiamat.repository;


import com.google.common.primitives.Longs;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.postgresql.core.NativeQuery;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.*;

@Repository
@Transactional
public class StopPlaceRepositoryImpl implements StopPlaceRepositoryCustom {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(StopPlaceRepositoryImpl.class);

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private GeometryFactory geometryFactory;

    @Override
    public StopPlace findStopPlaceDetailed(Long stopPlaceId) {

        EntityGraph<StopPlace> graph = entityManager.createEntityGraph(StopPlace.class);

        graph.addAttributeNodes("accessSpaces");
        graph.addAttributeNodes("equipmentPlaces");
        graph.addAttributeNodes("validityConditions");
        graph.addAttributeNodes("accessibilityAssessment");
//        graph.addAttributeNodes("levels");
        graph.addAttributeNodes("alternativeNames");
//        graph.addAttributeNodes("otherTransportModes");
        graph.addAttributeNodes("roadAddress");
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
    public Long findNearbyStopPlace(Envelope envelope, String name) {
        Geometry geometryFilter = geometryFactory.toGeometry(envelope);

        TypedQuery<Long> query = entityManager
                .createQuery("SELECT s.id FROM StopPlace s " +
                             "WHERE within(s.centroid, :filter) = true " +
                            "AND s.name.value = :name", Long.class);
        query.setParameter("filter", geometryFilter);
        query.setParameter("name", name);
        try {
            List<Long> resultList = query.getResultList();
            return  resultList.isEmpty() ? null : resultList.get(0);
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

    @Override
    public List<IdMappingDto> findAllKeyValueMappings() {
        TypedQuery<IdMappingDto> query = entityManager.createQuery(
                "SELECT NEW " + IdMappingDto.class.getCanonicalName() + "('Quay', id, VALUE(keyValues)) FROM Quay "
                , IdMappingDto.class);

        return query.getResultList();
    }

    @Override
    public Page<StopPlace> findStopPlace(String query, List<String> municipalityIds, List<String> countyIds, List<StopTypeEnumeration> stopPlaceTypes, Pageable pageable) {
        StringBuilder queryString = new StringBuilder("select stopPlace from StopPlace stopPlace ");

        List<String> wheres = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        List<String> operators = new ArrayList<>();

        if(query != null) {
            parameters.put("query", query);
            if(Longs.tryParse(query) != null) {
                wheres.add("concat('', id) like concat('%', :query, '%')");
            } else {
                if(query.length() <= 3) {
                  wheres.add("lower(stopPlace.name.value) like concat(lower(:query), '%')");
                } else {
                    wheres.add("lower(stopPlace.name.value) like concat('%', lower(:query), '%')");
                }
            }
            operators.add("and");
        }

        if(stopPlaceTypes != null && !stopPlaceTypes.isEmpty()) {
            wheres.add("stopPlace.stopPlaceType in :stopPlaceTypes");
            parameters.put("stopPlaceTypes", stopPlaceTypes);
            operators.add("and");
        }

        boolean hasMunicipalityFilter = municipalityIds != null && !municipalityIds.isEmpty();
        boolean hasCountyFilter = countyIds != null && !countyIds.isEmpty();

        if(hasMunicipalityFilter){
            String prefix;
            if(hasCountyFilter) {
                operators.add("or");
                prefix = "(";
            } else prefix = "";

            wheres.add(prefix+"stopPlace.topographicPlaceRef.ref in :municipalityId");
            parameters.put("municipalityId", municipalityIds);
        }

        if(hasCountyFilter) {
            String posix = hasMunicipalityFilter ? ")" : "";
            wheres.add("stopPlace.topographicPlaceRef.ref in (select concat('', municipality.id) from TopographicPlace municipality where municipality.parentTopographicPlaceRef.ref in :countyId)"+posix);
            parameters.put("countyId", countyIds);
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

        typedQuery.setFirstResult(pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<StopPlace> stopPlaces = typedQuery.getResultList();
        return new PageImpl<>(stopPlaces, pageable, stopPlaces.size());

    }

}
