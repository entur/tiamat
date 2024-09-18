package org.rutebanken.tiamat.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.NotImplementedException;
import org.rutebanken.tiamat.model.InfoSpot;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class InfoSpotRepositoryImpl implements InfoSpotRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public String findFirstByKeyValues(String key, Set<String> originalIds) {
        throw new NotImplementedException("findFirstByKeyValues not implemented for " + this.getClass().getSimpleName());
    }

    @Override
    public List<InfoSpot> findForAssociation(String netexId) {
        String sql = """
            select infoSpot.* from info_spot infoSpot
            inner join info_spot_location isl on isl.info_spot_id = infoSpot.id
            where
                isl.location_netex_id = :netexId
                and infoSpot.version = (
                    SELECT MAX(version)
                    FROM info_spot
                    WHERE netex_id = infoSpot.netex_id
                )
        """;

        return entityManager.createNativeQuery(sql, InfoSpot.class)
                .setParameter("netexId", netexId)
                .getResultList();
    }
}
