package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.PurposeOfGrouping;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
