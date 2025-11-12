package org.rutebanken.tiamat.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class VehicleModelRepositoryImpl implements VehicleModelRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(VehicleModelRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find vehicle model's netex ID by key value
     *
     * @param key    key in key values for stop
     * @param values list of values to check for
     * @return vehicle model's netex ID
     */
    @Override
    public String findFirstByKeyValues(String key, Set<String> values) {

        Query query = entityManager.createNativeQuery("SELECT o.netex_id " +
                "FROM vehicle_model o " +
                "INNER JOIN vehicle_model_key_values okv " +
                "ON okv.vehicle_model_id = o.id " +
                "INNER JOIN value_items v " +
                "ON okv.key_values_id = v.value_id " +
                "WHERE okv.key_values_key = :key " +
                "AND v.items IN ( :values ) " +
                "AND o.version = (SELECT MAX(oc.version) FROM vehicle_model oc WHERE oc.netex_id = o.netex_id)");

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
}
