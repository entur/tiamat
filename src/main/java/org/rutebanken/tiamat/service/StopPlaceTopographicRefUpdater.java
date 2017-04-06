package org.rutebanken.tiamat.service;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class StopPlaceTopographicRefUpdater {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceTopographicRefUpdater.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private CountyAndMunicipalityLookupService countyAndMunicipalityLookupService;

    public void update(AtomicInteger topographicPlacesCreated, Set<String> updatedStopPlaceIds) {
        try {

            Iterator<StopPlace> iterator = stopPlaceRepository.scrollStopPlaces();

            while (iterator.hasNext()) {
                StopPlace stopPlace = iterator.next();

                if (stopPlace.getTopographicPlace() == null) {
                    logger.info("Stop Place does not have reference to topographic place: {}", stopPlace);
                    try {
                        countyAndMunicipalityLookupService.populateCountyAndMunicipality(stopPlace, topographicPlacesCreated);
                        stopPlaceRepository.save(stopPlace);
                        updatedStopPlaceIds.add(stopPlace.getNetexId());
                    } catch (IOException e) {
                        logger.info("Issue looking up county and municipality for stop {}", stopPlace, e);
                    }
                }
            }
        } catch (InterruptedException e) {
            logger.info("Interrupted getting stop place from queue.", e);
            Thread.currentThread().interrupt();
        }
    }
}
