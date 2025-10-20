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

package org.rutebanken.tiamat.importer.handler;

import com.hazelcast.core.HazelcastInstance;
import org.rutebanken.netex.model.ParkingsInFrame_RelStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.importer.filter.ZoneTopographicPlaceFilter;
import org.rutebanken.tiamat.importer.initial.ParallelInitialParkingImporter;
import org.rutebanken.tiamat.importer.merging.TransactionalMergingParkingsImporter;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

@Component
public class ParkingsImportHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ParkingsImportHandler.class);

    /**
     * Hazelcast lock key for merging stop place import.
     */
    private static final String PARKING_IMPORT_LOCK_KEY = "STOP_PLACE_MERGING_IMPORT_LOCK_KEY";

    @Autowired
    private PublicationDeliveryHelper publicationDeliveryHelper;

    @Autowired
    private NetexMapper netexMapper;

    @Autowired
    private ZoneTopographicPlaceFilter zoneTopographicPlaceFilter;

    @Autowired
    private TransactionalMergingParkingsImporter transactionalMergingParkingsImporter;

    @Autowired
    private ParallelInitialParkingImporter parallelInitialParkingImporter;

//    @Autowired TODO
//    private HazelcastInstance hazelcastInstance;

    public void handleParkings(SiteFrame netexSiteFrame, ImportParams importParams, AtomicInteger parkingsCreatedOrUpdated, SiteFrame responseSiteframe) {

        if (publicationDeliveryHelper.hasParkings(netexSiteFrame)) {
//  TODO
//            List<Parking> tiamatParking = netexMapper.mapParkingsToTiamatModel(netexSiteFrame.getParkings().getParking());
//
//            int numberOfParkingsBeforeFiltering = tiamatParking.size();
//            logger.info("About to filter {} parkings based on topographic references: {}", tiamatParking.size(), importParams.targetTopographicPlaces);
//            tiamatParking = zoneTopographicPlaceFilter.filterByTopographicPlaceMatch(importParams.targetTopographicPlaces, tiamatParking);
//            logger.info("Got {} parkings (was {}) after filtering by: {}", tiamatParking.size(), numberOfParkingsBeforeFiltering, importParams.targetTopographicPlaces);
//
//            if (importParams.onlyMatchOutsideTopographicPlaces != null && !importParams.onlyMatchOutsideTopographicPlaces.isEmpty()) {
//                numberOfParkingsBeforeFiltering = tiamatParking.size();
//                logger.info("Filtering parkings outside given list of topographic places: {}", importParams.onlyMatchOutsideTopographicPlaces);
//                tiamatParking = zoneTopographicPlaceFilter.filterByTopographicPlaceMatch(importParams.onlyMatchOutsideTopographicPlaces, tiamatParking, true);
//                logger.info("Got {} parkings (was {}) after filtering", tiamatParking.size(), numberOfParkingsBeforeFiltering);
//            }
//
//
//            Collection<org.rutebanken.netex.model.Parking> importedParkings;
//
//            if (importParams.importType == null || importParams.importType.equals(ImportType.MERGE)) {
//                final Lock lock = hazelcastInstance.getCPSubsystem().getLock(PARKING_IMPORT_LOCK_KEY);
//                lock.lock();
//                try {
//                    importedParkings = transactionalMergingParkingsImporter.importParkings(tiamatParking, parkingsCreatedOrUpdated);
//                } finally {
//                    lock.unlock();
//                }
//            } else if (importParams.importType.equals(ImportType.INITIAL)) {
//                importedParkings = parallelInitialParkingImporter.importParkings(tiamatParking, parkingsCreatedOrUpdated);
//            } else {
//                logger.warn("Import type " + importParams.importType + " not implemented. Will not match parking.");
//                importedParkings = new ArrayList<>(0);
//            }
//
//            if (!importedParkings.isEmpty()) {
//                responseSiteframe.withParkings(
//                        new ParkingsInFrame_RelStructure()
//                                .withParking(importedParkings));
//            }
//
//            logger.info("Mapped {} parkings!!", tiamatParking.size());
//
        }
    }
}
