package org.rutebanken.tiamat.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class InfoSpotPosterRepositoryImpl implements InfoSpotPosterRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public String findFirstByKeyValues(String key, Set<String> originalIds) {
        throw new NotImplementedException("findFirstByKeyValues not implemented for " + this.getClass().getSimpleName());
    }
}
