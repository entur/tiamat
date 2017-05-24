package org.rutebanken.tiamat.repository;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.ParkingTypeEnumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class ParkingRepositoryImpl implements ParkingRepositoryCustom {


    @Autowired
    private EntityManager entityManager;


    @Autowired
    private GeometryFactory geometryFactory;



    /**
     * Find stop place's netex ID by key value
     *
     * @param key    key in key values for stop
     * @param values list of values to check for
     * @return stop place's netex ID
     */
    @Override
    public String findByKeyValue(String key, Set<String> values) {

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
                return results.get(0);
            }
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    @Override
    public Iterator<Parking> scrollParkings() throws InterruptedException {
        return scrollParkings(null);
    }

    @Override
    public Iterator<Parking> scrollParkings(List<String> parkingNetexIds) throws InterruptedException {

        final int fetchSize = 100;

        Session session = entityManager.getEntityManagerFactory().createEntityManager().unwrap(Session.class);

        Criteria query = session.createCriteria(Parking.class);
        if (parkingNetexIds != null) {
            query.add(Restrictions.in("netexId", parkingNetexIds));
        }

        query.setReadOnly(true);
        query.setFetchSize(fetchSize);
        query.setCacheable(false);
        ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

        ScrollableResultIterator<Parking> parkingEntityIterator = new ScrollableResultIterator<>(results, fetchSize, session);

        return parkingEntityIterator;
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

    private <T> T getOneOrNull(TypedQuery<T> query) {
        try {
            List<T> resultList = query.getResultList();
            return resultList.isEmpty() ? null : resultList.get(0);
        } catch (NoResultException e) {
            return null;
        }
    }
}
