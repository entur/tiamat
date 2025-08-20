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


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
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
        NativeQuery nativeQuery = session.createNativeQuery(pair.getFirst(),FareZone.class);
        nativeQuery.addEntity(FareZone.class);

        searchHelper.addParams(nativeQuery, pair.getSecond());

        @SuppressWarnings("unchecked")
        List<FareZone> fareZones = nativeQuery.list();
        return fareZones;
    }

    @Override
    public List<FareZone> findValidFareZones(List<String> netexIds) {
        if (netexIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Object> parameters = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT fz.* FROM fare_zone fz WHERE " +
                "fz.version = (SELECT MAX(fzv.version) FROM fare_zone fzv WHERE fzv.netex_id = fz.netex_id " +
                "and (fzv.to_date is null or fzv.to_date > :pointInTime) and (fzv.from_date is null or fzv.from_date < :pointInTime))");
        Instant pointInTime = Instant.now();
        parameters.put("pointInTime", pointInTime);

        sql.append("AND fz.netex_id in(:netexIds)");
        parameters.put("netexIds", netexIds);


        Query query = entityManager.createNativeQuery(sql.toString(), FareZone.class);
        parameters.forEach(query::setParameter);

        return query.getResultList();
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
    public List<FareZone> findAllValidFareZones() {
        Map<String, Object> parameters = new HashMap<>();
        String sql = "SELECT fz.* FROM fare_zone fz WHERE " +
                "fz.version = (SELECT MAX(fzv.version) FROM fare_zone fzv WHERE fzv.netex_id = fz.netex_id " +
                "and (fzv.to_date is null or fzv.to_date > :pointInTime) and (fzv.from_date is null or fzv.from_date < :pointInTime))";
        Instant pointInTime = Instant.now();
        parameters.put("pointInTime", pointInTime);

        Query query = entityManager.createNativeQuery(sql, FareZone.class);
        parameters.forEach(query::setParameter);

        return query.getResultList();
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
        NativeQuery sqlQuery = session.createNativeQuery(sql,FareZone.class);

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

        if (exportParams.getStopPlaceSearch() != null && exportParams.getStopPlaceSearch().getVersionValidity() !=null) {
            if (exportParams.getStopPlaceSearch().getVersionValidity().equals(ExportParams.VersionValidity.CURRENT)) {
                logger.info("Preparing to scroll only current fare zones");
                sql.append(" WHERE fz.version = (SELECT MAX(fzv.version) FROM fare_zone fzv WHERE fzv.netex_id = fz.netex_id " +
                        "and (fzv.to_date is null or fzv.to_date > now()) and (fzv.from_date is null or fzv.from_date < now()))");
            } else if (exportParams.getStopPlaceSearch().getVersionValidity().equals(ExportParams.VersionValidity.CURRENT_FUTURE)) {
                logger.info("Preparing to scroll current and future fare zones");
                sql.append(" WHERE (fz.to_date is null or fz.to_date > now())");
            }
        }

        return scrollFareZones(sql.toString());
    }

    private String generateFareZoneQueryFromStopPlaceIds(Set<Long> stopPlaceDbIds) {
        var sql = "select" +
                "        fz.* " +
                "    from" +
                "        (         select" +
                "            fz1.id         " +
                "        from" +
                "            stop_place_tariff_zones sptz     " +
                "        inner join" +
                "            fare_zone fz1              " +
                "                ON fz1.netex_id = sptz.ref         " +
                "                AND  sptz.stop_place_id IN(" + StringUtils.join(stopPlaceDbIds,',') +
                "                )            " +
                "                AND (" +
                "                    (" +
                "                        sptz.version IS NOT NULL                          " +
                "                        AND cast(fz1.version AS text) = sptz.version                           " +
                "                    )                                   " +
                "                    OR (" +
                "                        sptz.version IS NULL                          " +
                "                        AND fz1.version = (" +
                "                            SELECT" +
                "                                MAX(fz2.version)                          " +
                "                        FROM" +
                "                            fare_zone fz2                          " +
                "                        WHERE" +
                "                            fz2.netex_id = fz1.netex_id                                                         " +
                "                            AND fz2.from_date < NOW()                                   " +
                "                    )                       " +
                "                )                 " +
                "            )           " +
                "        GROUP BY" +
                "            fz1.id      ) fz1      " +
                "        join" +
                "            fare_zone fz      " +
                "                on fz.id = fz1.id";

        logger.info(sql);
        return sql;

    }

    @Override
    public int countResult(Set<Long> stopPlaceIds) {

        StringBuilder sqlStringBuilder = new StringBuilder("SELECT COUNT(f.*) " +
            "FROM " +
            "  ( SELECT " +
            "fz1.netex_id," +
            "fz1.version " +
            "   FROM fare_zone fz1 " +
            "   INNER JOIN stop_place_tariff_zones sptz ON fz1.netex_id = sptz.ref " +
            "   AND cast(fz1.version as text) = sptz.version " +
            "   AND sptz.stop_place_id IN( ");

        sqlStringBuilder.append(StringUtils.join(stopPlaceIds, ','));

        sqlStringBuilder.append(") " +
                "   GROUP BY fz1.netex_id,fz1.version ) fz " +
                "JOIN fare_zone f ON fz.netex_id = f.netex_id AND fz.version=f.version");

        String sql = sqlStringBuilder.toString();
        logger.info(sql);

        Session session = entityManager.unwrap(Session.class);
        NativeQuery query = session.createNativeQuery(sql,Long.class);
        return ((Long) query.uniqueResult()).intValue();
    }

    public void updateStopPlaceTariffZoneRef() {
        final Query explicitStopsQuery = entityManager.createNativeQuery(generateSqlQuery(true));
        final Query implicitStopsQuery = entityManager.createNativeQuery(generateSqlQuery(false));

        explicitStopsQuery.executeUpdate();
        implicitStopsQuery.executeUpdate();
    }

    private String generateSqlQuery(boolean explicitStops) {
        final String subQuery = getSubQuery(explicitStops);


        String sql= "INSERT " +
                "    INTO" +
                "        STOP_PLACE_TARIFF_ZONES" +
                "        SELECT" +
                "            SP.ID," +
                "            FZ.NETEX_ID," +
                "            CAST(FZ.VERSION AS text)  " +
                "        FROM" +
                "            FARE_ZONE FZ " +
                subQuery +
                "            AND FZ.VERSION =  (SELECT" +
                "                MAX(FZV.VERSION)   " +
                "            FROM" +
                "                FARE_ZONE FZV   " +
                "            WHERE" +
                "                FZV.NETEX_ID = FZ.NETEX_ID    " +
                "                AND (" +
                "                    FZV.TO_DATE IS NULL         " +
                "                    OR FZV.TO_DATE > NOW()" +
                "                )    " +
                "                AND (" +
                "                    FZV.FROM_DATE IS NULL         " +
                "                    OR FZV.FROM_DATE < NOW()" +
                "                ))                " +
                "        LEFT JOIN" +
                "            STOP_PLACE PSP " +
                "                ON SP.PARENT_SITE_REF = PSP.NETEX_ID " +
                "                AND CAST(sp.parent_site_ref_version as bigint) = psp.version  " +
                "        where" +
                "            (" +
                "                (" +
                "                    SP.FROM_DATE <= NOW()       " +
                "                    AND (" +
                "                        SP.TO_DATE >= NOW()            " +
                "                        OR SP.TO_DATE IS NULL" +
                "                    )" +
                "                )      " +
                "                OR (" +
                "                    PSP.FROM_DATE <= NOW()          " +
                "                    AND (" +
                "                        PSP.TO_DATE >= NOW()               " +
                "                        OR PSP.TO_DATE IS NULL" +
                "                    )" +
                "                )    " +
                "            )";

        logger.debug(sql);
        return sql;
    }

    private static String getSubQuery(boolean explicitStops) {
        String subQuery;
        if(explicitStops) {
            subQuery = "       JOIN" +
                    "                      FARE_ZONE_MEMBERS FZM " +
                    "                                ON FZM.FARE_ZONE_ID = FZ.ID " +
                    "                                JOIN" +
                    "                                  STOP_PLACE SP " +
                    "                                    ON SP.NETEX_ID = FZM.REF" +
                    "                                    AND FZ.SCOPING_METHOD='EXPLICIT_STOPS'";
        } else {
            subQuery = "        JOIN" +
                    "                     PERSISTABLE_POLYGON PP " +
                    "                       ON PP.ID = FZ.POLYGON_ID " +
                    "                        JOIN" +
                    "                         STOP_PLACE SP " +
                    "                           ON ST_CONTAINS(PP.POLYGON,SP.CENTROID) " +
                    "                           AND FZ.SCOPING_METHOD='IMPLICIT_SPATIAL_PROJECTION' ";
        }
        return subQuery;
    }

    public List<String> findAllFareZoneAuthorities() {

        String sql = "SELECT DISTINCT transport_organisation_ref FROM fare_zone";
        logger.info(sql);

        Session session = entityManager.unwrap(Session.class);
        NativeQuery query = session.createNativeQuery(sql,String.class);
        return  query.getResultList();
    }
}
