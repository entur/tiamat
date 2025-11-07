package org.rutebanken.tiamat.repository;

import jakarta.persistence.*;
import org.rutebanken.tiamat.model.vehicle.VehicleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Repository
public class VehicleTypeRepositoryImpl implements VehicleTypeRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(VehicleTypeRepositoryCustom.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find stop place's netex ID by key value
     *
     * @param key    key in key values for stop
     * @param values list of values to check for
     * @return stop place's netex ID
     */
    @Override
    public String findFirstByKeyValues(String key, Set<String> values) {

        Query query = entityManager.createNativeQuery("SELECT vt.netex_id " +
                "FROM vehicle_type vt " +
                "INNER JOIN vehicle_type_key_values vtkv " +
                "ON vtkv.vehicle_type_id = vt.id " +
                "INNER JOIN value_items v " +
                "ON vtkv.key_values_id = v.value_id " +
                "WHERE vtkv.key_values_key = :key " +
                "AND v.items IN ( :values ) " +
                "AND vt.version = (SELECT MAX(pv.version) FROM vehicle_type pv WHERE pv.netex_id = vt.netex_id)");

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

    public List<VehicleType> findAllCurrent() {
        Instant now = Instant.now();
        TypedQuery<VehicleType> query = entityManager.createQuery("SELECT vt FROM VehicleType vt WHERE vt.validBetween.fromDate <= :now and (vt.validBetween.toDate is null or vt.validBetween.toDate >= :now)", VehicleType.class);
        query.setParameter("now", now);
        return query.getResultList();
    }

}
