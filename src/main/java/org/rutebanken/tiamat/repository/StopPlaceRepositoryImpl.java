package org.rutebanken.tiamat.repository;


import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        graph.addAttributeNodes("tariffZones");
        graph.addAttributeNodes("accessSpaces");
        graph.addAttributeNodes("equipmentPlaces");
        graph.addAttributeNodes("validityConditions");
        graph.addAttributeNodes("accessibilityAssessment");
        graph.addAttributeNodes("levels");
        graph.addAttributeNodes("alternativeNames");
        graph.addAttributeNodes("otherTransportModes");
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
                "LEFT OUTER JOIN s.centroid sp " +
                "LEFT OUTER JOIN sp.location l "+
                "WHERE within(l.geometryPoint, :filter) = true " +
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
                                "LEFT OUTER JOIN s.centroid sp " +
                                "LEFT OUTER JOIN sp.location l " +
                             "WHERE within(l.geometryPoint, :filter) = true " +
                            "AND s.name.value = :name", Long.class);
        query.setParameter("filter", geometryFilter);
        query.setParameter("name", name);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Long findByKeyValue(String key, String value) {
        TypedQuery<Long> query = entityManager
                .createQuery("SELECT s.id " +
                        "FROM StopPlace s " +
                            "LEFT OUTER JOIN s.keyList kl " +
                            "LEFT OUTER JOIN kl.keyValue kv " +
                                "WHERE kv.key = :key " +
                                    "AND kv.value = :value",
                        Long.class);
        query.setParameter("key", key);
        query.setParameter("value", value);

        try {
            return query.getSingleResult();
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    @Override
    public Page<StopPlace> findStopPlace(String name, Long municipalityId, Long countyId, List<StopTypeEnumeration> stopPlaceTypes, Pageable pageable) {
        StringBuilder queryString = new StringBuilder("SELECT stopPlace FROM StopPlace stopPlace ");

        List<String> wheres = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();

        if(municipalityId != null){
            wheres.add("stopPlace.topographicPlaceRef.ref = :municipalityId");
            parameters.put("municipalityId", municipalityId.toString());
        }

        if(countyId != null) {
            wheres.add("stopPlace.topographicPlaceRef.ref IN (SELECT municipality.id FROM TopographicPlace municipality WHERE municipality.parentTopographicPlaceRef.ref = :countyId)");
            parameters.put("countyId", countyId.toString());
        }

        if(name != null) {
            wheres.add("lower(stopPlace.name.value) like concat('%', lower(:name), '%')");
            parameters.put("name", name);
        }

        if(stopPlaceTypes != null && !stopPlaceTypes.isEmpty()) {
            wheres.add("stopPlace.stopPlaceType in :stopPlaceTypes");
            parameters.put("stopPlaceTypes", stopPlaceTypes);
        }

        for(int i = 0; i < wheres.size(); i++) {
            if(i > 0) {
                queryString.append(" AND ");
            } else {
                queryString.append("WHERE");
            }
            queryString.append(' ').append(wheres.get(i)).append(' ');
        }


        logger.debug("{}", queryString);
         final TypedQuery<StopPlace> query = entityManager.createQuery(queryString.toString(), StopPlace.class);

        parameters.forEach(query::setParameter);

        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<StopPlace> stopPlaces = query.getResultList();
        return new PageImpl<>(stopPlaces, pageable, stopPlaces.size());

    }

}
