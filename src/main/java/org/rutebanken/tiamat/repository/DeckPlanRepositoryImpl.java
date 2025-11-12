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
public class DeckPlanRepositoryImpl implements DeckPlanRepositoryCustom {

    private static final Logger logger = LoggerFactory.getLogger(DeckPlanRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find deck plan's netex ID by key value
     *
     * @param key    key in key values for stop
     * @param values list of values to check for
     * @return deck plan's netex ID
     */
    @Override
    public String findFirstByKeyValues(String key, Set<String> values) {

        Query query = entityManager.createNativeQuery("SELECT o.netex_id " +
                "FROM deck_plan o " +
                "INNER JOIN deck_plan_key_values okv " +
                "ON okv.deck_plan_id = o.id " +
                "INNER JOIN value_items v " +
                "ON okv.key_values_id = v.value_id " +
                "WHERE okv.key_values_key = :key " +
                "AND v.items IN ( :values ) " +
                "AND o.version = (SELECT MAX(oc.version) FROM deck_plan oc WHERE oc.netex_id = o.netex_id)");

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
