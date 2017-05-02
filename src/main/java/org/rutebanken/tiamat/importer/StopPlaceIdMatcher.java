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
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder;

    @Autowired
    private NetexMapper netexMapper;

    public List<StopPlace> matchStopPlaces(List<org.rutebanken.tiamat.model.StopPlace> tiamatStops, AtomicInteger stopPlaceMatched) {

        List<StopPlace> matchedStopPlaces = new ArrayList<>();

        tiamatStops.forEach(incomingStopPlace -> {

            org.rutebanken.tiamat.model.StopPlace existingStopPlace;
            if(incomingStopPlace.getNetexId() != null && NetexIdHelper.isNsrId(incomingStopPlace.getNetexId())) {
                logger.debug("Looking for stop by netex id {}", incomingStopPlace.getNetexId());
                existingStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(incomingStopPlace.getNetexId());
            } else if(incomingStopPlace.getQuays() != null && !incomingStopPlace.getQuays().isEmpty()) {

                Optional<org.rutebanken.tiamat.model.StopPlace> stopPlaceOptional = incomingStopPlace.getQuays().stream()
                        .flatMap(quay -> quay.getOriginalIds().stream())
                        .map(quayOriginalId -> stopPlaceRepository.findStopPlaceFromQuayOriginalId(quayOriginalId))
                        .filter(stopPlaceNetexIds -> stopPlaceNetexIds != null)
                        .filter(stopPlaceNetexIds -> !stopPlaceNetexIds.isEmpty())
                        .map(stopPlaceNetexIds -> stopPlaceNetexIds.get(0))
                        .map(stopPlaceNetexId -> stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId))
                        .filter(stopPlace -> stopPlace != null)
                        .findFirst();

                if(stopPlaceOptional.isPresent()) {
                    existingStopPlace = stopPlaceOptional.get();
                    logger.debug("Found stop place from quay imported id: {}", existingStopPlace);
                } else {
                    existingStopPlace = null;
                }

            } else {
                logger.debug("Looking for stop by original id: {}", incomingStopPlace.getOriginalIds());
                existingStopPlace = stopPlaceFromOriginalIdFinder.find(incomingStopPlace);
            }

            if(existingStopPlace == null) {
                logger.warn("Cannot find nearby stop place from NSR ID or original ID: {}", incomingStopPlace);
            } else {
                logger.debug("Found matching stop place {}", existingStopPlace);

                boolean alreadyAdded = matchedStopPlaces
                        .stream()
                        .anyMatch(alreadyAddedStop -> alreadyAddedStop.getId().equals(existingStopPlace.getNetexId()));

                if(!alreadyAdded) {
                    matchedStopPlaces.add(netexMapper.mapToNetexModel(existingStopPlace));
                }
                stopPlaceMatched.incrementAndGet();
            }
        });

        return matchedStopPlaces;
    }
}
