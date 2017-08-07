package org.rutebanken.tiamat.repository;


import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.model.*;
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

		TypedQuery<TopographicPlace> query = entityManager.createQuery(sql.toString(), TopographicPlace.class);
		parameters.forEach(query::setParameter);

		return query.getResultList();
	}


	@Override
	public List<TopographicPlace> getTopographicPlacesFromStopPlaceIds(Set<Long> stopPlaceDbIds) {
		if(stopPlaceDbIds == null || stopPlaceDbIds.isEmpty()) {
			return new ArrayList<>();
		}
		StringBuilder sql = new StringBuilder("select tp.* from topographic_place tp inner join stop_place sp on sp.topographic_place_id = tp.id where sp.id in(");

		Set<String> stopPlaceStringDbIds = stopPlaceDbIds.stream().map(lvalue -> String.valueOf(lvalue)).collect(Collectors.toSet());
		sql.append(String.join(",", stopPlaceStringDbIds));
		sql.append(")");


		Query query = entityManager.createNativeQuery(sql.toString(), TopographicPlace.class);

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
}
