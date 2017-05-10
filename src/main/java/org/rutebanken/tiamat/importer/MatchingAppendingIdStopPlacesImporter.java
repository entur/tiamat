package org.rutebanken.tiamat.importer;

import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.importer.finder.NearbyStopPlaceFinder;
import org.rutebanken.tiamat.importer.finder.StopPlaceByIdFinder;
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
 * Matches nearby existing stop places.
 * If match, the ID and tariffzone ref is appended to the existing stop.
 */
@Component
@Transactional
public class MatchingAppendingIdStopPlacesImporter {

    private static final Logger logger = LoggerFactory.getLogger(MatchingAppendingIdStopPlacesImporter.class);


    @Autowired
    private TransactionalmatchingAppendingStopPlaceImporter transactionalmatchingAppendingStopPlaceImporter;

    public List<StopPlace> importStopPlaces(List<org.rutebanken.tiamat.model.StopPlace> tiamatStops, AtomicInteger stopPlacesCreatedOrUpdated) {

        List<StopPlace> matchedStopPlaces = new ArrayList<>();

        tiamatStops.forEach(incomingStopPlace -> {

            transactionalmatchingAppendingStopPlaceImporter.findAppendAndAdd(incomingStopPlace, matchedStopPlaces, stopPlacesCreatedOrUpdated);

        });

        return matchedStopPlaces;
    }


}
