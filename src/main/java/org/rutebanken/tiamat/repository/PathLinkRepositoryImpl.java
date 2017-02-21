package org.rutebanken.tiamat.repository;


import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigInteger;
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
                "WHERE  plkv.key_values_key = :key " +
                "AND v.items IN ( :values ) ");

        query.setParameter("key", key);
        query.setParameter("values", values);

        try {
            @SuppressWarnings("unchecked")
            List<BigInteger> results = query.getResultList();
            if(results.isEmpty()) {
                return null;
            } else {
                return results.get(0).longValue();
            }
        } catch (NoResultException noResultException) {
            return null;
        }
    }
}
