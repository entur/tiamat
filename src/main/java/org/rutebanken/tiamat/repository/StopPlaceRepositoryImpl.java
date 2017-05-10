package org.rutebanken.tiamat.repository;


import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

@Repository
@Transactional
public class StopPlaceRepositoryImpl implements StopPlaceRepositoryCustom {

	private static final Logger logger = LoggerFactory.getLogger(StopPlaceRepositoryImpl.class);

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private GeometryFactory geometryFactory;

	/**
	 * Find nearby stop places, specifying a bounding box.
	 * Optionally, a stop place ID to ignore can be defined.
	 */
	@Override
	public Page<StopPlace> findStopPlacesWithin(double xMin, double yMin, double xMax, double yMax, String ignoreStopPlaceId, Pageable pageable) {
		Envelope envelope = new Envelope(xMin, xMax, yMin, yMax);

		Geometry geometryFilter = geometryFactory.toGeometry(envelope);

		String queryString = "SELECT s FROM StopPlace s " +
				                     "WHERE within(s.centroid, :filter) = true " +
				                     "AND s.version = (SELECT MAX(sv.version) FROM StopPlace sv WHERE sv.netexId = s.netexId) " +
				                     "AND (:ignoreStopPlaceId IS NULL OR s.netexId != :ignoreStopPlaceId)";

		final TypedQuery<StopPlace> query = entityManager.createQuery(queryString, StopPlace.class);
		query.setParameter("filter", geometryFilter);
		query.setParameter("ignoreStopPlaceId", ignoreStopPlaceId);
		query.setFirstResult(pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		List<StopPlace> stopPlaces = query.getResultList();
		return new PageImpl<>(stopPlaces, pageable, stopPlaces.size());
	}

	@Override
	public String findNearbyStopPlace(Envelope envelope, String name, StopTypeEnumeration stopTypeEnumeration) {
		Geometry geometryFilter = geometryFactory.toGeometry(envelope);

		TypedQuery<String> query = entityManager
				                           .createQuery("SELECT s.netexId FROM StopPlace s " +
						                                        "WHERE within(s.centroid, :filter) = true " +
						                                        "AND s.version = (SELECT MAX(sv.version) FROM StopPlace sv WHERE sv.netexId = s.netexId) " +
						                                        "AND s.name.value = :name " +
						                                        "AND s.stopPlaceType = :stopPlaceType", String.class);
		query.setParameter("filter", geometryFilter);
		query.setParameter("stopPlaceType", stopTypeEnumeration);
		query.setParameter("name", name);
		return getOneOrNull(query);
	}

	@Override
	public String findNearbyStopPlace(Envelope envelope, String name) {
		Geometry geometryFilter = geometryFactory.toGeometry(envelope);

		TypedQuery<String> query = entityManager
				.createQuery("SELECT s.netexId FROM StopPlace s " +
						"WHERE within(s.centroid, :filter) = true " +
						"AND s.version = (SELECT MAX(sv.version) FROM StopPlace sv WHERE sv.netexId = s.netexId) " +
						"AND s.name.value = :name ",
						String.class);
		query.setParameter("filter", geometryFilter);
		query.setParameter("name", name);
		return getOneOrNull(query);
	}

	private <T> T getOneOrNull(TypedQuery<T> query) {
		try {
			List<T> resultList = query.getResultList();
			return resultList.isEmpty() ? null : resultList.get(0);
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<String> findNearbyStopPlace(Envelope envelope, StopTypeEnumeration stopTypeEnumeration) {
		Geometry geometryFilter = geometryFactory.toGeometry(envelope);

		TypedQuery<String> query = entityManager
				                           .createQuery("SELECT s.netexId FROM StopPlace s " +
						                                        "WHERE within(s.centroid, :filter) = true " +
						                                        "AND s.version = (SELECT MAX(sv.version) FROM StopPlace sv WHERE sv.netexId = s.netexId) " +
						                                        "AND s.stopPlaceType = :stopPlaceType", String.class);
		query.setParameter("filter", geometryFilter);
		query.setParameter("stopPlaceType", stopTypeEnumeration);
		try {
			List<String> resultList = query.getResultList();
			return resultList;
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Find stop place's netex ID by key value
	 *
	 * @param key    key in key values for stop
	 * @param values list of values to check for
	 * @return stop place's netex ID
	 */
	@Override
	public String findByKeyValue(String key, Set<String> values) {

		Query query = entityManager.createNativeQuery("SELECT s.netex_id " +
				                                              "FROM stop_place s " +
				                                              "INNER JOIN stop_place_key_values spkv " +
				                                              "ON spkv.stop_place_id = s.id " +
				                                              "INNER JOIN value_items v " +
				                                              "ON spkv.key_values_id = v.value_id " +
				                                              "WHERE spkv.key_values_key = :key " +
				                                              "AND v.items IN ( :values ) " +
				                                              "AND s.version = (SELECT MAX(sv.version) FROM stop_place sv WHERE sv.netex_id = s.netex_id)");

		query.setParameter("key", key);
		query.setParameter("values", values);

		try {
			@SuppressWarnings("unchecked")
			List<String> results = query.getResultList();
			if (results.isEmpty()) {
				return null;
			} else {
				return results.get(0);
			}
		} catch (NoResultException noResultException) {
			return null;
		}
	}

	public List<String> searchByKeyValue(String key, String value) {

		Query query = entityManager.createNativeQuery("SELECT s.netex_id " +
				                                              "FROM stop_place_key_values spkv " +
				                                              "INNER JOIN value_items v " +
				                                              "ON spkv.key_values_id = v.value_id " +
				                                              "INNER JOIN stop_place s " +
				                                              "ON spkv.stop_place_id = s.id " +
				                                              "WHERE  spkv.key_values_key = :key " +
				                                              "AND v.items LIKE ( :value ) " +
				                                              "AND s.version = (SELECT MAX(sv.version) FROM stop_place sv WHERE sv.netex_id = s.netex_id)");

		query.setParameter("key", key);
		query.setParameter("value", "%" + value + "%");

		try {
			@SuppressWarnings("unchecked")
			List<String> results = query.getResultList();
			if (results.isEmpty()) {
				return null;
			} else {
				return results;
			}
		} catch (NoResultException noResultException) {
			return null;
		}
	}

	// Does not belong here. Move it to QuayRepository.
	@Override
	public List<IdMappingDto> findKeyValueMappingsForQuay(int recordPosition, int recordsPerRoundTrip) {
		String sql = "SELECT vi.items, q.netex_id " +
				             "FROM quay_key_values qkv " +
				             	"INNER JOIN stop_place_quays spq " +
				             		"ON spq.quays_id = qkv.quay_id " +
				             	"INNER JOIN quay q " +
				             		"ON (spq.quays_id = q.id " +
									"AND q.version = (SELECT MAX(qv.version) FROM quay qv WHERE qv.netex_id = q.netex_id)) "+
				             	"INNER JOIN value_items vi " +
									"ON qkv.key_values_id = vi.value_id AND vi.items NOT LIKE '' AND qkv.key_values_key = :originalIdKey";
		Query nativeQuery = entityManager.createNativeQuery(sql).setFirstResult(recordPosition).setMaxResults(recordsPerRoundTrip);

		nativeQuery.setParameter("originalIdKey", ORIGINAL_ID_KEY);

		@SuppressWarnings("unchecked")
		List<Object[]> result = nativeQuery.getResultList();

		List<IdMappingDto> mappingResult = new ArrayList<>();
		for (Object[] row : result) {
			mappingResult.add(new IdMappingDto((String) row[0].toString(), (String) row[1].toString()));
		}

		return mappingResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IdMappingDto> findKeyValueMappingsForStop(int recordPosition, int recordsPerRoundTrip) {
		String sql = "SELECT v.items, s.netex_id " +
				             "FROM stop_place_key_values spkv " +
				             	"INNER JOIN value_items v " +
				             		"ON spkv.key_values_key = :originalIdKey AND spkv.key_values_id = v.value_id AND v.items NOT LIKE '' " +
				             	"INNER JOIN stop_place s " +
				             		"ON s.id = spkv.stop_place_id " +
				 			 		"AND s.version = (SELECT MAX(sv.version) FROM stop_place sv WHERE sv.netex_id = s.netex_id)";

		Query nativeQuery = entityManager.createNativeQuery(sql).setFirstResult(recordPosition).setMaxResults(recordsPerRoundTrip);

		nativeQuery.setParameter("originalIdKey", ORIGINAL_ID_KEY);

		List<Object[]> result = nativeQuery.getResultList();

		List<IdMappingDto> mappingResult = new ArrayList<>();
		for (Object[] row : result) {
			mappingResult.add(new IdMappingDto((String) row[0], (String) row[1]));
		}

		return mappingResult;
	}

	@Override
	public List<String> findStopPlaceFromQuayOriginalId(String quayOriginalId) {
		String sql = "SELECT s.netex_id " +
				"FROM stop_place s " +
				"  INNER JOIN stop_place_quays spq " +
				"    ON s.id = spq.stop_place_id " +
				"  INNER JOIN quay q " +
				"    ON spq.quays_id = q.id " +
				"  INNER JOIN quay_key_values qkv " +
				"    ON q.id = qkv.quay_id AND qkv.key_values_key = :originalIdKey " +
				"  INNER JOIN value_items vi " +
				"    ON vi.value_id = qkv.key_values_id AND vi.items LIKE :value ";

		Query query = entityManager.createNativeQuery(sql);

		query.setParameter("value",  "%:" + quayOriginalId);
		query.setParameter("originalIdKey", ORIGINAL_ID_KEY);

		try {
			@SuppressWarnings("unchecked")
			List<String> results = query.getResultList();
			if (results.isEmpty()) {
				return null;
			} else {
				return results;
			}
		} catch (NoResultException noResultException) {
			return null;
		}
	}


	@Override
	public Iterator<StopPlace> scrollStopPlaces() throws InterruptedException {
		return scrollStopPlaces(null);
	}

	@Override
	public Iterator<StopPlace> scrollStopPlaces(List<String> stopPlaceNetexIds) throws InterruptedException {

		final int fetchSize = 100;

		Session session = entityManager.getEntityManagerFactory().createEntityManager().unwrap(Session.class);

		Criteria query = session.createCriteria(StopPlace.class);
		if (stopPlaceNetexIds != null) {
			query.add(Restrictions.in("netexId", stopPlaceNetexIds));
		}

		query.setReadOnly(true);
		query.setFetchSize(fetchSize);
		query.setCacheable(false);
		ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

		ScrollableResultIterator<StopPlace> stopPlaceEntityIterator = new ScrollableResultIterator<>(results, fetchSize, session);

		return stopPlaceEntityIterator;
	}

	@Override
	public Page<StopPlace> findStopPlace(StopPlaceSearch stopPlaceSearch) {

		StringBuilder queryString = new StringBuilder("select stopPlace from StopPlace stopPlace ");

		List<String> wheres = new ArrayList<>();
		Map<String, Object> parameters = new HashMap<>();
		List<String> operators = new ArrayList<>();
		List<String> orderByStatements = new ArrayList<>();

		boolean hasIdFilter = stopPlaceSearch.getNetexIdList() != null && !stopPlaceSearch.getNetexIdList().isEmpty();

		if (hasIdFilter) {
			wheres.add("stopPlace.netexId in :netexIdList");
			parameters.put("netexIdList", stopPlaceSearch.getNetexIdList());
		} else {
			if (stopPlaceSearch.getQuery() != null) {
				parameters.put("query", stopPlaceSearch.getQuery());

				if (stopPlaceSearch.getQuery().length() <= 3) {
					wheres.add("(lower(stopPlace.name.value) like concat(lower(:query), '%')");
				} else {
					wheres.add("(lower(stopPlace.name.value) like concat('%', lower(:query), '%')");
				}
				operators.add("or");
				wheres.add("netexId like concat('%:', :query))");
				operators.add("and");
				orderByStatements.add("similarity(stopPlace.name.value, :query) desc");
			}

			if (stopPlaceSearch.getStopTypeEnumerations() != null && !stopPlaceSearch.getStopTypeEnumerations().isEmpty()) {
				wheres.add("stopPlace.stopPlaceType in :stopPlaceTypes");
				parameters.put("stopPlaceTypes", stopPlaceSearch.getStopTypeEnumerations());
				operators.add("and");
			}

			boolean hasMunicipalityFilter = stopPlaceSearch.getMunicipalityIds() != null && !stopPlaceSearch.getMunicipalityIds().isEmpty();
			boolean hasCountyFilter = stopPlaceSearch.getCountyIds() != null && !stopPlaceSearch.getCountyIds().isEmpty();

			if (hasMunicipalityFilter && !hasIdFilter) {
				String prefix;
				if (hasCountyFilter) {
					operators.add("or");
					prefix = "(";
				} else prefix = "";

				wheres.add(prefix + "stopPlace.topographicPlace.netexId in :municipalityId");
				parameters.put("municipalityId", stopPlaceSearch.getMunicipalityIds());
			}

			if (hasCountyFilter && !hasIdFilter) {
				String suffix = hasMunicipalityFilter ? ")" : "";
				wheres.add("stopPlace.topographicPlace.netexId in (select municipality.netexId from TopographicPlace municipality where municipality.parentTopographicPlaceRef.ref in :countyId)" + suffix);
				parameters.put("countyId", stopPlaceSearch.getCountyIds());
			}
		}

		if (stopPlaceSearch.getVersion() != null) {
			operators.add("and");
			wheres.add("stopPlace.version = :version");
			parameters.put("version", stopPlaceSearch.getVersion());
		} else if (!stopPlaceSearch.isAllVersions()) {
			operators.add("and");
			wheres.add("version = (select max(sv.version) from StopPlace sv where sv.netexId = stopPlace.netexId)");
		}

		for (int i = 0; i < wheres.size(); i++) {
			if (i > 0) {
				queryString.append(operators.get(i - 1));
			} else {
				queryString.append("where");
			}
			queryString.append(' ').append(wheres.get(i)).append(' ');
		}

		orderByStatements.add("netexId, version asc");
		queryString.append(" order by");

		for (int i = 0; i < orderByStatements.size(); i++) {
			if (i > 0) {
				queryString.append(',');
			}
			queryString.append(' ').append(orderByStatements.get(i)).append(' ');
		}

		logger.debug("{}", queryString);
		final TypedQuery<StopPlace> typedQuery = entityManager.createQuery(queryString.toString(), StopPlace.class);

		parameters.forEach(typedQuery::setParameter);

		typedQuery.setFirstResult(stopPlaceSearch.getPageable().getOffset());
		typedQuery.setMaxResults(stopPlaceSearch.getPageable().getPageSize());

		List<StopPlace> stopPlaces = typedQuery.getResultList();
		return new PageImpl<>(stopPlaces, stopPlaceSearch.getPageable(), stopPlaces.size());

	}

	@Override
	public List<StopPlace> findAll(List<String> stopPlacesNetexIds) {
		final String queryString = "SELECT stopPlace FROM StopPlace stopPlace WHERE stopPlace.netexId IN :netexIds";
		final TypedQuery<StopPlace> typedQuery = entityManager.createQuery(queryString, StopPlace.class);
		typedQuery.setParameter("netexIds", stopPlacesNetexIds);
		return typedQuery.getResultList();
	}

	@Override
	public StopPlace findByQuay(Quay quay) {
		final String queryString = "select s from StopPlace s where :quay member of s.quays";
		final TypedQuery<StopPlace> typedQuery = entityManager.createQuery(queryString, StopPlace.class);
		typedQuery.setParameter("quay", quay);
		return getOneOrNull(typedQuery);
	}
}
