package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.geo.CentroidComputer;
import org.rutebanken.tiamat.importer.finder.NearbyStopPlaceFinder;
import org.rutebanken.tiamat.importer.finder.NearbyStopsWithSameTypeFinder;
import org.rutebanken.tiamat.importer.finder.StopPlaceFromOriginalIdFinder;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Qualifier("mergingStopPlaceImporter")
@Transactional
public class MergingStopPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(MergingStopPlaceImporter.class);

    private final StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder;

    private final NearbyStopsWithSameTypeFinder nearbyStopsWithSameTypeFinder;

    private final NearbyStopPlaceFinder nearbyStopPlaceFinder;

    private final CentroidComputer centroidComputer;

    private final KeyValueListAppender keyValueListAppender;

    private final QuayMerger quayMerger;

    private final NetexMapper netexMapper;

    private final StopPlaceVersionedSaverService stopPlaceVersionedSaverService;


    @Autowired
    public MergingStopPlaceImporter(StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder,
                                    NearbyStopsWithSameTypeFinder nearbyStopsWithSameTypeFinder, NearbyStopPlaceFinder nearbyStopPlaceFinder,
                                    CentroidComputer centroidComputer,
                                    KeyValueListAppender keyValueListAppender, QuayMerger quayMerger, NetexMapper netexMapper,
                                    StopPlaceVersionedSaverService stopPlaceVersionedSaverService) {
        this.stopPlaceFromOriginalIdFinder = stopPlaceFromOriginalIdFinder;
        this.nearbyStopsWithSameTypeFinder = nearbyStopsWithSameTypeFinder;
        this.nearbyStopPlaceFinder = nearbyStopPlaceFinder;
        this.centroidComputer = centroidComputer;
        this.keyValueListAppender = keyValueListAppender;
        this.quayMerger = quayMerger;
        this.netexMapper = netexMapper;
        this.stopPlaceVersionedSaverService = stopPlaceVersionedSaverService;
    }

    /**
     * When importing site frames in multiple threads, and those site frames might contain different stop places that will be merged,
     * we run into the risk of having multiple threads trying to save the same stop place.
     * <p>
     * That's why we use a striped semaphore to not work on the same stop place concurrently. (SiteFrameImporter)
     * it is important to flush the session between each stop place, *before* the semaphore has been released.
     * <p>
     * Attempts to use saveAndFlush or hibernate flush mode always have not been successful.
     */
    public org.rutebanken.netex.model.StopPlace importStopPlace(StopPlace newStopPlace) throws InterruptedException, ExecutionException {

        logger.debug("Transaction active: {}. Isolation level: {}", TransactionSynchronizationManager.isActualTransactionActive(), TransactionSynchronizationManager.getCurrentTransactionIsolationLevel());

        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new RuntimeException("Transaction with required "
                    + "TransactionSynchronizationManager.isActualTransactionActive(): " + TransactionSynchronizationManager.isActualTransactionActive());
        }

        return netexMapper.mapToNetexModel(importStopPlaceWithoutNetexMapping(newStopPlace));
    }

    public StopPlace importStopPlaceWithoutNetexMapping(StopPlace incomingStopPlace) throws InterruptedException, ExecutionException {
        final StopPlace foundStopPlace = findNearbyOrExistingStopPlace(incomingStopPlace);

        final StopPlace stopPlace;
        if (foundStopPlace != null) {
            stopPlace = handleAlreadyExistingStopPlace(foundStopPlace, incomingStopPlace);
        } else {
            stopPlace = handleCompletelyNewStopPlace(incomingStopPlace);

        }
        return stopPlace;
    }


    public StopPlace handleCompletelyNewStopPlace(StopPlace incomingStopPlace) throws ExecutionException {

        if (incomingStopPlace.getNetexId() != null) {
            // This should not be necesarry.
            // Because this is a completely new stop.
            // And original netex ID should have been moved to key values.
            incomingStopPlace.setNetexId(null);
            if (incomingStopPlace.getQuays() != null) {
                incomingStopPlace.getQuays().forEach(q -> q.setNetexId(null));
            }
        }

        if (incomingStopPlace.getQuays() != null) {
            Set<Quay> quays = quayMerger.addNewQuaysOrAppendImportIds(incomingStopPlace.getQuays(), null, new AtomicInteger(), new AtomicInteger());
            incomingStopPlace.setQuays(quays);
            logger.trace("Importing quays for new stop place {}", incomingStopPlace);
        }

        centroidComputer.computeCentroidForStopPlace(incomingStopPlace);
        // Ignore incoming version. Always set version to 1 for new stop places.
        logger.debug("New stop place: {}. Setting version to \"1\"", incomingStopPlace.getName());
        stopPlaceVersionedSaverService.createCopy(incomingStopPlace, StopPlace.class);

        incomingStopPlace = stopPlaceVersionedSaverService.saveNewVersion(incomingStopPlace);
        return updateCache(incomingStopPlace);
    }

    public StopPlace handleAlreadyExistingStopPlace(StopPlace existingStopPlace, StopPlace incomingStopPlace) {
        logger.debug("Found existing stop place {} from incoming {}", existingStopPlace, incomingStopPlace);

        StopPlace copy = stopPlaceVersionedSaverService.createCopy(existingStopPlace, StopPlace.class);

        boolean quayChanged = quayMerger.addNewQuaysOrAppendImportIds(incomingStopPlace, copy);
        boolean keyValuesChanged = keyValueListAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, incomingStopPlace, copy);
        boolean centroidChanged = centroidComputer.computeCentroidForStopPlace(copy);

        boolean typeChanged = false;
        if (copy.getStopPlaceType() == null && incomingStopPlace.getStopPlaceType() != null) {
            copy.setStopPlaceType(incomingStopPlace.getStopPlaceType());
            logger.info("Updated stop place type to {} for stop place {}", copy.getStopPlaceType(), copy);
            typeChanged = true;
        }

        if (quayChanged || keyValuesChanged || centroidChanged || typeChanged) {
            logger.info("Updated existing stop place {}. ", copy);
            copy = stopPlaceVersionedSaverService.saveNewVersion(existingStopPlace, copy);
            return updateCache(copy);
        }

        logger.debug("No changes. Returning existing stop {}", existingStopPlace);
        return existingStopPlace;

    }

    private StopPlace updateCache(StopPlace stopPlace) {
        // Keep the attached stop place reference in case it is merged.

        stopPlaceFromOriginalIdFinder.update(stopPlace);
        nearbyStopPlaceFinder.update(stopPlace);
        logger.info("Saved stop place {}", stopPlace);
        return stopPlace;
    }

    private StopPlace findNearbyOrExistingStopPlace(StopPlace newStopPlace) {
        final StopPlace existingStopPlace = stopPlaceFromOriginalIdFinder.find(newStopPlace);
        if (existingStopPlace != null) {
            return existingStopPlace;
        }

        if (newStopPlace.getName() != null) {
            final StopPlace nearbyStopPlace = nearbyStopPlaceFinder.find(newStopPlace);
            if (nearbyStopPlace != null) {
                logger.debug("Found nearby stop place with name: {}, id: {}", nearbyStopPlace.getName(), nearbyStopPlace.getNetexId());
                return nearbyStopPlace;
            }
        }

        // Find existing nearby stop place based on type
        final List<StopPlace> nearbyStopsWithSameType = nearbyStopsWithSameTypeFinder.find(newStopPlace);
        if (!nearbyStopsWithSameType.isEmpty()) {
            StopPlace nearbyStopWithSameType = nearbyStopsWithSameType.get(0);
            logger.debug("Found nearby stop place with same type: {}", nearbyStopWithSameType);
            return nearbyStopWithSameType;
        }
        return null;
    }

}
