package no.rutebanken.tiamat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import no.rutebanken.tiamat.model.AccessSpace;

public interface AccessSpaceRepository extends JpaRepository<AccessSpace, Long> {
}