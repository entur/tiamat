package no.rutebanken.tiamat.repository.ifopt;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.org.netex.netex.StopPlace;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

@Repository
public class StopPlaceRepositoryImpl implements StopPlaceRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

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

}
