package no.rutebanken.tiamat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import no.rutebanken.tiamat.model.TariffZone;

public interface TariffZoneRepository extends JpaRepository<TariffZone, Long> {
	
}

