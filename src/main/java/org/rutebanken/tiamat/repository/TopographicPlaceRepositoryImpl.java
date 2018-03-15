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


import com.google.api.client.repackaged.com.google.common.base.Strings;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.*;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.iterator.ScrollableResultIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class TopographicPlaceRepositoryImpl implements TopographicPlaceRepositoryCustom {

	private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceRepositoryImpl.class);

	@Autowired
	private EntityManager entityManager;


	@Override
	public String findFirstByKeyValues(String key, Set<String> originalIds) {
		throw new NotImplementedException("findByKeyvalue not implemented for topographic place");
	}

	@Override
	public List<TopographicPlace> findByNameAndTypeMaxVersion(String name, TopographicPlaceTypeEnumeration topographicPlaceType) {

		StringBuilder sql = new StringBuilder("SELECT tp FROM TopographicPlace tp WHERE " +
				"tp.version = (SELECT MAX(tpv.version) FROM TopographicPlace tpv WHERE tpv.netexId = tp.netexId) ");

		Map<String, Object> parameters = new HashMap<>();

		if(!Strings.isNullOrEmpty(name)) {
			sql.append("AND LOWER(tp.name.value) LIKE CONCAT('%', LOWER(:name), '%')");
			parameters.put("name", name);
		}

		if(topographicPlaceType != null) {
			sql.append("AND tp.topographicPlaceType = :topographicPlaceType ");
			parameters.put("topographicPlaceType", topographicPlaceType);
		}

		sql.append("ORDER BY SIMILARITY(tp.name_value, :name) DESC");

		TypedQuery<TopographicPlace> query = entityManager.createQuery(sql.toString(), TopographicPlace.class);
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
		SQLQuery sqlQuery = session.createSQLQuery(sql);

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
}
