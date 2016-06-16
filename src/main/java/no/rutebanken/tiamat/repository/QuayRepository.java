package no.rutebanken.tiamat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import no.rutebanken.tiamat.model.Quay;

public interface QuayRepository extends JpaRepository<Quay, String>, QuayRepositoryCustom {
}
