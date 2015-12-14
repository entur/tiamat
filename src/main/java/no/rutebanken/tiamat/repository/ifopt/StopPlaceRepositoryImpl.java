package no.rutebanken.tiamat.repository.ifopt;


import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.org.netex.netex.StopPlace;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StopPlaceRepositoryImpl implements StopPlaceRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    private static final int SRID = 4326;


    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), SRID);


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
     *    @Query("select s from StopPlace s " +
    "left outer join s.centroid sp " +
    "left outer join sp.location l " +
    "where l.latitude like ?1 AND l.longitude like ?2")
     * @param xMin
     * @param yMin
     * @param xMax
     * @param yMax
     * @return
     */

    @Override
    public List<StopPlace> findStopPlacesWithin(BigDecimal xMin, BigDecimal yMin, BigDecimal xMax, BigDecimal yMax) {

        Envelope envelope = new Envelope(xMin.doubleValue(), xMax.doubleValue(), yMin.doubleValue(), yMax.doubleValue());

        Geometry geometryFilter = geometryFactory.toGeometry(envelope);


        javax.persistence.Query query = entityManager
                .createQuery("SELECT s FROM StopPlace s LEFT OUTER JOIN s.centroid sp WHERE within(sp.point, :filter) = true", StopPlace.class);
        query.setParameter("filter", geometryFilter);

        //4326


        return query.getResultList();



    }
}
