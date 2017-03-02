package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.PathJunction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PathJunctionRepository extends JpaRepository<PathJunction, Long>, IdentifiedEntityRepository<PathJunction> {

    @Override
    PathJunction findByNetexId(String netexId);
}
