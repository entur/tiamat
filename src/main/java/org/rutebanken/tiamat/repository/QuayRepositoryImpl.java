package org.rutebanken.tiamat.repository;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.dtoassembling.dto.JbvCodeMappingDto;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.*;

import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.MERGED_ID_KEY;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;
import static org.rutebanken.tiamat.repository.StopPlaceRepositoryImpl.SQL_LEFT_JOIN_PARENT_STOP;
import static org.rutebanken.tiamat.repository.StopPlaceRepositoryImpl.SQL_STOP_PLACE_OR_PARENT_IS_VALID_AT_POINT_IN_TIME;

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

	@Override
	public List<IdMappingDto> findKeyValueMappingsForQuay(Instant pointInTime, int recordPosition, int recordsPerRoundTrip) {
		String sql = "SELECT vi.items, q.netex_id, s.stop_place_type " +
				"FROM quay_key_values qkv " +
				"	INNER JOIN stop_place_quays spq " +
				"		ON spq.quays_id = qkv.quay_id " +
				"	INNER JOIN quay q " +
				"		ON spq.quays_id = q.id " +
				"	INNER JOIN stop_place s " +
				"		ON s.id= spq.stop_place_id " +
				"	INNER JOIN value_items vi " +
				"		ON qkv.key_values_id = vi.value_id AND vi.items NOT LIKE '' AND qkv.key_values_key in (:mappingIdKeys) " +
				SQL_LEFT_JOIN_PARENT_STOP +
				"WHERE " +
				SQL_STOP_PLACE_OR_PARENT_IS_VALID_AT_POINT_IN_TIME +
                "ORDER BY q.id,qkv.key_values_id";


		Query nativeQuery = entityManager.createNativeQuery(sql).setFirstResult(recordPosition).setMaxResults(recordsPerRoundTrip);

		nativeQuery.setParameter("mappingIdKeys", Arrays.asList(ORIGINAL_ID_KEY, MERGED_ID_KEY));
		nativeQuery.setParameter("pointInTime", Date.from(pointInTime));
		@SuppressWarnings("unchecked")
		List<Object[]> result = nativeQuery.getResultList();

		List<IdMappingDto> mappingResult = new ArrayList<>();
		for (Object[] row : result) {
			mappingResult.add(new IdMappingDto(row[0].toString(), row[1].toString(), parseStopType(row[2])));
		}

		return mappingResult;
	}

	private StopTypeEnumeration parseStopType(Object o) {
		if (o != null) {
			return StopTypeEnumeration.valueOf(o.toString());
		}
		return null;
	}

	@Override
	public List<JbvCodeMappingDto> findJbvCodeMappingsForQuay() {
		String sql ="SELECT DISTINCT vi.items, q.public_code, q.netex_id " +
				"FROM stop_place_key_values qkv " +
				"INNER JOIN stop_place_quays spq " +
				"ON spq.stop_place_id = qkv.stop_place_id " +
				"INNER JOIN stop_place sp " +
				"ON sp.id = qkv.stop_place_id and sp.stop_place_type = :stopPlaceType " +
				"INNER JOIN quay q " +
				"ON spq.quays_id = q.id  and q.public_code NOT LIKE '' " +
				"INNER JOIN value_items vi " +
				"ON qkv.key_values_id = vi.value_id AND vi.items NOT LIKE '' AND qkv.key_values_key = :mappingIdKeys " +
				"order by items, public_code ";
		Query nativeQuery = entityManager.createNativeQuery(sql);

		nativeQuery.setParameter("stopPlaceType", StopTypeEnumeration.RAIL_STATION.toString());
		nativeQuery.setParameter("mappingIdKeys", Arrays.asList("jbvCode"));
		@SuppressWarnings("unchecked")
		List<Object[]> result = nativeQuery.getResultList();

		List<JbvCodeMappingDto> mappingResult = new ArrayList<>();
		for (Object[] row : result) {
			mappingResult.add(new JbvCodeMappingDto(row[0].toString(), row[1].toString(), row[2].toString()));
		}

		return mappingResult;
	}
	
}
