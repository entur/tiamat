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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TransactionalmatchingAppendingStopPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(TransactionalmatchingAppendingStopPlaceImporter.class);

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


    public void findAppendAndAdd(org.rutebanken.tiamat.model.StopPlace incomingStopPlace,
                                 List<StopPlace> matchedStopPlaces,
                                 AtomicInteger stopPlacesCreatedOrUpdated) {


        Optional<org.rutebanken.tiamat.model.StopPlace> foundStopPlace = stopPlaceByIdFinder.findStopPlace(incomingStopPlace);

        if(!foundStopPlace.isPresent()) {
            foundStopPlace = Optional.ofNullable(nearbyStopPlaceFinder.find(incomingStopPlace, ALLOW_OTHER_TYPE_AS_ANY_MATCH));
        }

        if(!foundStopPlace.isPresent()) {
            logger.warn("Cannot find stop place: {}", incomingStopPlace);
        } else {

            org.rutebanken.tiamat.model.StopPlace existingStopPlace = foundStopPlace.get();


            logger.debug("Found matching stop place {}", existingStopPlace);


            keyValueListAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, incomingStopPlace, existingStopPlace);

            if(incomingStopPlace.getTariffZones() != null) {
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

            if(!alreadyAdded) {
                matchedStopPlaces.add(netexMapper.mapToNetexModel(existingStopPlace));
            }

            stopPlacesCreatedOrUpdated.incrementAndGet();
        }
    }
}
