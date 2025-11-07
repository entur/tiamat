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

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.ResourceFrame;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.exporter.PublicationDeliveryCreator;
import org.rutebanken.tiamat.importer.handler.*;
import org.rutebanken.tiamat.importer.log.ImportLogger;
import org.rutebanken.tiamat.importer.log.ImportLoggerTask;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.service.batch.BackgroundJobs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

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
    private final VehicleImportHandler vehicleImportHandler;
    private final VehicleTypeImportHandler vehicleTypeImportHandler;
    private final DeckPlanImportHandler deckPlanImportHandler;
    private final VehicleModelImportHandler vehicleModelImportHandler;
    private final TopographicPlaceImportHandler topographicPlaceImportHandler;
    private final BackgroundJobs backgroundJobs;
    private final AuthorizationService authorizationService;
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
                                       VehicleImportHandler vehicleImportHandler, VehicleTypeImportHandler vehicleTypeImportHandler, DeckPlanImportHandler deckPlanImportHandler, VehicleModelImportHandler vehicleModelImportHandler,
                                       BackgroundJobs backgroundJobs,
                                       AuthorizationService authorizationService,
                                       @Value("${authorization.enabled:true}") boolean authorizationEnabled) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.parkingsImportHandler = parkingsImportHandler;
        this.publicationDeliveryCreator = publicationDeliveryCreator;
        this.pathLinkImportHandler = pathLinkImportHandler;
        this.topographicPlaceImportHandler = topographicPlaceImportHandler;
        this.tariffZoneImportHandler = tariffZoneImportHandler;
        this.groupOfTariffZonesImportHandler = groupOfTariffZonesImportHandler;
        this.stopPlaceImportHandler = stopPlaceImportHandler;
        this.vehicleImportHandler = vehicleImportHandler;
        this.vehicleTypeImportHandler = vehicleTypeImportHandler;
        this.deckPlanImportHandler = deckPlanImportHandler;
        this.vehicleModelImportHandler = vehicleModelImportHandler;
        this.backgroundJobs = backgroundJobs;
        this.authorizationService = authorizationService;
        this.authorizationEnabled = authorizationEnabled;
    }


    public PublicationDeliveryStructure importPublicationDelivery(PublicationDeliveryStructure incomingPublicationDelivery) {
        return importPublicationDelivery(incomingPublicationDelivery, null);
    }

    @SuppressWarnings("unchecked")
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
        AtomicInteger vehicleCounter = new AtomicInteger(0);
        AtomicInteger vehicleTypeCounter = new AtomicInteger(0);
        AtomicInteger vehicleModelCounter = new AtomicInteger(0);
        AtomicInteger deckPlanCounter = new AtomicInteger(0);

        // Currently only supporting one site frame per publication delivery
        SiteFrame netexSiteFrame = publicationDeliveryHelper.findSiteFrame(incomingPublicationDelivery);
        ResourceFrame netexResourceFrame = publicationDeliveryHelper.findResourceFrame(incomingPublicationDelivery);

        updateMappingContext(incomingPublicationDelivery);

        SiteFrame responseSiteFrame = null;
        ResourceFrame responseResourceFrame = null;
        if(netexSiteFrame != null) {
            String requestId = netexSiteFrame.getId();

            Timer loggerTimer = new ImportLogger(new ImportLoggerTask(stopPlaceCounter, publicationDeliveryHelper.numberOfStops(netexSiteFrame), topographicPlaceCounter, netexSiteFrame.getId()));

            try {
                responseSiteFrame = new SiteFrame();

                MDC.put(IMPORT_CORRELATION_ID, requestId);
                logger.info("Publication delivery contains site frame created at {}", netexSiteFrame.getCreated());

                responseSiteFrame.withId(requestId + "-response").withVersion("1");

                topographicPlaceImportHandler.handleTopographicPlaces(netexSiteFrame, importParams, topographicPlaceCounter, responseSiteFrame);
                tariffZoneImportHandler.handleTariffZones(netexSiteFrame, importParams, tariffZoneCounter, responseSiteFrame);
                groupOfTariffZonesImportHandler.handleGroupOfTariffZones(netexSiteFrame, importParams, responseSiteFrame);
                stopPlaceImportHandler.handleStops(netexSiteFrame, importParams, stopPlaceCounter, responseSiteFrame);
                parkingsImportHandler.handleParkings(netexSiteFrame, importParams, parkingCounter, responseSiteFrame);
                pathLinkImportHandler.handlePathLinks(netexSiteFrame, importParams, pathLinkCounter, responseSiteFrame);
                if (responseSiteFrame.getTariffZones() != null || responseSiteFrame.getTopographicPlaces() != null) {
                    backgroundJobs.triggerStopPlaceUpdate();
                }
            } finally {
                MDC.remove(IMPORT_CORRELATION_ID);
                loggerTimer.cancel();
            }
        }
        if(netexResourceFrame != null) {
            String requestId = netexResourceFrame.getId();
            Timer loggerTimer = new ImportLogger(new ImportLoggerTask(vehicleCounter, publicationDeliveryHelper.numberOfVehicles(netexResourceFrame), vehicleTypeCounter, netexResourceFrame.getId()));
            try {
                responseResourceFrame = new ResourceFrame();

                MDC.put(IMPORT_CORRELATION_ID, requestId);
                logger.info("Publication delivery contains resource frame created at {}", netexResourceFrame.getCreated());

                responseResourceFrame.withId(requestId + "-response").withVersion("1");
                if(netexResourceFrame.getVehicleModels() != null) {
                    vehicleModelImportHandler.handleVehicleModels(netexResourceFrame, importParams, vehicleModelCounter, responseResourceFrame);
                }
                if(netexResourceFrame.getDeckPlans() != null) {
                    deckPlanImportHandler.handleDeckPlans(netexResourceFrame, importParams, deckPlanCounter, responseResourceFrame);
                }
                if(netexResourceFrame.getVehicleTypes() != null) {
                    vehicleTypeImportHandler.handleVehicleTypes(netexResourceFrame, importParams, vehicleTypeCounter, responseResourceFrame);
                }
                if(netexResourceFrame.getVehicles() != null) {
                    vehicleImportHandler.handleVehicles(netexResourceFrame, importParams, vehicleCounter, responseResourceFrame);
                }
            } finally {
                MDC.remove(IMPORT_CORRELATION_ID);
                loggerTimer.cancel();
            }
        }
        return publicationDeliveryCreator.createPublicationDelivery(responseSiteFrame, responseResourceFrame);
    }


    private void validate(ImportParams importParams) {
        if (importParams.targetTopographicPlaces != null && importParams.onlyMatchOutsideTopographicPlaces != null) {
            if (!importParams.targetTopographicPlaces.isEmpty() && !importParams.onlyMatchOutsideTopographicPlaces.isEmpty()) {
                throw new IllegalArgumentException("targetTopographicPlaces and onlyMatchOutsideTopographicPlaces cannot be specified at the same time!");
            }
        }
    }

}
