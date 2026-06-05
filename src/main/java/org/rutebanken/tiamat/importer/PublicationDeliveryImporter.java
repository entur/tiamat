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

package org.rutebanken.tiamat.importer;

import org.rutebanken.netex.model.FareFrame;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.config.FareZoneConfig;
import org.rutebanken.tiamat.exporter.PublicationDeliveryCreator;
import org.rutebanken.tiamat.importer.handler.GroupOfTariffZonesImportHandler;
import org.rutebanken.tiamat.importer.handler.ParkingsImportHandler;
import org.rutebanken.tiamat.importer.handler.PathLinkImportHandler;
import org.rutebanken.tiamat.importer.handler.StopPlaceImportHandler;
import org.rutebanken.tiamat.importer.handler.TariffZoneImportHandler;
import org.rutebanken.tiamat.importer.handler.TopographicPlaceImportHandler;
import org.rutebanken.tiamat.importer.log.ImportLogger;
import org.rutebanken.tiamat.importer.log.ImportLoggerTask;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.service.batch.BackgroundJobs;
import org.rutebanken.tiamat.versioning.save.FareZoneSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

import static org.rutebanken.tiamat.netex.mapping.NetexMappingContextThreadLocal.updateMappingContext;

@Service
public class PublicationDeliveryImporter {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryImporter.class);

    public static final String IMPORT_CORRELATION_ID = "importCorrelationId";


    private final PublicationDeliveryHelper publicationDeliveryHelper;
    private final PublicationDeliveryCreator publicationDeliveryCreator;
    private final PathLinkImportHandler pathLinkImportHandler;
    private final TariffZoneImportHandler tariffZoneImportHandler;
    private final GroupOfTariffZonesImportHandler groupOfTariffZonesImportHandler;
    private final StopPlaceImportHandler stopPlaceImportHandler;
    private final ParkingsImportHandler parkingsImportHandler;
    private final TopographicPlaceImportHandler topographicPlaceImportHandler;
    private final BackgroundJobs backgroundJobs;
    private final AuthorizationService authorizationService;
    private final FareZoneConfig fareZoneConfig;
    private final FareZoneSaverService fareZoneSaverService;
    private final TransactionTemplate transactionTemplate;
    private final boolean authorizationEnabled;

    @Autowired
    public PublicationDeliveryImporter(PublicationDeliveryHelper publicationDeliveryHelper, NetexMapper netexMapper,
                                       PublicationDeliveryCreator publicationDeliveryCreator,
                                       PathLinkImportHandler pathLinkImportHandler,
                                       TopographicPlaceImportHandler topographicPlaceImportHandler,
                                       TariffZoneImportHandler tariffZoneImportHandler,
                                       GroupOfTariffZonesImportHandler groupOfTariffZonesImportHandler,
                                       StopPlaceImportHandler stopPlaceImportHandler,
                                       ParkingsImportHandler parkingsImportHandler,
                                       BackgroundJobs backgroundJobs,
                                       AuthorizationService authorizationService,
                                       FareZoneConfig fareZoneConfig,
                                       FareZoneSaverService fareZoneSaverService,
                                       PlatformTransactionManager transactionManager,
                                       @Value("${authorization.enabled:true}") boolean authorizationEnabled) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.parkingsImportHandler = parkingsImportHandler;
        this.publicationDeliveryCreator = publicationDeliveryCreator;
        this.pathLinkImportHandler = pathLinkImportHandler;
        this.topographicPlaceImportHandler = topographicPlaceImportHandler;
        this.tariffZoneImportHandler = tariffZoneImportHandler;
        this.groupOfTariffZonesImportHandler = groupOfTariffZonesImportHandler;
        this.stopPlaceImportHandler = stopPlaceImportHandler;
        this.backgroundJobs = backgroundJobs;
        this.authorizationService = authorizationService;
        this.fareZoneConfig = fareZoneConfig;
        this.fareZoneSaverService = fareZoneSaverService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.authorizationEnabled = authorizationEnabled;
    }


    public PublicationDeliveryStructure importPublicationDelivery(PublicationDeliveryStructure incomingPublicationDelivery) {
        return importPublicationDelivery(incomingPublicationDelivery, null);
    }

    public PublicationDeliveryStructure importPublicationDelivery(PublicationDeliveryStructure incomingPublicationDelivery, ImportParams importParams) {
        if(authorizationEnabled && !authorizationService.canEditAllEntities()){
                throw new AccessDeniedException("Insufficient privileges for operation");
            }


        if (incomingPublicationDelivery.getDataObjects() == null) {
            String responseMessage = "Received publication delivery but it does not contain any data objects.";
            logger.warn(responseMessage);
            throw new RuntimeException(responseMessage);
        }

        if (importParams == null) {
            importParams = new ImportParams();
        } else {
            validate(importParams);
        }

        logger.info("Got publication delivery with {} site frames and description {}",
                incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame().size(),
                incomingPublicationDelivery.getDescription());

        AtomicInteger stopPlaceCounter = new AtomicInteger(0);
        AtomicInteger parkingCounter = new AtomicInteger(0);
        AtomicInteger topographicPlaceCounter = new AtomicInteger(0);
        AtomicInteger tariffZoneCounter = new AtomicInteger(0);
        AtomicInteger pathLinkCounter = new AtomicInteger(0);

        // Currently only supporting one site frame per publication delivery
        SiteFrame netexSiteFrame = publicationDeliveryHelper.findSiteFrame(incomingPublicationDelivery);

        // A FareFrame may accompany the SiteFrame (Nordic profile: FareZones live in the FareFrame,
        // while the GroupOfTariffZones referencing them lives in the SiteFrame).
        FareFrame netexFareFrame = publicationDeliveryHelper.findFareFrame(incomingPublicationDelivery);

        String requestId = netexSiteFrame.getId();

        updateMappingContext(netexSiteFrame);

        Timer loggerTimer = new ImportLogger(new ImportLoggerTask(stopPlaceCounter, publicationDeliveryHelper.numberOfStops(netexSiteFrame), topographicPlaceCounter, netexSiteFrame.getId()));

        try {
            SiteFrame responseSiteFrame = new SiteFrame();

            MDC.put(IMPORT_CORRELATION_ID, requestId);
            logger.info("Publication delivery contains site frame created at {}", netexSiteFrame.getCreated());

            responseSiteFrame.withId(requestId + "-response").withVersion("1");

            topographicPlaceImportHandler.handleTopographicPlaces(netexSiteFrame, importParams, topographicPlaceCounter ,responseSiteFrame);
            tariffZoneImportHandler.handleTariffZones(netexSiteFrame, importParams, tariffZoneCounter, responseSiteFrame);

            // Import fare zones carried in an accompanying FareFrame, so that a GroupOfTariffZones
            // in the SiteFrame can reference them within the same delivery.
            final Set<String> fareFrameZoneIds;
            if (netexFareFrame != null) {
                FareFrame responseFareFrame = new FareFrame().withId(requestId + "-fareframe-response").withVersion("1");
                fareFrameZoneIds = tariffZoneImportHandler.handleFareZonesFromFareFrame(netexFareFrame, importParams, tariffZoneCounter, responseFareFrame);
                } else {
                fareFrameZoneIds = Collections.emptySet();
            }

            // Run the external versioning FareZone cleanup and the GroupOfTariffZones import in a single
            // transaction, so that a failed group member validation rolls back the deletes instead of
            // leaving FareZones permanently removed by a rejected import.
            final ImportParams finalImportParams = importParams;
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                // With external versioning the import is a full replace: prune FareZones not present in this delivery.
                if (fareZoneConfig.isExternalVersioning() && !fareFrameZoneIds.isEmpty()) {
                    int deletedCount = fareZoneSaverService.deleteAllExcept(fareFrameZoneIds);
                    logger.info("External versioning cleanup: deleted {} orphaned FareZones", deletedCount);
                }

                groupOfTariffZonesImportHandler.handleGroupOfTariffZones(netexSiteFrame, finalImportParams, responseSiteFrame, fareFrameZoneIds);
            });
            stopPlaceImportHandler.handleStops(netexSiteFrame, importParams, stopPlaceCounter, responseSiteFrame);
            parkingsImportHandler.handleParkings(netexSiteFrame, importParams, parkingCounter, responseSiteFrame);
            pathLinkImportHandler.handlePathLinks(netexSiteFrame, importParams, pathLinkCounter, responseSiteFrame);

            if(responseSiteFrame.getTariffZones() != null || responseSiteFrame.getTopographicPlaces() != null) {
                backgroundJobs.triggerStopPlaceUpdate();
            }
            return publicationDeliveryCreator.createPublicationDelivery(responseSiteFrame);
        } finally {
            MDC.remove(IMPORT_CORRELATION_ID);
            loggerTimer.cancel();
        }
    }


    private void validate(ImportParams importParams) {
        if (importParams.targetTopographicPlaces != null && importParams.onlyMatchOutsideTopographicPlaces != null) {
            if (!importParams.targetTopographicPlaces.isEmpty() && !importParams.onlyMatchOutsideTopographicPlaces.isEmpty()) {
                throw new IllegalArgumentException("targetTopographicPlaces and onlyMatchOutsideTopographicPlaces cannot be specified at the same time!");
            }
        }
    }

}
