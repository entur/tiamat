package org.rutebanken.tiamat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.rutebanken.tiamat.model.AccessSpace;

public interface AccessSpaceRepository extends JpaRepository<AccessSpace, Long> {
}