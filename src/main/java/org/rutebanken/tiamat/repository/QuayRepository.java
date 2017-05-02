package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.Quay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuayRepository extends QuayRepositoryCustom, EntityInVersionRepository<Quay> {

}
