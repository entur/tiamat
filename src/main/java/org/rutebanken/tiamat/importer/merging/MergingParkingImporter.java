/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.importer.merging;

import org.rutebanken.tiamat.importer.KeyValueListAppender;
import org.rutebanken.tiamat.importer.finder.NearbyParkingFinder;
import org.rutebanken.tiamat.importer.finder.ParkingFromOriginalIdFinder;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
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

    private final ReferenceResolver referenceResolver;

    @Autowired
    public MergingParkingImporter(ParkingFromOriginalIdFinder parkingFromOriginalIdFinder,
                                  NearbyParkingFinder nearbyParkingFinder, ReferenceResolver referenceResolver,
                                  KeyValueListAppender keyValueListAppender, NetexMapper netexMapper,
                                  ParkingVersionedSaverService parkingVersionedSaverService) {
        this.parkingFromOriginalIdFinder = parkingFromOriginalIdFinder;
        this.nearbyParkingFinder = nearbyParkingFinder;
        this.referenceResolver = referenceResolver;
        this.keyValueListAppender = keyValueListAppender;
        this.netexMapper = netexMapper;
        this.parkingVersionedSaverService = parkingVersionedSaverService;
    }

    /**
     * When importing site frames in multiple threads, and those site frames might contain different parkings that will be merged,
     * we run into the risk of having multiple threads trying to save the same parking.
     * <p>
     * That's why we use a striped semaphore to not work on the same parking concurrently. (SiteFrameImporter)
     * it is important to flush the session between each parking, *before* the semaphore has been released.
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

        resolveAndFixParentSiteRef(parking);

        return parking;
    }

    private void resolveAndFixParentSiteRef(Parking parking) {
        if (parking != null && parking.getParentSiteRef() != null) {
            DataManagedObjectStructure referencedStopPlace = referenceResolver.resolve(parking.getParentSiteRef());
            parking.getParentSiteRef().setRef(referencedStopPlace.getNetexId());
        }
    }


    public Parking handleCompletelyNewParking(Parking incomingParking) throws ExecutionException {

        if (incomingParking.getNetexId() != null) {
            // This should not be necessary.
            // Because this is a completely new parking.
            // And original netex ID should have been moved to key values.
            incomingParking.setNetexId(null);
        }

        // Ignore incoming version. Always set version to 1 for new parkings.
        logger.debug("New parking: {}. Setting version to \"1\"", incomingParking.getName());
        parkingVersionedSaverService.createCopy(incomingParking, Parking.class);

        incomingParking = parkingVersionedSaverService.saveNewVersion(incomingParking);
        return updateCache(incomingParking);
    }

    public Parking handleAlreadyExistingParking(Parking existingParking, Parking incomingParking) {
        logger.debug("Found existing parking {} from incoming {}", existingParking, incomingParking);

        Parking copy = parkingVersionedSaverService.createCopy(existingParking, Parking.class);

        boolean keyValuesChanged = keyValueListAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, incomingParking, copy);
        boolean centroidChanged = (copy.getCentroid() != null && incomingParking.getCentroid() != null && !copy.getCentroid().equals(incomingParking.getCentroid()));

        boolean typeChanged = false;
        if ((copy.getParkingType() == null && incomingParking.getParkingType() != null) ||
            (copy.getParkingType() != null && incomingParking.getParkingType() != null
                    && !copy.getParkingType().equals(incomingParking.getParkingType()))) {

            copy.setParkingType(incomingParking.getParkingType());
            logger.info("Updated parking type to {} for parking {}", copy.getParkingType(), copy);
            typeChanged = true;
        }

        boolean vehicleType = false;
        if (!copy.getParkingVehicleTypes().containsAll(incomingParking.getParkingVehicleTypes()) ||
                        !incomingParking.getParkingVehicleTypes().containsAll(copy.getParkingVehicleTypes()) ) {
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

        logger.debug("No changes. Returning existing parking {}", existingParking);
        return existingParking;

    }

    private Parking updateCache(Parking parking) {
        // Keep the attached parking reference in case it is merged.

        parkingFromOriginalIdFinder.update(parking);
        nearbyParkingFinder.update(parking);
        logger.info("Saved parking {}", parking);
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
                logger.debug("Found nearby parking with name: {}, id: {}", nearbyParking.getName(), nearbyParking.getNetexId());
                return nearbyParking;
            }
        }
        return null;
    }

}
