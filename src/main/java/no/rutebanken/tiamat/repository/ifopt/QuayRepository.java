package no.rutebanken.tiamat.repository.ifopt;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.org.netex.netex.Quay;

public interface QuayRepository extends JpaRepository<Quay, String>, QuayRepositoryCustom {
}
