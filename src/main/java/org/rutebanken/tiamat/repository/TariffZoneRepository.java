package org.rutebanken.tiamat.repository;

import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TariffZone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Iterator;
import java.util.List;

public interface TariffZoneRepository extends JpaRepository<TariffZone, Long>, IdentifiedEntityRepository<TariffZone> {

}

