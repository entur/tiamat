package org.rutebanken.tiamat.repository;

import jakarta.persistence.QueryHint;
import java.util.List;
import org.rutebanken.tiamat.model.InfoSpot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

public interface InfoSpotRepository extends InfoSpotRepositoryCustom, EntityInVersionRepository<InfoSpot> {

    @QueryHints(value = { @QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    @Query("select spot from InfoSpot spot WHERE spot.version = (SELECT MAX(spotv.version) FROM InfoSpot spotv WHERE spotv.netexId = spot.netexId)")
    List<InfoSpot> findAllMaxVersion();
}
