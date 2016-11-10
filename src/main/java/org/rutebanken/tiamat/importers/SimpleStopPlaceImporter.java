package org.rutebanken.tiamat.importers;


import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Import stop place without taking existing data into account.
 * Suitable for clean databases. Topographical places may already exist,
 * or will be created by the TopoGraphicPlaceCreator.
 */
@Component
@Qualifier("cleanStopPlaceImporter")
public class SimpleStopPlaceImporter implements StopPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(SimpleStopPlaceImporter.class);

    private TopographicPlaceCreator topographicPlaceCreator;

    private QuayRepository quayRepository;

    private StopPlaceRepository stopPlaceRepository;


    @Autowired
    public SimpleStopPlaceImporter(TopographicPlaceCreator topographicPlaceCreator, QuayRepository quayRepository, StopPlaceRepository stopPlaceRepository) {
        this.topographicPlaceCreator = topographicPlaceCreator;
        this.quayRepository = quayRepository;
        this.stopPlaceRepository = stopPlaceRepository;
    }

    @Override
    public StopPlace importStopPlace(StopPlace stopPlace, SiteFrame siteFrame,
                                     AtomicInteger topographicPlacesCreatedCounter) throws InterruptedException, ExecutionException {
        if (!stopPlace.hasCoordinates()) {
            logger.info("Ignoring stop place {} - {} because it lacks geometry", stopPlace.getName(), stopPlace.getId());
            return null;
        }

        topographicPlaceCreator.setTopographicReference(stopPlace,
                siteFrame.getTopographicPlaces().getTopographicPlace(),
                topographicPlacesCreatedCounter);

        logger.trace("Resetting IDs for stop place");
        resetIds(stopPlace);

        if (stopPlace.getQuays() != null) {
            logger.debug("Stop place has {} quays", stopPlace.getQuays().size());
            stopPlace.getQuays().forEach(quay -> {

                logger.trace("Resetting IDs for quay");
                resetIds(quay);
                logger.debug("Saving quay ");
                quayRepository.save(quay);
            });
        }

        stopPlaceRepository.save(stopPlace);
        logger.debug("Saving stop place {} {}", stopPlace.getName(), stopPlace.getId());
        return stopPlace;
    }

    /**
     * Incoming data might contain IDs which may not correspond with our IDs. Reset.
     * @param zone
     */
    public void resetIds(Zone_VersionStructure zone) {
        zone.setId(null);
        if(zone.getCentroid() != null) {
            zone.getCentroid().setId(null);
            if(zone.getCentroid().getLocation() != null) {
                zone.getCentroid().getLocation().setId(0);
            }
        }
    }
}
