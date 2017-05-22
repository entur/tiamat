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
import java.util.Set;
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

                    Set<StopPlace> existingStopPlaces = stopPlaceByIdFinder.findStopPlace(incomingStopPlace);

                    if(existingStopPlaces.isEmpty()){
                        logger.warn("Cannot find stop place from IDs: {}. StopPlace toString: {}.",
                                incomingStopPlace.importedIdAndNameToString(),
                                incomingStopPlace);
                    }

                    for (StopPlace stopPlaceFound : existingStopPlaces) {
                        logger.debug("Found matching stop place {}", stopPlaceFound);

                        boolean alreadyAdded = matchedStopPlaces
                                .stream()
                                .anyMatch(alreadyAddedStop -> alreadyAddedStop.getId().equals(stopPlaceFound.getNetexId()));

                        if (!alreadyAdded) {
                            matchedStopPlaces.add(netexMapper.mapToNetexModel(stopPlaceFound));
                        }
                        stopPlaceMatched.incrementAndGet();

                    }


                }
        );

        return matchedStopPlaces;
    }





}
