package org.rutebanken.tiamat.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.rutebanken.tiamat.model.Quay;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
public class QuayRepositoryImpl implements QuayRepositoryCustom
{
    @Autowired
    private EntityManager entityManager;

	@Autowired
	private GeometryFactory geometryFactory;

	@Override
	public Quay findQuayDetailed(Long quayId) {
		EntityGraph<Quay> graph = entityManager.createEntityGraph(Quay.class);
		graph.addAttributeNodes("alternativeNames");
		graph.addAttributeNodes("accessibilityAssessment");
//		graph.addAttributeNodes("roadAddress");

		return entityManager.find(Quay.class, quayId, hints(graph));
	}

	private Map<String, Object> hints(EntityGraph<Quay> graph) {
		Map<String, Object> hints = new HashMap<>();
		hints.put("javax.persistence.loadgraph", graph);
		return hints;
	}

	@Override
	public Page<Quay> findQuaysWithin(double xMin, double yMin, double xMax, double yMax, Pageable pageable) {
		Envelope envelope = new Envelope(xMin, xMax, yMin, yMax);

		Geometry geometryFilter = geometryFactory.toGeometry(envelope);

		TypedQuery<Quay> query = entityManager.createQuery(
				"SELECT s FROM Quay s LEFT OUTER JOIN s.centroid sp WHERE within(sp.location, :filter) = true",
				Quay.class);
		query.setParameter("filter", geometryFilter);

		query.setFirstResult(pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		List<Quay> quays = query.getResultList();

		return new PageImpl<>(quays, pageable, quays.size());
	}

}
