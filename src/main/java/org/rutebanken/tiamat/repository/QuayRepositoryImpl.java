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

import javax.persistence.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Transactional
public class QuayRepositoryImpl implements QuayRepositoryCustom
{
    @Autowired
    private EntityManager entityManager;

	@Autowired
	private GeometryFactory geometryFactory;

	@Override
	public Page<Quay> findQuaysWithin(double xMin, double yMin, double xMax, double yMax, Pageable pageable) {
		Envelope envelope = new Envelope(xMin, xMax, yMin, yMax);

		Geometry geometryFilter = geometryFactory.toGeometry(envelope);

		TypedQuery<Quay> query = entityManager.createQuery(
				"SELECT s FROM Quay s " +
					"LEFT OUTER JOIN s.centroid sp " +
					"WHERE within(sp.location, :filter) = true" +
						" AND s.version = (SELECT MAX(sv.version) FROM Quay sv WHERE sv.netexId = s.netexId)",
				Quay.class);
		query.setParameter("filter", geometryFilter);

		query.setFirstResult(pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		List<Quay> quays = query.getResultList();

		return new PageImpl<>(quays, pageable, quays.size());
	}

	@Override
	public String findFirstByKeyValues(String key, Set<String> values) {

		Query query = entityManager.createNativeQuery("SELECT q.netex_id " +
				"FROM quay_key_values qkv " +
					"INNER JOIN value_items v " +
						"ON qkv.key_values_id = v.value_id " +
					"INNER JOIN quay q " +
						"ON quay_id = quay_id " +
				"WHERE qkv.key_values_key = :key " +
					"AND v.items IN ( :values ) " +
					"AND q.version = (SELECT MAX(qv.version) FROM quay qv WHERE q.netex_id = qv.netex_id)");

		query.setParameter("key", key);
		query.setParameter("values", values);

		try {
			@SuppressWarnings("unchecked")
			List<String> results = query.getResultList();
			if(results.isEmpty()) {
				return null;
			} else {
				return results.get(0);
			}
		} catch (NoResultException noResultException) {
			return null;
		}
	}
	
}
