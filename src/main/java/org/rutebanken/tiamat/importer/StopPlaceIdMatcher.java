package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.importer.finder.StopPlaceByIdFinder;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Matches stops from nsr ID or imported/original ID
 */
@Transactional
@Component
public class StopPlaceIdMatcher {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceIdMatcher.class);


    @Autowired
    private StopPlaceByIdFinder stopPlaceByIdFinder;

    @Autowired
    private NetexMapper netexMapper;


    public List<org.rutebanken.netex.model.StopPlace> matchStopPlaces(List<org.rutebanken.tiamat.model.StopPlace> tiamatStops, AtomicInteger stopPlaceMatched) {

        List<org.rutebanken.netex.model.StopPlace> matchedStopPlaces = new ArrayList<>();

        tiamatStops.forEach(incomingStopPlace -> {

            Optional<StopPlace> existingStopPlace = stopPlaceByIdFinder.findStopPlace(incomingStopPlace);

            if (existingStopPlace.isPresent()) {
                StopPlace stopPlaceFound = existingStopPlace.get();
                logger.debug("Found matching stop place {}", stopPlaceFound);

                boolean alreadyAdded = matchedStopPlaces
                        .stream()
                        .anyMatch(alreadyAddedStop -> alreadyAddedStop.getId().equals(stopPlaceFound.getNetexId()));

                if (!alreadyAdded) {
                    matchedStopPlaces.add(netexMapper.mapToNetexModel(stopPlaceFound));
                }
                stopPlaceMatched.incrementAndGet();

            } else {
                logger.warn("Cannot find stop place from quay imported-id, stop place nsr id, stop place imported-id or quay nsr id {}", incomingStopPlace);
            }
        });

        return matchedStopPlaces;
    }





}
