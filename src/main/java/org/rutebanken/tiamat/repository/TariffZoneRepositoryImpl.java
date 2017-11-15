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
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.repository.iterator.ScrollableResultIterator;
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
import java.util.*;
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
		if(stopPlaceIds == null || stopPlaceIds.isEmpty()) {
			return new ArrayList<>();
		}

		Query query = entityManager.createNativeQuery(generateTariffZoneQueryFromStopPlaceIds(stopPlaceIds), TariffZone.class);

		@SuppressWarnings("unchecked")
		List<TariffZone> tariffZones = query.getResultList();
		return tariffZones;
	}

	@Override
	public Iterator<TariffZone> scrollTariffZones(Set<Long> stopPlaceDbIds) {

		if(stopPlaceDbIds == null || stopPlaceDbIds.isEmpty()) {
			return new ArrayList<TariffZone>().iterator();
		}
		return scrollTariffZones(generateTariffZoneQueryFromStopPlaceIds(stopPlaceDbIds));
	}

	public Iterator<TariffZone> scrollTariffZones(String sql) {
		Session session = entityManager.unwrap(Session.class);
		SQLQuery sqlQuery = session.createSQLQuery(sql);

		sqlQuery.addEntity(TariffZone.class);
		sqlQuery.setReadOnly(true);
		sqlQuery.setFetchSize(100);
		sqlQuery.setCacheable(false);
		ScrollableResults results = sqlQuery.scroll(ScrollMode.FORWARD_ONLY);
		ScrollableResultIterator<TariffZone> tariffZoneIterator = new ScrollableResultIterator<>(results, 100, session);
		return tariffZoneIterator;
	}

	@Override
	public Iterator<TariffZone> scrollTariffZones() {
		return scrollTariffZones("select tz.* from tariff_zone tz");
	}

	private String generateTariffZoneQueryFromStopPlaceIds(Set<Long> stopPlaceDbIds) {
		StringBuilder sql = new StringBuilder("SELECT tz.* " +
				"FROM (SELECT tz2.id " +
				"      FROM stop_place_tariff_zones sptz " +
				"            	inner join tariff_zone_ref tzr " +
				"               	ON sptz.tariff_zones_id = tzr.id " +
				"	                AND sptz.stop_place_id IN(");

		sql.append(StringUtils.join(stopPlaceDbIds, ','));
		sql.append(')');

		sql.append("            inner join tariff_zone tz2 " +
				"                   ON tz2.netex_id = tzr.ref " +
				"                   AND ( tz2.version IS NULL " +
				"                   	OR Cast(tz2.version AS TEXT) = tzr.version ) " +
				"        GROUP BY tz2.id) tz2 " +
				"		 JOIN tariff_zone tz ON tz2.id = tz.id");

		return sql.toString();
	}
}
