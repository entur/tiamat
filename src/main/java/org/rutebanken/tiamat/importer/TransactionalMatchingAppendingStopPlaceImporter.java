package org.rutebanken.tiamat.importer;

import com.google.common.collect.Sets;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.importer.finder.NearbyStopPlaceFinder;
import org.rutebanken.tiamat.importer.finder.StopPlaceByIdFinder;
import org.rutebanken.tiamat.model.ZoneDistanceChecker;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TransactionalMatchingAppendingStopPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(TransactionalMatchingAppendingStopPlaceImporter.class);

    private static final boolean CREATE_NEW_QUAYS = false;

    @Autowired
    private KeyValueListAppender keyValueListAppender;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayMerger quayMerger;

    @Autowired
    private NetexMapper netexMapper;
    private static final boolean ALLOW_OTHER_TYPE_AS_ANY_MATCH = true;

    @Autowired
    private NearbyStopPlaceFinder nearbyStopPlaceFinder;


    @Autowired
    private StopPlaceByIdFinder stopPlaceByIdFinder;

    @Autowired
    private ZoneDistanceChecker zoneDistanceChecker;


    public void findAppendAndAdd(org.rutebanken.tiamat.model.StopPlace incomingStopPlace,
                                 List<StopPlace> matchedStopPlaces,
                                 AtomicInteger stopPlacesCreatedOrUpdated) {


        Set<org.rutebanken.tiamat.model.StopPlace> foundStopPlaces = stopPlaceByIdFinder.findStopPlace(incomingStopPlace);

        if(foundStopPlaces.isEmpty()) {
            org.rutebanken.tiamat.model.StopPlace nearbyStopPlace = nearbyStopPlaceFinder.find(incomingStopPlace, ALLOW_OTHER_TYPE_AS_ANY_MATCH);
            if(nearbyStopPlace !=  null) {
                foundStopPlaces = Sets.newHashSet(nearbyStopPlace);
            }
        }

        if(foundStopPlaces.isEmpty()) {
            logger.warn("Cannot find stop place from IDs or location: {}. StopPlace toString: {}",
                    incomingStopPlace.importedIdAndNameToString(),
                    incomingStopPlace);
        } else {

            if(foundStopPlaces.size() > 1) {
                logger.warn("Found {} matches for inomcing stop place {}", foundStopPlaces.size(), incomingStopPlace);
            }

            for(org.rutebanken.tiamat.model.StopPlace existingStopPlace : foundStopPlaces) {

                if (zoneDistanceChecker.exceedsLimit(incomingStopPlace, existingStopPlace)) {
                    logger.warn("Found stop place, but the distance between incoming and found stop place is too far in meters: {}. Incoming: {}. Found: {}", ZoneDistanceChecker.DEFAULT_MAX_DISTANCE, incomingStopPlace, existingStopPlace);
                    continue;
                }

                logger.debug("Found matching stop place {}", existingStopPlace);

                keyValueListAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, incomingStopPlace, existingStopPlace);

                if (incomingStopPlace.getTariffZones() != null) {
                    if (existingStopPlace.getTariffZones() == null) {
                        existingStopPlace.setTariffZones(new HashSet<>());
                    }
                    existingStopPlace.getTariffZones().addAll(incomingStopPlace.getTariffZones());
                }

                quayMerger.appendImportIds(incomingStopPlace, existingStopPlace, CREATE_NEW_QUAYS);

                incomingStopPlace = stopPlaceRepository.save(existingStopPlace);
                String netexId = incomingStopPlace.getNetexId();

                boolean alreadyAdded = matchedStopPlaces
                        .stream()
                        .filter(alreadyAddedStop -> alreadyAddedStop.getId().equals(netexId))
                        .findAny().isPresent();

                if (!alreadyAdded) {
                    matchedStopPlaces.add(netexMapper.mapToNetexModel(existingStopPlace));
                }

                stopPlacesCreatedOrUpdated.incrementAndGet();
                break;
            }
        }
    }
}
