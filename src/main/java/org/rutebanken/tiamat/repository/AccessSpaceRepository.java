package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.AccessSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessSpaceRepository extends JpaRepository<AccessSpace, Long>, IdentifiedEntityRepository<AccessSpace> {

    AccessSpace findFirstByNetexIdOrderByVersionDesc(String netexId);
}