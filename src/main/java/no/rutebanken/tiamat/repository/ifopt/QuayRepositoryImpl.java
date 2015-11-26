package no.rutebanken.tiamat.repository.ifopt;

import org.springframework.beans.factory.annotation.Autowired;
import uk.org.netex.netex.Quay;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

public class QuayRepositoryImpl implements QuayRepositoryCustom
{
    @Autowired
    private EntityManager entityManager;

    @Override
    public Quay findQuayDetailed(String quayId) {
        EntityGraph<Quay> graph = entityManager.createEntityGraph(Quay.class);
        graph.addAttributeNodes("alternativeNames");
        graph.addAttributeNodes("accessibilityAssessment");
        graph.addAttributeNodes("roadAddress");


        return entityManager.find(Quay.class, quayId, hints(graph));
    }

    private Map<String, Object> hints(EntityGraph<Quay> graph) {
        Map<String, Object> hints = new HashMap<>();
        hints.put("javax.persistence.loadgraph", graph);
        return hints;
    }
}
