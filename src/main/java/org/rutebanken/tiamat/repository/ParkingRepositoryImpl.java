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
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.rutebanken.tiamat.exporter.params.ParkingSearch;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.ParkingTypeEnumeration;
import org.rutebanken.tiamat.repository.iterator.ScrollableResultIterator;
import org.rutebanken.tiamat.repository.search.ParkingQueryFromSearchBuilder;
import org.rutebanken.tiamat.repository.search.SearchHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@Transactional
public class ParkingRepositoryImpl implements ParkingRepositoryCustom {

    /**
     * When selecting parkings and there are multiple versions of the same parking by netex_id, and you only need the highest version by number.
     */
    protected static final String SQL_MAX_VERSION_OF_PARKING = "p.version = (select max(pv.version) from parking pv where pv.netex_id = p.netex_id) ";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private SearchHelper searchHelper;

    @Autowired
    private ParkingQueryFromSearchBuilder parkingQueryFromSearchBuilder;

    /**
     * Find stop place's netex ID by key value
     *
     * @param key    key in key values for stop
     * @param values list of values to check for
     * @return stop place's netex ID
     */
    @Override
    public String findFirstByKeyValues(String key, Set<String> values) {

        Query query = entityManager.createNativeQuery("SELECT p.netex_id " +
                                                        "FROM parking p " +
                                                        "INNER JOIN parking_key_values pkv " +
                                                        "ON pkv.parking_id = p.id " +
                                                        "INNER JOIN value_items v " +
                                                        "ON pkv.key_values_id = v.value_id " +
                                                        "WHERE pkv.key_values_key = :key " +
                                                        "AND v.items IN ( :values ) " +
                                                        "AND p.version = (SELECT MAX(pv.version) FROM parking pv WHERE pv.netex_id = p.netex_id)");

        query.setParameter("key", key);
        query.setParameter("values", values);

        try {
            @SuppressWarnings("unchecked")
            List<String> results = query.getResultList();
            if (results.isEmpty()) {
                return null;
            } else {
                return results.getFirst();
            }
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    @Override
    public Iterator<Parking> scrollParkings(ParkingSearch parkingSearch) {
        return scrollParkings(parkingQueryFromSearchBuilder.buildQueryFromSearch(parkingSearch));
    }

    @Override
    public Iterator<Parking> scrollParkings(Set<Long> stopPlaceIds) {
        return scrollParkings(getParkingsByStopPlaceIdsSQL(stopPlaceIds));
    }

    @Override
    public int countResult(ParkingSearch parkingSearch) {
        return countResult(parkingQueryFromSearchBuilder.buildQueryFromSearch(parkingSearch));
    }

    @Override
    public int countResult(Set<Long> stopPlaceIds) {
        if(stopPlaceIds == null || stopPlaceIds.isEmpty()) {
            return 0;
        }
        return countResult(getParkingsByStopPlaceIdsSQL(stopPlaceIds));
    }

    private int countResult(Pair<String, Map<String, Object>> sqlWithParams) {
        Session session = entityManager.unwrap(Session.class);
        NativeQuery query = session.createNativeQuery("SELECT COUNT(*) from (" + sqlWithParams.getFirst() + ") as numberOfParkings");
        searchHelper.addParams(query, sqlWithParams.getSecond());
        return ((Long) query.uniqueResult()).intValue();
    }

    private Iterator<Parking> scrollParkings(Pair<String, Map<String, Object>> sqlWithParams) {
        final int fetchSize = 100;

        Session session = entityManager.unwrap(Session.class);
        NativeQuery query = session.createNativeQuery(sqlWithParams.getFirst());
        searchHelper.addParams(query, sqlWithParams.getSecond());

        query.addEntity(Parking.class);
        query.setReadOnly(true);
        query.setFetchSize(fetchSize);
        query.setCacheable(false);
        ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

        ScrollableResultIterator<Parking> parkingEntityIterator = new ScrollableResultIterator<>(results, fetchSize, session);

        return parkingEntityIterator;
    }

    private Pair<String, Map<String, Object>> getParkingsByStopPlaceIdsSQL(Set<Long> stopPlaceIds) {

        StringBuilder sql = new StringBuilder("SELECT p.* " +
                "FROM (SELECT p2.id, " +
                "           p2.netex_id, " +
                "           p2.version " +
                "      FROM parking p2 " +
                "      INNER JOIN stop_place sp " +
                "           ON sp.netex_id = p2.parent_site_ref " +
                "           AND ( Cast(sp.version AS TEXT) = " +
                "                   p2.parent_site_ref_version " +
                "                 OR p2.parent_site_ref_version IS NULL ) " +
                "      WHERE sp.id in (");

        sql.append(StringUtils.join(stopPlaceIds, ','));
        sql.append(')');
        sql.append("   GROUP  BY p2.id) p2 ")
                .append("JOIN parking p ")
                .append("ON p2.id = p.id ")
                .append("WHERE ")
                .append(SQL_MAX_VERSION_OF_PARKING)
                .append("ORDER BY p.netex_id, p.version");

        return Pair.of(sql.toString(), new HashMap<String, Object>(0));
    }

    @Override
    public String findNearbyParking(Envelope envelope, String name, ParkingTypeEnumeration parkingTypeEnumeration) {
        Geometry geometryFilter = geometryFactory.toGeometry(envelope);

        TypedQuery<String> query = entityManager
                .createQuery("SELECT p.netexId FROM Parking p " +
                        "WHERE within(p.centroid, :filter) = true " +
                        "AND p.version = (SELECT MAX(pv.version) FROM Parking pv WHERE pv.netexId = p.netexId) " +
                        "AND p.name.value = :name " +
                        (parkingTypeEnumeration != null ? "AND p.parkingType = :parkingType":""),
                        String.class);

        query.setParameter("filter", geometryFilter);
        query.setParameter("name", name);
        if (parkingTypeEnumeration != null) {
            query.setParameter("parkingType", parkingTypeEnumeration);
        }
        return getOneOrNull(query);
    }


    @Override
    public List<String> findByStopPlaceNetexId(String netexStopPlaceId) {

        String sql = "SELECT p.netex_id " +
                "FROM parking p " +
                "WHERE p.parent_site_ref = :netexStopPlaceId " +
                "AND p.version = (SELECT MAX(pv.version) FROM Parking pv WHERE pv.netex_id = p.netex_id) "
                ;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("netexStopPlaceId", netexStopPlaceId);

        try {
            @SuppressWarnings("unchecked")
            List<String> results = query.getResultList();
            return results;

        } catch (NoResultException noResultException) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Parking> findParkingEntitiesByStopPlaceNetexId(String netexStopPlaceId) {
        String sql = "SELECT p.* " +
                "FROM parking p " +
                "WHERE p.parent_site_ref = :netexStopPlaceId " +
                "AND p.version = (SELECT MAX(pv.version) FROM parking pv WHERE pv.netex_id = p.netex_id) " +
                "ORDER BY p.netex_id";

        Query query = entityManager.createNativeQuery(sql, Parking.class);
        query.setParameter("netexStopPlaceId", netexStopPlaceId);

        try {
            @SuppressWarnings("unchecked")
            List<Parking> results = query.getResultList();
            return results;

        } catch (NoResultException noResultException) {
            return new ArrayList<>();
        }
    }

    private <T> T getOneOrNull(TypedQuery<T> query) {
        try {
            List<T> resultList = query.getResultList();
            return resultList.isEmpty() ? null : resultList.getFirst();
        } catch (NoResultException e) {
            return null;
        }
    }
}
