package org.rutebanken.tiamat.repository;


import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PathLinkRepositoryImpl implements PathLinkRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    public Long findByKeyValue(String key, Set<String> values) {

        Query query = entityManager.createNativeQuery("SELECT path_link_id " +
                "FROM path_link_key_values plkv " +
                    "INNER JOIN value_items v " +
                        "ON plkv.key_values_id = v.value_id " +
                    "INNER JOIN path_link pl " +
                        "ON path_link_id = pl.id " +
                "WHERE plkv.key_values_key = :key " +
                    "AND v.items IN ( :values ) " +
                    "AND pl.version = (SELECT MAX(plv.version) FROM path_link plv WHERE plv.netex_id = pl.netex_id)");

        query.setParameter("key", key);
        query.setParameter("values", values);

        try {
            @SuppressWarnings("unchecked")
            List<BigInteger> results = query.getResultList();
            if (results.isEmpty()) {
                return null;
            } else {
                return results.get(0).longValue();
            }
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    @Override
    public List<String> findByStopPlaceNetexId(String netexStopPlaceId) {

        String sql = "SELECT pl.netex_id " +
                "FROM path_link pl " +
                "       INNER JOIN path_link_end ple " +
                "               ON pl.from_id = ple.id " +
                "                  OR pl.to_id = ple.id " +
                "       INNER JOIN quay q " +
                "               ON q.netex_id = ple.place_ref " +
                "WHERE q.id IN (SELECT spq.quays_id " +
                "                FROM stop_place_quays spq " +
                "                   INNER JOIN stop_place s ON s.id = spq.stop_place_id " +
                "                WHERE s.netex_id = :netexStopPlaceId)";

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
}
