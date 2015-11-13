package no.rutebanken.tiamat.repository.ifopt;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.org.netex.netex.TariffZone;

public interface TariffZoneRepository extends JpaRepository<TariffZone, Long> {
	
}

