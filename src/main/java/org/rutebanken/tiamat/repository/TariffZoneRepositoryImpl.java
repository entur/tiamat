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


import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.hibernate.query.NativeQuery;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.TariffZoneSearch;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.repository.iterator.ScrollableResultIterator;
import org.rutebanken.tiamat.repository.search.SearchHelper;
import org.rutebanken.tiamat.repository.search.TariffZoneQueryFromSearchBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.Instant;
import java.util.*;

@Repository
@Transactional
public class TariffZoneRepositoryImpl implements TariffZoneRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(TariffZoneRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SearchHelper searchHelper;

    @Autowired
    private TariffZoneQueryFromSearchBuilder tariffZoneQueryFromSearchBuilder;

    @Override
    public List<TariffZone> findTariffZones(TariffZoneSearch search) {
        Pair<String, Map<String, Object>> pair = tariffZoneQueryFromSearchBuilder.buildQueryFromSearch(search);
        Session session = entityManager.unwrap(SessionImpl.class);
        NativeQuery nativeQuery = session.createNativeQuery(pair.getFirst());
        nativeQuery.addEntity(TariffZone.class);

        searchHelper.addParams(nativeQuery, pair.getSecond());

        @SuppressWarnings("unchecked")
        List<TariffZone> tariffZones = nativeQuery.list();
        return tariffZones;
    }

    @Override
    public Optional<TariffZone> findValidTariffZone(String netexId) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT tz.* FROM tariff_zone tz WHERE " +
                "tz.version = (SELECT MAX(tzv.version) FROM tariff_zone tzv WHERE tzv.netex_id = tz.netex_id " +
                "and (tzv.to_date is null or tzv.to_date > :pointInTime) and (tzv.from_date is null or tzv.from_date < :pointInTime))");
        Instant pointInTime = Instant.now();
        parameters.put("pointInTime", pointInTime);

        sql.append("AND tz.netex_id =:netexId");
        parameters.put("netexId", netexId);


        Query query = entityManager.createNativeQuery(sql.toString(), TariffZone.class);
        parameters.forEach(query::setParameter);

        return query.getResultList().stream().findFirst();
    }
    @Override
    public String findFirstByKeyValues(String key, Set<String> originalIds) {
        throw new NotImplementedException("findFirstByKeyValues not implemented for " + this.getClass().getSimpleName());
    }

    @Override
    public List<TariffZone> getTariffZonesFromStopPlaceIds(Set<Long> stopPlaceIds) {
        if (stopPlaceIds == null || stopPlaceIds.isEmpty()) {
            return new ArrayList<>();
        }

        Query query = entityManager.createNativeQuery(generateTariffZoneQueryFromStopPlaceIds(stopPlaceIds), TariffZone.class);

        @SuppressWarnings("unchecked")
        List<TariffZone> tariffZones = query.getResultList();
        return tariffZones;
    }

    @Override
    public Iterator<TariffZone> scrollTariffZones(Set<Long> stopPlaceDbIds) {

        if (stopPlaceDbIds == null || stopPlaceDbIds.isEmpty()) {
            return new ArrayList<TariffZone>().iterator();
        }
        return scrollTariffZones(generateTariffZoneQueryFromStopPlaceIds(stopPlaceDbIds));
    }

    public Iterator<TariffZone> scrollTariffZones(String sql) {
        Session session = entityManager.unwrap(Session.class);
        NativeQuery sqlQuery = session.createNativeQuery(sql);

        sqlQuery.addEntity(TariffZone.class);
        sqlQuery.setReadOnly(true);
        sqlQuery.setFetchSize(100);
        sqlQuery.setCacheable(false);
        ScrollableResults results = sqlQuery.scroll(ScrollMode.FORWARD_ONLY);
        return new ScrollableResultIterator<>(results, 100, session);
    }

    @Override
    public Iterator<TariffZone> scrollTariffZones(ExportParams exportParams) {
        var sql = new StringBuilder("select tz.* from tariff_zone tz");

        if (exportParams.getStopPlaceSearch() != null && exportParams.getStopPlaceSearch().getVersionValidity() !=null) {
            if (exportParams.getStopPlaceSearch().getVersionValidity().equals(ExportParams.VersionValidity.CURRENT)) {
                logger.info("Preparing to scroll only current tariff zones");
                sql.append(" WHERE tz.version = (SELECT MAX(tzv.version) FROM tariff_zone fzv WHERE tzv.netex_id = tz.netex_id " +
                        "and (tzv.to_date is null or tzv.to_date > now()) and (tzv.from_date is null or tzv.from_date < now()))");
            } else if (exportParams.getStopPlaceSearch().getVersionValidity().equals(ExportParams.VersionValidity.CURRENT_FUTURE)) {
                logger.info("Preparing to scroll current and future tariff zones");
                sql.append(" WHERE (tz.to_date is null or tz.to_date > now())");
            }
        }

        return scrollTariffZones(sql.toString());
    }

    private String generateTariffZoneQueryFromStopPlaceIds(Set<Long> stopPlaceDbIds) {
        StringBuilder sqlStringBuilder = new StringBuilder("SELECT tz.* " +
                "FROM " +
                "  ( SELECT tz1.id " +
                "   FROM stop_place_tariff_zones sptz " +
                "   INNER JOIN tariff_zone_ref tzr ON sptz.tariff_zones_id = tzr.id " +
                "   AND sptz.stop_place_id IN( ");

        sqlStringBuilder.append(StringUtils.join(stopPlaceDbIds, ','));

        sqlStringBuilder.append(") " +
                "   INNER JOIN tariff_zone tz1 ON tz1.netex_id = tzr.ref " +
                "   AND (" +
                "      (" +
                "        tzr.version IS NOT NULL AND cast(tz1.version AS text) = tzr.version" +
                "      )" +
                "      OR (    " +
                "        tzr.version IS NULL AND tz1.version = (" +
                "           SELECT MAX(tz2.version) FROM tariff_zone tz2 WHERE tz2.netex_id = tz1.netex_id " +
                "               AND tz2.from_date < NOW()" +
                "              )" +
                "      )" +
                "    ) " +
                "   AND (" +
                "        tz1.to_date IS NULL OR tz1.to_date > NOW()" +
                "       )" +
                "   AND (" +
                "        tz1.from_date < NOW()" +
                "       )" +
                "   GROUP BY tz1.id ) tz1 " +
                "JOIN tariff_zone tz ON tz.id = tz1.id");

        String sql = sqlStringBuilder.toString();
        logger.info(sql);
        return sql;
    }
}
