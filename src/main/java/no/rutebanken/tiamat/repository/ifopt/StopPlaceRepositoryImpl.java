package no.rutebanken.tiamat.repository.ifopt;


import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.org.netex.netex.StopPlace;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StopPlaceRepositoryImpl implements StopPlaceRepositoryCustom {

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

        return entityManager.find(StopPlace.class, stopPlaceId, hints(graph));
    }

    private Map<String, Object> hints(EntityGraph<StopPlace> graph) {
        Map<String, Object> hints = new HashMap<>();
        hints.put("javax.persistence.loadgraph", graph);
        return hints;
    }


    /**
     *
     * Find nearby stop places, specifying a bounding box.
     *
     * @param xMin
     * @param yMin
     * @param xMax
     * @param yMax
     * @return
     */

    @Override
    public List<StopPlace> findStopPlacesWithin(double xMin, double yMin, double xMax, double yMax) {

        Envelope envelope = new Envelope(xMin, xMax, yMin, yMax);

        Geometry geometryFilter = geometryFactory.toGeometry(envelope);


        javax.persistence.Query query = entityManager
                .createQuery("SELECT s FROM StopPlace s LEFT OUTER JOIN s.centroid sp WHERE within(sp.location, :filter) = true", StopPlace.class);
        query.setParameter("filter", geometryFilter);

        //4326


        return query.getResultList();



    }
}
