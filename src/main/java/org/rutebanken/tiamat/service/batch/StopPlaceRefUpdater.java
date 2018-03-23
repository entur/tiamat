package org.rutebanken.tiamat.service.batch;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.TariffZonesLookupService;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StopPlaceRefUpdater implements Callable<Optional<StopPlace>> {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceRefUpdater.class);

    private final TariffZonesLookupService tariffZonesLookupService;
    private final TopographicPlaceLookupService topographicPlaceLookupService;
    private final StopPlace stopPlace;
    private final AtomicInteger updatedBecauseOfTariffZoneRefChange;
    private final AtomicInteger updatedBecauseOfTopographicPlaceRef;

    public StopPlaceRefUpdater(TariffZonesLookupService tariffZonesLookupService,
                               TopographicPlaceLookupService topographicPlaceLookupService,
                               StopPlace stopPlace,
                               AtomicInteger updatedBecauseOfTariffZoneRefChange,
                               AtomicInteger updatedBecauseOfTopographicPlaceRef) {
        this.tariffZonesLookupService = tariffZonesLookupService;
        this.topographicPlaceLookupService = topographicPlaceLookupService;
        this.stopPlace = stopPlace;
        this.updatedBecauseOfTariffZoneRefChange = updatedBecauseOfTariffZoneRefChange;
        this.updatedBecauseOfTopographicPlaceRef = updatedBecauseOfTopographicPlaceRef;
    }

    @Override
    public Optional<StopPlace> call() throws Exception {


        boolean tariffZoneRefsChanged = tariffZonesLookupService.populateTariffZone(stopPlace);

        if(tariffZoneRefsChanged) {
            updatedBecauseOfTariffZoneRefChange.incrementAndGet();
        }

        boolean topographicPlaceRefChanged = topographicPlaceLookupService.populateTopographicPlaceRelation(stopPlace);

        if(topographicPlaceRefChanged) {
            updatedBecauseOfTopographicPlaceRef.incrementAndGet();
        }

        if (tariffZoneRefsChanged || topographicPlaceRefChanged) {
            return Optional.of(stopPlace);
        }

        return Optional.empty();
    }
}
