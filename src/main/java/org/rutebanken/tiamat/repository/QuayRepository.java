package org.rutebanken.tiamat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.rutebanken.tiamat.model.Quay;

public interface QuayRepository extends JpaRepository<Quay, Long>, QuayRepositoryCustom {
}
