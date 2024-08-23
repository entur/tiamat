package org.rutebanken.tiamat.repository;

import jakarta.persistence.QueryHint;
import org.rutebanken.tiamat.model.Organisation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

public interface OrganisationRepository extends OrganisationRepositoryCustom, EntityInVersionRepository<Organisation> {
    @QueryHints(value = { @QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    @Query("select org from Organisation org WHERE org.version = (SELECT MAX(orgv.version) FROM Organisation orgv WHERE orgv.netexId = org.netexId)")
    List<Organisation> findAllMaxVersion();
}
