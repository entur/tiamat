package org.rutebanken.tiamat.repository;


import com.google.api.client.repackaged.com.google.common.base.Strings;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Transactional
public class TariffZoneRepositoryImpl implements TariffZoneRepositoryCustom {

	private static final Logger logger = LoggerFactory.getLogger(TariffZoneRepositoryImpl.class);

	@Autowired
	private EntityManager entityManager;

	@Override
	public String findFirstByKeyValues(String key, Set<String> originalIds) {
		throw new NotImplementedException("findFirstByKeyValues not implemented for " + this.getClass().getSimpleName());
	}

	@Override
	public List<TariffZone> getTariffZonesFromStopPlaceIds(Set<Long> stopPlaceIds) {

		StringBuilder sql = new StringBuilder("select tz.* from stop_place_tariff_zones sptz " +
				"inner join tariff_zone tz on sptz.tariff_zones_id = tz.id " +
				"inner join stop_place s on s.id = sptz.stop_place_id " +
				"where s.id in(");

		sql.append(StringUtils.join(stopPlaceIds, ','));
		sql.append(')');

		Query query = entityManager.createNativeQuery(sql.toString(), TariffZone.class);

		@SuppressWarnings("unchecked")
		List<TariffZone> tariffZones = query.getResultList();
		return tariffZones;
	}
}
