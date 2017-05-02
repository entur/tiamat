package org.rutebanken.tiamat.importer;

import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.importer.finder.NearbyStopPlaceFinder;
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
 * Matches nearby existing stop places.
 * If match, the ID and tariffzone ref is appended to the existing stop.
 */
@Transactional
@Component
public class MatchingIdAppendingStopPlacesImporter {

    private static final Logger logger = LoggerFactory.getLogger(MatchingIdAppendingStopPlacesImporter.class);

    private static final boolean ALLOW_OTHER_TYPE_AS_ANY_MATCH = true;

    @Autowired
    private NearbyStopPlaceFinder nearbyStopPlaceFinder;

    @Autowired
    private KeyValueListAppender keyValueListAppender;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private NetexMapper netexMapper;

    public List<StopPlace> importStopPlaces(List<org.rutebanken.tiamat.model.StopPlace> tiamatStops, AtomicInteger stopPlacesCreatedOrUpdated) {

        List<StopPlace> matchedStopPlaces = new ArrayList<>();

        tiamatStops.forEach(stopPlace -> {

            org.rutebanken.tiamat.model.StopPlace existingstopPlace = nearbyStopPlaceFinder.find(stopPlace, ALLOW_OTHER_TYPE_AS_ANY_MATCH);
            if(existingstopPlace == null) {
                logger.warn("Cannot find nearby stop place: {}", stopPlace);
            } else {
                logger.debug("Found matching stop place {}", existingstopPlace);
                keyValueListAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, stopPlace, existingstopPlace);

                if(stopPlace.getTariffZones() != null) {
                    if (existingstopPlace.getTariffZones() == null) {
                        existingstopPlace.setTariffZones(new HashSet<>());
                    }
                    existingstopPlace.getTariffZones().addAll(stopPlace.getTariffZones());
                }
                stopPlace = stopPlaceRepository.save(existingstopPlace);
                String netexId = stopPlace.getNetexId();

                boolean alreadyAdded = matchedStopPlaces
                        .stream()
                        .filter(alreadyAddedStop -> alreadyAddedStop.getId().equals(netexId))
                        .findAny().isPresent();

                if(!alreadyAdded) {
                    matchedStopPlaces.add(netexMapper.mapToNetexModel(stopPlace));
                }
                stopPlacesCreatedOrUpdated.incrementAndGet();
            }
        });

        return matchedStopPlaces;
    }
}
