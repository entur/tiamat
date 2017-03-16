package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.PathLink;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PathLinkRepository extends JpaRepository<PathLink, Long>, PathLinkRepositoryCustom, IdentifiedEntityRepository<PathLink> {

    @Override
    PathLink findFirstByNetexIdOrderByVersionDesc(String netexId);
}
