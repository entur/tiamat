package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.importer.finder.NearbyParkingFinder;
import org.rutebanken.tiamat.importer.finder.ParkingFromOriginalIdFinder;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.versioning.ParkingVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.ExecutionException;

@Component
@Qualifier("mergingParkingImporter")
@Transactional
public class MergingParkingImporter {

    private static final Logger logger = LoggerFactory.getLogger(MergingParkingImporter.class);

    private final KeyValueListAppender keyValueListAppender;

    private final NetexMapper netexMapper;

    private final NearbyParkingFinder nearbyParkingFinder;

    private final ParkingVersionedSaverService parkingVersionedSaverService;

    private final ParkingFromOriginalIdFinder parkingFromOriginalIdFinder;

    @Autowired
    public MergingParkingImporter(ParkingFromOriginalIdFinder parkingFromOriginalIdFinder,
                                  NearbyParkingFinder nearbyParkingFinder,
                                  KeyValueListAppender keyValueListAppender, NetexMapper netexMapper,
                                  ParkingVersionedSaverService parkingVersionedSaverService) {
        this.parkingFromOriginalIdFinder = parkingFromOriginalIdFinder;
        this.nearbyParkingFinder = nearbyParkingFinder;
        this.keyValueListAppender = keyValueListAppender;
        this.netexMapper = netexMapper;
        this.parkingVersionedSaverService = parkingVersionedSaverService;
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
    public org.rutebanken.netex.model.Parking importParking(Parking parking) throws InterruptedException, ExecutionException {

        logger.debug("Transaction active: {}. Isolation level: {}", TransactionSynchronizationManager.isActualTransactionActive(), TransactionSynchronizationManager.getCurrentTransactionIsolationLevel());

        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new RuntimeException("Transaction with required "
                    + "TransactionSynchronizationManager.isActualTransactionActive(): " + TransactionSynchronizationManager.isActualTransactionActive());
        }

        return netexMapper.mapToNetexModel(importParkingWithoutNetexMapping(parking));
    }

    public Parking importParkingWithoutNetexMapping(Parking newParking) throws InterruptedException, ExecutionException {
        final Parking foundParking = findNearbyOrExistingParking(newParking);

        final Parking parking;
        if (foundParking != null) {
            parking = handleAlreadyExistingParking(foundParking, newParking);
        } else {
            parking = handleCompletelyNewParking(newParking);
        }

        return parking;
    }


    public Parking handleCompletelyNewParking(Parking incomingParking) throws ExecutionException {

        if (incomingParking.getNetexId() != null) {
            // This should not be necesarry.
            // Because this is a completely new parking.
            // And original netex ID should have been moved to key values.
            incomingParking.setNetexId(null);
        }

        // Ignore incoming version. Always set version to 1 for new stop places.
        logger.debug("New stop place: {}. Setting version to \"1\"", incomingParking.getName());
        parkingVersionedSaverService.createCopy(incomingParking, Parking.class);

        incomingParking = parkingVersionedSaverService.saveNewVersion(incomingParking);
        return updateCache(incomingParking);
    }

    public Parking handleAlreadyExistingParking(Parking existingParking, Parking incomingParking) {
        logger.debug("Found existing stop place {} from incoming {}", existingParking, incomingParking);

        Parking copy = parkingVersionedSaverService.createCopy(existingParking, Parking.class);

        boolean keyValuesChanged = keyValueListAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, incomingParking, copy);
        boolean centroidChanged = (copy.getCentroid() != null && incomingParking.getCentroid() != null && !copy.getCentroid().equals(incomingParking.getCentroid()));

        boolean typeChanged = false;
        if ((copy.getParkingType() == null && incomingParking.getParkingType() != null) ||
                !copy.getParkingType().equals(incomingParking.getParkingType())) {
            copy.setParkingType(incomingParking.getParkingType());
            logger.info("Updated parking type to {} for parking {}", copy.getParkingType(), copy);
            typeChanged = true;
        }

        boolean vehicleType = false;
        if ((copy.getParkingVehicleTypes() == null && incomingParking.getParkingVehicleTypes() != null) ||
                (!copy.getParkingVehicleTypes().containsAll(incomingParking.getParkingVehicleTypes()) ||
                        !incomingParking.getParkingVehicleTypes().containsAll(copy.getParkingVehicleTypes())) ) {
            copy.getParkingVehicleTypes().clear();
            copy.getParkingVehicleTypes().addAll(incomingParking.getParkingVehicleTypes());
            logger.info("Updated parkingVehicleTypes to {} for parking {}", copy.getParkingVehicleTypes(), copy);
            vehicleType = true;
        }


        if (keyValuesChanged || typeChanged || centroidChanged || vehicleType) {
            logger.info("Updated existing parking {}. ", copy);
            copy = parkingVersionedSaverService.saveNewVersion(copy);
            return updateCache(copy);
        }

        logger.debug("No changes. Returning existing stop {}", existingParking);
        return existingParking;

    }

    private Parking updateCache(Parking parking) {
        // Keep the attached parking reference in case it is merged.

        parkingFromOriginalIdFinder.update(parking);
        nearbyParkingFinder.update(parking);
        logger.info("Saved stop place {}", parking);
        return parking;
    }


    private Parking findNearbyOrExistingParking(Parking newParking) {
        final Parking existingParking = parkingFromOriginalIdFinder.find(newParking);
        if (existingParking != null) {
            return existingParking;
        }

        if (newParking.getName() != null) {
            final Parking nearbyParking = nearbyParkingFinder.find(newParking);
            if (nearbyParking != null) {
                logger.debug("Found nearby stop place with name: {}, id: {}", nearbyParking.getName(), nearbyParking.getNetexId());
                return nearbyParking;
            }
        }

//        // Find existing nearby stop place based on type
//        final List<Parking> nearbyStopsWithSameType = nearbyParkingsWithSameTypeFinder.find(newParking);
//        if (!nearbyStopsWithSameType.isEmpty()) {
//            Parking nearbyStopWithSameType = nearbyStopsWithSameType.get(0);
//            logger.debug("Found nearby stop place with same type: {}", nearbyStopWithSameType);
//            return nearbyStopWithSameType;
//        }
        return null;
    }

}
