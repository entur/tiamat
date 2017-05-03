package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.importer.finder.NearbyStopPlaceFinder;
import org.rutebanken.tiamat.importer.finder.StopPlaceFromOriginalIdFinder;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.QuayRepository;
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
    private QuayRepository quayRepository;

    @Autowired
    private StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder;

    @Autowired
    private NetexMapper netexMapper;

    public List<org.rutebanken.netex.model.StopPlace> matchStopPlaces(List<org.rutebanken.tiamat.model.StopPlace> tiamatStops, AtomicInteger stopPlaceMatched) {

        List<org.rutebanken.netex.model.StopPlace> matchedStopPlaces = new ArrayList<>();

        tiamatStops.forEach(incomingStopPlace -> {

            Optional<StopPlace> existingStopPlace = Optional.empty();
            if(incomingStopPlace.getNetexId() != null && NetexIdHelper.isNsrId(incomingStopPlace.getNetexId())) {
                logger.debug("Looking for stop by netex id {}", incomingStopPlace.getNetexId());
                existingStopPlace = Optional.ofNullable(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(incomingStopPlace.getNetexId()));
            } else if(incomingStopPlace.getQuays() != null && !incomingStopPlace.getQuays().isEmpty()) {

                logger.debug("Looking for stop by quay netex ID");
                existingStopPlace = incomingStopPlace.getQuays().stream()
                        .filter(quay -> quay.getNetexId() != null && NetexIdHelper.isNsrId(quay.getNetexId()))
                        .map(quay -> quayRepository.findFirstByNetexIdOrderByVersionDesc(quay.getNetexId()))
                        .filter(quay -> quay != null)
                        .map(quay -> stopPlaceRepository.findByQuay(quay))
                        .findAny();

                if(!existingStopPlace.isPresent()) {
                    logger.debug("Looking for stop by quay original ID");
                    existingStopPlace = incomingStopPlace.getQuays().stream()
                            .flatMap(quay -> quay.getOriginalIds().stream())
                            .map(quayOriginalId -> stopPlaceRepository.findStopPlaceFromQuayOriginalId(quayOriginalId))
                            .filter(stopPlaceNetexIds -> stopPlaceNetexIds != null)
                            .filter(stopPlaceNetexIds -> !stopPlaceNetexIds.isEmpty())
                            .map(stopPlaceNetexIds -> stopPlaceNetexIds.get(0))
                            .map(stopPlaceNetexId -> stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId))
                            .filter(stopPlace -> stopPlace != null)
                            .findFirst();
                }
            }

            if(!existingStopPlace.isPresent()) {
                logger.debug("Looking for stop by stops original id: {}", incomingStopPlace.getOriginalIds());
                existingStopPlace = Optional.ofNullable(stopPlaceFromOriginalIdFinder.find(incomingStopPlace));
            }

            if(existingStopPlace.isPresent()) {
                StopPlace stopPlaceFound = existingStopPlace.get();
                logger.debug("Found matching stop place {}", stopPlaceFound);

                boolean alreadyAdded = matchedStopPlaces
                        .stream()
                        .anyMatch(alreadyAddedStop -> alreadyAddedStop.getId().equals(stopPlaceFound.getNetexId()));

                if(!alreadyAdded) {
                    matchedStopPlaces.add(netexMapper.mapToNetexModel(stopPlaceFound));
                }
                stopPlaceMatched.incrementAndGet();

            } else {
                logger.warn("Cannot find nearby stop place from NSR ID or original ID: {}", incomingStopPlace);
            }
        });

        return matchedStopPlaces;
    }
}
