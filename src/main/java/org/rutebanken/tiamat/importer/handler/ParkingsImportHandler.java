package org.rutebanken.tiamat.importer.handler;

import org.rutebanken.netex.model.ParkingsInFrame_RelStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.merging.TransactionalMergingParkingsImporter;
import org.rutebanken.tiamat.importer.filter.ZoneTopographicPlaceFilter;
import org.rutebanken.tiamat.importer.initial.ParallelInitialParkingImporter;
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

    private static final Object PARKING_IMPORT_LOCK = new Object();

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

            List<Parking> tiamatParking = netexMapper.mapParkingsToTiamatModel(netexSiteFrame.getParkings().getParking());

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


            Collection<org.rutebanken.netex.model.Parking> importedParkings;

            if (importParams.importType == null || importParams.importType.equals(ImportType.MERGE)) {
                synchronized (PARKING_IMPORT_LOCK) {
                    importedParkings = transactionalMergingParkingsImporter.importParkings(tiamatParking, parkingsCreatedOrUpdated);
                }
            } else if (importParams.importType.equals(ImportType.INITIAL)) {
                importedParkings = parallelInitialParkingImporter.importParkings(tiamatParking, parkingsCreatedOrUpdated);
            } else {
                logger.warn("Import type " + importParams.importType + " not implemented. Will not match parking.");
                importedParkings = new ArrayList<>(0);
            }

            if (!importedParkings.isEmpty()) {
                responseSiteframe.withParkings(
                        new ParkingsInFrame_RelStructure()
                                .withParking(importedParkings));
            }

            logger.info("Mapped {} parkings!!", tiamatParking.size());

        }
    }
}
