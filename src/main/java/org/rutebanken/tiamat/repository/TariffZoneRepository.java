package org.rutebanken.tiamat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.rutebanken.tiamat.model.TariffZone;

public interface TariffZoneRepository extends JpaRepository<TariffZone, Long> {
	
}

