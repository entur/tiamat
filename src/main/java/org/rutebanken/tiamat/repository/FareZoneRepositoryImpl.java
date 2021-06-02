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
import org.rutebanken.tiamat.exporter.params.FareZoneSearch;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.repository.iterator.ScrollableResultIterator;
import org.rutebanken.tiamat.repository.search.FareZoneQueryFromSearchBuilder;
import org.rutebanken.tiamat.repository.search.SearchHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public class FareZoneRepositoryImpl implements FareZoneRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(FareZoneRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SearchHelper searchHelper;

    @Autowired
    private FareZoneQueryFromSearchBuilder fareZoneQueryFromSearchBuilder;

    @Override
    public List<FareZone> findFareZones(FareZoneSearch search) {
        Pair<String, Map<String, Object>> pair = fareZoneQueryFromSearchBuilder.buildQueryFromSearch(search);
        Session session = entityManager.unwrap(SessionImpl.class);
        NativeQuery nativeQuery = session.createNativeQuery(pair.getFirst());
        nativeQuery.addEntity(FareZone.class);

        searchHelper.addParams(nativeQuery, pair.getSecond());

        @SuppressWarnings("unchecked")
        List<FareZone> fareZones = nativeQuery.list();
        return fareZones;
    }

    @Override
    public Optional<FareZone> findValidFareZone(String netexId) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT fz.* FROM fare_zone fz WHERE " +
                "fz.version = (SELECT MAX(fzv.version) FROM fare_zone fzv WHERE fzv.netex_id = fz.netex_id " +
                "and (fzv.to_date is null or fzv.to_date > :pointInTime) and (fzv.from_date is null or fzv.from_date < :pointInTime))");
        Instant pointInTime = Instant.now();
        parameters.put("pointInTime", pointInTime);

        sql.append("AND fz.netex_id =:netexId");
        parameters.put("netexId", netexId);


        Query query = entityManager.createNativeQuery(sql.toString(), FareZone.class);
        parameters.forEach(query::setParameter);

        return query.getResultList().stream().findFirst();
    }
    @Override
    public String findFirstByKeyValues(String key, Set<String> originalIds) {
        throw new NotImplementedException("findFirstByKeyValues not implemented for " + this.getClass().getSimpleName());
    }

    @Override
    public List<FareZone> getFareZonesFromStopPlaceIds(Set<Long> stopPlaceIds) {
        if (stopPlaceIds == null || stopPlaceIds.isEmpty()) {
            return new ArrayList<>();
        }

        Query query = entityManager.createNativeQuery(generateFareZoneQueryFromStopPlaceIds(stopPlaceIds), FareZone.class);

        @SuppressWarnings("unchecked")
        List<FareZone> fareZones = query.getResultList();
        return fareZones;
    }

    @Override
    public Iterator<FareZone> scrollFareZones(Set<Long> stopPlaceDbIds) {

        if (stopPlaceDbIds == null || stopPlaceDbIds.isEmpty()) {
            return new ArrayList<FareZone>().iterator();
        }
        return scrollFareZones(generateFareZoneQueryFromStopPlaceIds(stopPlaceDbIds));
    }

    public Iterator<FareZone> scrollFareZones(String sql) {
        Session session = entityManager.unwrap(Session.class);
        NativeQuery sqlQuery = session.createNativeQuery(sql);

        sqlQuery.addEntity(FareZone.class);
        sqlQuery.setReadOnly(true);
        sqlQuery.setFetchSize(100);
        sqlQuery.setCacheable(false);
        ScrollableResults results = sqlQuery.scroll(ScrollMode.FORWARD_ONLY);
        ScrollableResultIterator<FareZone> fareZoneIterator = new ScrollableResultIterator<>(results, 100, session);
        return fareZoneIterator;
    }

    @Override
    public Iterator<FareZone> scrollFareZones(ExportParams exportParams) {

        var sql = new StringBuilder("select fz.* from fare_zone fz");

        if (exportParams.getStopPlaceSearch() != null && exportParams.getStopPlaceSearch().getVersionValidity() !=null && exportParams.getStopPlaceSearch().getVersionValidity().equals(ExportParams.VersionValidity.CURRENT)) {
            logger.info("Preparing to scroll only current fare zones");
            sql.append(" WHERE " +
                    "fz.version = (SELECT MAX(fzv.version) FROM fare_zone fzv WHERE fzv.netex_id = fz.netex_id " +
                    "and (fzv.to_date is null or fzv.to_date > now()) and (fzv.from_date is null or fzv.from_date < now()))");
        }

        return scrollFareZones(sql.toString());
    }

    private String generateFareZoneQueryFromStopPlaceIds(Set<Long> stopPlaceDbIds) {
        StringBuilder sqlStringBuilder = new StringBuilder("SELECT fz.* " +
                "FROM " +
                "  ( SELECT fz1.id " +
                "   FROM stop_place_tariff_zones sptz " +
                "   INNER JOIN tariff_zone_ref tzr ON sptz.tariff_zones_id = tzr.id " +
                "   AND sptz.stop_place_id IN( ");

        sqlStringBuilder.append(StringUtils.join(stopPlaceDbIds, ','));

        sqlStringBuilder.append(") " +
                "   INNER JOIN fare_zone fz1 ON fz1.netex_id = tzr.ref " +
                "   AND (" +
                "      (" +
                "        tzr.version IS NOT NULL AND cast(fz1.version AS text) = tzr.version" +
                "      )" +
                "      OR (    " +
                "        tzr.version IS NULL AND fz1.version = (" +
                "           SELECT MAX(fz2.version) FROM fare_zone fz2 WHERE fz2.netex_id = fz1.netex_id " +
                "               AND fz2.from_date < NOW()" +
                "              )" +
                "      )" +
                "    ) " +
                "   AND (" +
                "        fz1.to_date IS NULL OR fz1.to_date > NOW()" +
                "       )" +
                "   AND (" +
                "        fz1.from_date < NOW()" +
                "       )" +
                "   GROUP BY fz1.id ) fz1 " +
                "JOIN fare_zone fz ON fz.id = fz1.id");

        String sql = sqlStringBuilder.toString();
        logger.info(sql);
        return sql;
    }

    @Override
    public int countResult(Set<Long> stopPlaceIds) {
        StringBuilder sqlStringBuilder = new StringBuilder("SELECT COUNT(fz.*) " +
                "FROM " +
                "  ( SELECT fz1.id " +
                "   FROM stop_place_tariff_zones sptz " +
                "   INNER JOIN tariff_zone_ref tzr ON sptz.tariff_zones_id = tzr.id " +
                "   AND sptz.stop_place_id IN( ");

        sqlStringBuilder.append(StringUtils.join(stopPlaceIds, ','));

        sqlStringBuilder.append(") " +
                "   INNER JOIN fare_zone fz1 ON fz1.netex_id = tzr.ref " +
                "   AND (" +
                "      (" +
                "        tzr.version IS NOT NULL AND cast(fz1.version AS text) = tzr.version" +
                "      )" +
                "      OR (    " +
                "        tzr.version IS NULL AND fz1.version = (" +
                "           SELECT MAX(fz2.version) FROM fare_zone fz2 WHERE fz2.netex_id = fz1.netex_id " +
                "               AND fz2.from_date < NOW()" +
                "              )" +
                "      )" +
                "    ) " +
                "   AND (" +
                "        fz1.to_date IS NULL OR fz1.to_date > NOW()" +
                "       )" +
                "   AND (" +
                "        fz1.from_date < NOW()" +
                "       )" +
                "   GROUP BY fz1.id ) fz1 " +
                "JOIN fare_zone fz ON fz.id = fz1.id");

        String sql = sqlStringBuilder.toString();
        logger.info(sql);

        Session session = entityManager.unwrap(Session.class);
        NativeQuery query = session.createNativeQuery(sql);
        return ((BigInteger) query.uniqueResult()).intValue();
    }
}
