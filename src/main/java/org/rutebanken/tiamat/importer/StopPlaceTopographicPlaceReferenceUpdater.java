package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The netex mapper should not map topographic place references to topographic places.
 * Because it should not know about repositories.
 * Thats why only the stop place's topopgraphic place id and version is populated.
 * Update the reference with a proper topographic reference fetched from the repository,
 */
@Component
public class StopPlaceTopographicPlaceReferenceUpdater {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceTopographicPlaceReferenceUpdater.class);

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;


    public void updateTopographicReference(StopPlace stopPlace) {
        if(stopPlace.getTopographicPlace() != null) {
            String netexId = stopPlace.getTopographicPlace().getNetexId();
            Long version = stopPlace.getTopographicPlace().getVersion();
            stopPlace.setTopographicPlace(topographicPlaceRepository.findFirstByNetexIdAndVersion(netexId, version));
            logger.trace("Resolved topographic place from {}:{} for stop place {}:{}", netexId, version, stopPlace.getNetexId(), stopPlace.getVersion());
        }
    }
}
