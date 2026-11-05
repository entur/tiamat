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

import org.entur.netex2.model.ObjectFactory;
import org.entur.netex2.model.ParkingsInFrame_RelStructure;
import org.entur.netex2.model.SiteFrame;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.importer.filter.ZoneTopographicPlaceFilter;
import org.rutebanken.tiamat.importer.initial.ParallelInitialParkingImporter;
import org.rutebanken.tiamat.importer.merging.TransactionalMergingParkingsImporter;
import org.rutebanken.tiamat.lock.AdvisoryLock;
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

@Component
public class ParkingsImportHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ParkingsImportHandler.class);

    /**
     * Lock name for merging parking import. One import runs at a time across all Tiamat instances.
     */
    private static final String PARKING_IMPORT_LOCK_KEY = "STOP_PLACE_MERGING_IMPORT_LOCK_KEY";

    @Autowired
    private AdvisoryLock advisoryLock;

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

    public void handleParkings(SiteFrame netexSiteFrame, ImportParams importParams, AtomicInteger parkingsCreatedOrUpdated, SiteFrame responseSiteframe) {

        if (publicationDeliveryHelper.hasParkings(netexSiteFrame)) {

            List<org.entur.netex2.model.Parking> netexParkings = netexSiteFrame.getParkings().getParking()
                    .stream()
                    .map(jakarta.xml.bind.JAXBElement::getValue)
                    .filter(org.entur.netex2.model.Parking.class::isInstance)
                    .map(org.entur.netex2.model.Parking.class::cast)
                    .toList();
            List<Parking> tiamatParking = netexMapper.mapParkingsToTiamatModel(netexParkings);

            int numberOfParkingsBeforeFiltering = tiamatParking.size();
            logger.info("About to filter {} parkings based on topographic references: {}", tiamatParking.size(), importParams.targetTopographicPlaces);
            tiamatParking = zoneTopographicPlaceFilter.filterByTopographicPlaceMatch(importParams.targetTopographicPlaces, tiamatParking);
            logger.info("Got {} parkings (was {}) after filtering by: {}", tiamatParking.size(), numberOfParkingsBeforeFiltering, importParams.targetTopographicPlaces);

            if (importParams.onlyMatchOutsideTopographicPlaces != null && !importParams.onlyMatchOutsideTopographicPlaces.isEmpty()) {
                numberOfParkingsBeforeFiltering = tiamatParking.size();
                logger.info("Filtering parkings outside given list of topographic places: {}", importParams.onlyMatchOutsideTopographicPlaces);
                tiamatParking = zoneTopographicPlaceFilter.filterByTopographicPlaceMatch(importParams.onlyMatchOutsideTopographicPlaces, tiamatParking, true);
                logger.info("Got {} parkings (was {}) after filtering", tiamatParking.size(), numberOfParkingsBeforeFiltering);
            }


            Collection<org.entur.netex2.model.Parking> importedParkings;

            if (importParams.importType == null || importParams.importType.equals(ImportType.MERGE)) {
                final List<Parking> parkingsToImport = tiamatParking;
                importedParkings = advisoryLock.executeInSessionLock(PARKING_IMPORT_LOCK_KEY,
                        () -> transactionalMergingParkingsImporter.importParkings(parkingsToImport, parkingsCreatedOrUpdated));
            } else if (importParams.importType.equals(ImportType.INITIAL)) {
                importedParkings = parallelInitialParkingImporter.importParkings(tiamatParking, parkingsCreatedOrUpdated);
            } else {
                logger.warn("Import type " + importParams.importType + " not implemented. Will not match parking.");
                importedParkings = new ArrayList<>(0);
            }

            if (!importedParkings.isEmpty()) {
                ObjectFactory objectFactory = new ObjectFactory();
                List<jakarta.xml.bind.JAXBElement<org.entur.netex2.model.Parking>> wrappedParkings = importedParkings.stream()
                        .map(objectFactory::createParking)
                        .toList();
                ParkingsInFrame_RelStructure parkingsInFrameRelStructure = new ParkingsInFrame_RelStructure();
                parkingsInFrameRelStructure.getParking().addAll(wrappedParkings);
                responseSiteframe.withParkings(parkingsInFrameRelStructure);
            }

            logger.info("Mapped {} parkings!!", tiamatParking.size());

        }
    }
}
