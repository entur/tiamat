package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;

import java.util.List;
import java.util.Set;


public interface TariffZoneRepositoryCustom extends DataManagedObjectStructureRepository<TariffZone> {

    List<TariffZone> getTariffZonesFromStopPlaceIds(Set<Long> stopPlaceIds);
}
