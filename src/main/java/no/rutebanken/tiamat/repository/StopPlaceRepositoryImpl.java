package no.rutebanken.tiamat.repository;


import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import no.rutebanken.tiamat.model.StopPlace;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
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
    public StopPlace findStopPlaceDetailed(String stopPlaceId) {

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
    public Page<StopPlace> findStopPlacesWithin(double xMin, double yMin, double xMax, double yMax, String ignoreStopPlaceId, Pageable pageable) {
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
    public String findNearbyStopPlace(Envelope envelope, String name) {
        Geometry geometryFilter = geometryFactory.toGeometry(envelope);

        TypedQuery<String> query = entityManager
                .createQuery("SELECT s.id FROM StopPlace s " +
                                "LEFT OUTER JOIN s.centroid sp " +
                                "LEFT OUTER JOIN sp.location l " +
                             "WHERE within(l.geometryPoint, :filter) = true " +
                            "AND s.name.value = :name", String.class);
        query.setParameter("filter", geometryFilter);
        query.setParameter("name", name);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public String findByKeyValue(String key, String value) {
        TypedQuery<String> query = entityManager
                .createQuery("SELECT s.id " +
                        "FROM StopPlace s " +
                            "LEFT OUTER JOIN s.keyList kl " +
                            "LEFT OUTER JOIN kl.keyValue kv " +
                                "WHERE kv.key = :key " +
                                    "AND kv.value = :value",
                        String.class);
        query.setParameter("key", key);
        query.setParameter("value", value);

        try {
            return query.getSingleResult();
        } catch (NoResultException noResultException) {
            return null;
        }
    }
}
