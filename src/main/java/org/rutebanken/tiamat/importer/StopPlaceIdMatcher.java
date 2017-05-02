package org.rutebanken.tiamat.importer;

import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.importer.finder.NearbyStopPlaceFinder;
import org.rutebanken.tiamat.importer.finder.StopPlaceFromOriginalIdFinder;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Matches stops from nsr ID or imported/original ID
 */
@Transactional
@Component
public class StopPlaceIdMatcher {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceIdMatcher.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder;

    @Autowired
    private NetexMapper netexMapper;

    public List<StopPlace> matchStopPlaces(List<org.rutebanken.tiamat.model.StopPlace> tiamatStops, AtomicInteger stopPlaceMatched) {

        List<StopPlace> matchedStopPlaces = new ArrayList<>();

        tiamatStops.forEach(stopPlace -> {

            org.rutebanken.tiamat.model.StopPlace existingStopPlace;
            if(stopPlace.getNetexId() != null && NetexIdHelper.isNsrId(stopPlace.getNetexId())) {
                logger.info("Looking for stop by netex id {}", stopPlace.getNetexId());
                existingStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());
            } else {
                logger.info("Looking for stop by original id: {}", stopPlace.getOriginalIds());
                existingStopPlace = stopPlaceFromOriginalIdFinder.find(stopPlace);
            }

            if(existingStopPlace == null) {
                logger.warn("Cannot find nearby stop place from NSR ID or original ID: {}", stopPlace);
            } else {
                logger.debug("Found matching stop place {}", existingStopPlace);

                String netexId = stopPlace.getNetexId();

                boolean alreadyAdded = matchedStopPlaces
                        .stream()
                        .anyMatch(alreadyAddedStop -> alreadyAddedStop.getId().equals(netexId));

                if(!alreadyAdded) {
                    matchedStopPlaces.add(netexMapper.mapToNetexModel(existingStopPlace));
                }
                stopPlaceMatched.incrementAndGet();
            }
        });

        return matchedStopPlaces;
    }
}
