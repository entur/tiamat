package org.rutebanken.tiamat.service.batch;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.service.FareZonesLookupService;
import org.rutebanken.tiamat.service.TariffZonesLookupService;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;


import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StopPlaceRefUpdater implements Callable<Optional<StopPlace>> {

    private final TariffZonesLookupService tariffZonesLookupService;
    private final FareZonesLookupService fareZonesLookupService;
    private final TopographicPlaceLookupService topographicPlaceLookupService;
    private final StopPlace stopPlace;
    private final AtomicInteger updatedBecauseOfTariffZoneRefChange;
    private final AtomicInteger updatedBecauseOfTopographicPlaceRef;

    public StopPlaceRefUpdater(TariffZonesLookupService tariffZonesLookupService,
                               FareZonesLookupService fareZonesLookupService,
                               TopographicPlaceLookupService topographicPlaceLookupService,
                               StopPlace stopPlace,
                               AtomicInteger updatedBecauseOfTariffZoneRefChange,
                               AtomicInteger updatedBecauseOfTopographicPlaceRef) {
        this.tariffZonesLookupService = tariffZonesLookupService;
        this.fareZonesLookupService = fareZonesLookupService;
        this.topographicPlaceLookupService = topographicPlaceLookupService;
        this.stopPlace = stopPlace;
        this.updatedBecauseOfTariffZoneRefChange = updatedBecauseOfTariffZoneRefChange;
        this.updatedBecauseOfTopographicPlaceRef = updatedBecauseOfTopographicPlaceRef;
    }

    @Override
    public Optional<StopPlace> call() throws Exception {


        boolean tariffZoneRefsChanged = tariffZonesLookupService.populateTariffZone(stopPlace);

        boolean fareZoneRefsChanged = fareZonesLookupService.populateFareZone(stopPlace);

        if (tariffZoneRefsChanged || fareZoneRefsChanged) {
            updatedBecauseOfTariffZoneRefChange.incrementAndGet();
        }

        boolean topographicPlaceRefChanged = topographicPlaceLookupService.populateTopographicPlaceRelation(stopPlace);

        if(topographicPlaceRefChanged) {
            updatedBecauseOfTopographicPlaceRef.incrementAndGet();
        }

        if (fareZoneRefsChanged || tariffZoneRefsChanged || topographicPlaceRefChanged) {
            return Optional.of(stopPlace);
        }

        return Optional.empty();
    }
}
