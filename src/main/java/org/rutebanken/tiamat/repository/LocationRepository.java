package org.rutebanken.tiamat.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.rutebanken.tiamat.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long>{
}
