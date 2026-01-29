/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.repository;


import com.google.common.base.Strings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.NotImplementedException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.rutebanken.tiamat.exporter.params.TopographicPlaceSearch;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.repository.iterator.ScrollableResultIterator;
import org.rutebanken.tiamat.repository.search.TopographicPlaceQueryFromSearchBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Transactional
public class TopographicPlaceRepositoryImpl implements TopographicPlaceRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;


	@Autowired
	private TopographicPlaceQueryFromSearchBuilder topographicPlaceQueryFromSearchBuilder;

	@Override
	public List<TopographicPlace> findTopographicPlace(TopographicPlaceSearch topographicPlaceSearch) {

		Pair<String, Map<String, Object>> queryWithParams = topographicPlaceQueryFromSearchBuilder.buildQueryString(topographicPlaceSearch);

		final Query nativeQuery = entityManager.createNativeQuery(queryWithParams.getFirst(), TopographicPlace.class);

		queryWithParams.getSecond().forEach(nativeQuery::setParameter);

		List<TopographicPlace> topographicPlaces = nativeQuery.getResultList();
		return topographicPlaces;
	}

	@Override
	public String findFirstByKeyValues(String key, Set<String> originalIds) {
		throw new NotImplementedException("findByKeyvalue not implemented for topographic place");
	}

	@Override
	public List<TopographicPlace> findByNetexIdOrNameAndTypeMaxVersion(String name, TopographicPlaceTypeEnumeration topographicPlaceType) {

		Map<String, Object> parameters = new HashMap<>();
		StringBuilder sql = new StringBuilder("SELECT tp.* FROM topographic_place tp WHERE " +
				"tp.version = (SELECT MAX(tpv.version) FROM topographic_place tpv WHERE tpv.netex_id = tp.netex_id " +
				"and (tpv.to_date is null or tpv.to_date > :pointInTime) and (tpv.from_date is null or tpv.from_date < :pointInTime)) ");
		Instant pointInTime = Instant.now();
		parameters.put("pointInTime", pointInTime);

        if(topographicPlaceType != null) {
            sql.append("AND tp.topographic_place_type = :topographicPlaceType ");
            parameters.put("topographicPlaceType", topographicPlaceType.name());
        }
		//or t.netex_id like concat('%', :query, '%'))
        if(!Strings.isNullOrEmpty(name)) {
            sql.append("AND (similarity(tp.name_value, :name) > 0.2 OR  similarity(tp.netex_id, :name) = 1)");
            parameters.put("name", name);
            sql.append("ORDER BY SIMILARITY(tp.name_value, :name) DESC");
        }

		Query query = entityManager.createNativeQuery(sql.toString(), TopographicPlace.class);
        parameters.forEach(query::setParameter);

		return query.getResultList();
	}

	@Override
	public Iterator<TopographicPlace> scrollTopographicPlaces(Set<Long> stopPlaceDbIds) {

		if(stopPlaceDbIds == null || stopPlaceDbIds.isEmpty()) {
			return new ArrayList<TopographicPlace>().iterator();
		}

		return scrollTopographicPlaces(generateTopographicPlacesQueryFromStopPlaceIds(stopPlaceDbIds));
	}

	@Override
	public Iterator<TopographicPlace> scrollTopographicPlaces() {
		return scrollTopographicPlaces("SELECT t.* FROM topographic_place t");
	}

	public Iterator<TopographicPlace> scrollTopographicPlaces(String sql) {
		Session session = entityManager.unwrap(Session.class);
		NativeQuery sqlQuery = session.createNativeQuery(sql);

		sqlQuery.addEntity(TopographicPlace.class);
		sqlQuery.setReadOnly(true);
		sqlQuery.setFetchSize(1000);
		sqlQuery.setCacheable(false);
		ScrollableResults results = sqlQuery.scroll(ScrollMode.FORWARD_ONLY);
		ScrollableResultIterator<TopographicPlace> topographicPlaceIterator = new ScrollableResultIterator<>(results, 100, session);
		return  topographicPlaceIterator;
	}

	@Override
	public List<TopographicPlace> getTopographicPlacesFromStopPlaceIds(Set<Long> stopPlaceDbIds) {
		if(stopPlaceDbIds == null || stopPlaceDbIds.isEmpty()) {
			return new ArrayList<>();
		}
		Query query = entityManager.createNativeQuery(generateTopographicPlacesQueryFromStopPlaceIds(stopPlaceDbIds), TopographicPlace.class);

		try {
			@SuppressWarnings("unchecked")
			List<TopographicPlace> results = query.getResultList();
			if (results.isEmpty()) {
				return null;
			} else {
				return results;
			}
		} catch (NoResultException noResultException) {
			return null;
		}
	}

	private String generateTopographicPlacesQueryFromStopPlaceIds(Set<Long> stopPlaceDbIds) {

		Set<String> stopPlaceStringDbIds = stopPlaceDbIds.stream().map(lvalue -> String.valueOf(lvalue)).collect(Collectors.toSet());
		String joinedStopPlaceDbIds = String.join(",", stopPlaceStringDbIds);
		StringBuilder sql = new StringBuilder("SELECT tp.* " +
				"FROM ( " +
				"  SELECT tp1.id " +
				"  FROM topographic_place tp1 " +
				"  INNER JOIN stop_place sp " +
				"    ON sp.topographic_place_id = tp1.id " +
				"  WHERE sp.id IN(");
		sql.append(joinedStopPlaceDbIds);
		sql.append(") " +
				"  GROUP BY tp1.id " +
				") tp1 " +
				"JOIN topographic_place tp ON tp.id = tp1.id");
		return sql.toString();
	}

	/**
	 * Updates stop_place.topographic_place_id using a native query with PostGIS spatial intersection.
	 * Checks both persistable_multi_polygon and persistable_polygon for matches.
	 * Prioritizes by:
	 *   1. Admin hierarchy: MUNICIPALITY (1) > COUNTY (2) > COUNTRY (3)
	 *   2. Geometry type: multi_surface (0) > polygon (1)
	 * Only updates currently valid stop places.
	 */
	@Override
	public int updateStopPlaceTopographicPlaceRef() {
		String sql = """
				UPDATE stop_place sp
				SET topographic_place_id = matched.tp_id
				FROM (
				    SELECT DISTINCT ON (sp_id)
				        sp_id,
				        tp_id
				    FROM (
				        -- Match via multi_surface (persistable_multi_polygon) - geometry_priority = 0
				        SELECT
				            sp.id AS sp_id,
				            tp.id AS tp_id,
				            CASE tp.topographic_place_type
				                WHEN 'MUNICIPALITY' THEN 1
				                WHEN 'COUNTY' THEN 2
				                WHEN 'COUNTRY' THEN 3
				                ELSE 4
				            END AS admin_priority,
				            0 AS geometry_priority
				        FROM stop_place sp
				        JOIN persistable_multi_polygon pmp
				            ON ST_Contains(pmp.multi_polygon, sp.centroid)
				        JOIN topographic_place tp
				            ON tp.multi_surface_id = pmp.id
				            AND tp.version = (
				                SELECT MAX(tpv.version)
				                FROM topographic_place tpv
				                WHERE tpv.netex_id = tp.netex_id
				                AND (tpv.to_date IS NULL OR tpv.to_date > NOW())
				                AND (tpv.from_date IS NULL OR tpv.from_date < NOW())
				            )
				        LEFT JOIN stop_place psp
				            ON sp.parent_site_ref = psp.netex_id
				            AND sp.parent_site_ref_version = CAST(psp.version AS text)
				        WHERE
				            sp.centroid IS NOT NULL
				            AND (
				                (
				                    sp.from_date <= NOW()
				                    AND (sp.to_date >= NOW() OR sp.to_date IS NULL)
				                )
				                OR (
				                    psp.from_date <= NOW()
				                    AND (psp.to_date >= NOW() OR psp.to_date IS NULL)
				                )
				            )

				        UNION ALL

				        -- Match via polygon (persistable_polygon) - geometry_priority = 1
				        SELECT
				            sp.id AS sp_id,
				            tp.id AS tp_id,
				            CASE tp.topographic_place_type
				                WHEN 'MUNICIPALITY' THEN 1
				                WHEN 'COUNTY' THEN 2
				                WHEN 'COUNTRY' THEN 3
				                ELSE 4
				            END AS admin_priority,
				            1 AS geometry_priority
				        FROM stop_place sp
				        JOIN persistable_polygon pp
				            ON ST_Contains(pp.polygon, sp.centroid)
				        JOIN topographic_place tp
				            ON tp.polygon_id = pp.id
				            AND tp.version = (
				                SELECT MAX(tpv.version)
				                FROM topographic_place tpv
				                WHERE tpv.netex_id = tp.netex_id
				                AND (tpv.to_date IS NULL OR tpv.to_date > NOW())
				                AND (tpv.from_date IS NULL OR tpv.from_date < NOW())
				            )
				        LEFT JOIN stop_place psp
				            ON sp.parent_site_ref = psp.netex_id
				            AND sp.parent_site_ref_version = CAST(psp.version AS text)
				        WHERE
				            sp.centroid IS NOT NULL
				            AND (
				                (
				                    sp.from_date <= NOW()
				                    AND (sp.to_date >= NOW() OR sp.to_date IS NULL)
				                )
				                OR (
				                    psp.from_date <= NOW()
				                    AND (psp.to_date >= NOW() OR psp.to_date IS NULL)
				                )
				            )
				    ) ranked
				    ORDER BY sp_id, admin_priority ASC, geometry_priority ASC
				) matched
				WHERE sp.id = matched.sp_id
				""";

		final Query query = entityManager.createNativeQuery(sql);
		return query.executeUpdate();
	}
}
