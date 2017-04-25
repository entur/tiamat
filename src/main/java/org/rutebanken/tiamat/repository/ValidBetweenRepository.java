package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.ValidBetween;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidBetweenRepository extends JpaRepository<ValidBetween, Long>, IdentifiedEntityRepository<ValidBetween> {

}