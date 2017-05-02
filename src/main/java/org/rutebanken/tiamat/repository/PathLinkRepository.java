package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.PathLink;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PathLinkRepository extends PathLinkRepositoryCustom, EntityInVersionRepository<PathLink> {

}
