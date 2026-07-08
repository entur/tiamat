package org.rutebanken.tiamat.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.rutebanken.tiamat.model.PurposeOfGrouping;

import java.util.List;

public class PurposeOfGroupingRepositoryImpl implements PurposeOfGroupingRepositoryCustom{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PurposeOfGrouping> findAllPurposeOfGrouping() {
        String sql = "SELECT pog.* FROM purpose_of_grouping pog";
        Query query = entityManager.createNativeQuery(sql, PurposeOfGrouping.class);
        return query.getResultList();
    }
}
