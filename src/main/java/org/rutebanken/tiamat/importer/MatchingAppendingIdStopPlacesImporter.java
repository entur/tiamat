package org.rutebanken.tiamat.importer;

import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.importer.finder.NearbyStopPlaceFinder;
import org.rutebanken.tiamat.importer.finder.StopPlaceByIdFinder;
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
 * Matches nearby existing stop places.
 * If match, the ID and tariffzone ref is appended to the existing stop.
 */
@Transactional
@Component
public class MatchingAppendingIdStopPlacesImporter {

    private static final Logger logger = LoggerFactory.getLogger(MatchingAppendingIdStopPlacesImporter.class);

    private static final boolean ALLOW_OTHER_TYPE_AS_ANY_MATCH = true;

    private static final boolean CREATE_NEW_QUAYS = false;

    @Autowired
    private NearbyStopPlaceFinder nearbyStopPlaceFinder;

    @Autowired
    private KeyValueListAppender keyValueListAppender;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayMerger quayMerger;

    @Autowired
    private NetexMapper netexMapper;

    @Autowired
    private StopPlaceByIdFinder stopPlaceByIdFinder;

    public List<StopPlace> importStopPlaces(List<org.rutebanken.tiamat.model.StopPlace> tiamatStops, AtomicInteger stopPlacesCreatedOrUpdated) {

        List<StopPlace> matchedStopPlaces = new ArrayList<>();

        tiamatStops.forEach(incomingStopPlace -> {


            Optional<org.rutebanken.tiamat.model.StopPlace> foundStopPlace = stopPlaceByIdFinder.findStopPlace(incomingStopPlace);

            if(!foundStopPlace.isPresent()) {
                foundStopPlace = Optional.ofNullable(nearbyStopPlaceFinder.find(incomingStopPlace, ALLOW_OTHER_TYPE_AS_ANY_MATCH));
            }

            if(!foundStopPlace.isPresent()) {
                logger.warn("Cannot find stop place: {}", incomingStopPlace);
            } else {

                org.rutebanken.tiamat.model.StopPlace existingstopPlace = foundStopPlace.get();

                logger.debug("Found matching stop place {}", existingstopPlace);
                keyValueListAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, incomingStopPlace, existingstopPlace);

                if(incomingStopPlace.getTariffZones() != null) {
                    if (existingstopPlace.getTariffZones() == null) {
                        existingstopPlace.setTariffZones(new HashSet<>());
                    }
                    existingstopPlace.getTariffZones().addAll(incomingStopPlace.getTariffZones());
                }

                quayMerger.appendImportIds(incomingStopPlace, existingstopPlace, CREATE_NEW_QUAYS);

                incomingStopPlace = stopPlaceRepository.save(existingstopPlace);
                String netexId = incomingStopPlace.getNetexId();

                boolean alreadyAdded = matchedStopPlaces
                        .stream()
                        .filter(alreadyAddedStop -> alreadyAddedStop.getId().equals(netexId))
                        .findAny().isPresent();

                if(!alreadyAdded) {
                    matchedStopPlaces.add(netexMapper.mapToNetexModel(existingstopPlace));
                }
                stopPlacesCreatedOrUpdated.incrementAndGet();
            }
        });

        return matchedStopPlaces;
    }
}
