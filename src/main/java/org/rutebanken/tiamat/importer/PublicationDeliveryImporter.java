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

import org.rutebanken.helper.organisation.NotAuthenticatedException;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.exporter.PublicationDeliveryExporter;
import org.rutebanken.tiamat.importer.handler.*;
import org.rutebanken.tiamat.importer.log.ImportLogger;
import org.rutebanken.tiamat.importer.log.ImportLoggerTask;
import org.rutebanken.tiamat.netex.mapping.*;
import org.rutebanken.tiamat.service.batch.BackgroundJobs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;
import static org.rutebanken.tiamat.netex.mapping.NetexMappingContextThreadLocal.updateMappingContext;

@Service
public class PublicationDeliveryImporter {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryImporter.class);

    public static final String IMPORT_CORRELATION_ID = "importCorrelationId";


    private final PublicationDeliveryHelper publicationDeliveryHelper;
    private final PublicationDeliveryExporter publicationDeliveryExporter;
    private final PathLinkImportHandler pathLinkImportHandler;
    private final TariffZoneImportHandler tariffZoneImportHandler;
    private final StopPlaceImportHandler stopPlaceImportHandler;
    private final ParkingsImportHandler parkingsImportHandler;
    private final TopographicPlaceImportHandler topographicPlaceImportHandler;
    private final RoleAssignmentExtractor roleAssignmentExtractor;
    private final BackgroundJobs backgroundJobs;

    @Autowired
    public PublicationDeliveryImporter(PublicationDeliveryHelper publicationDeliveryHelper, NetexMapper netexMapper,
                                       PublicationDeliveryExporter publicationDeliveryExporter,
                                       PathLinkImportHandler pathLinkImportHandler,
                                       TopographicPlaceImportHandler topographicPlaceImportHandler,
                                       TariffZoneImportHandler tariffZoneImportHandler,
                                       StopPlaceImportHandler stopPlaceImportHandler,
                                       ParkingsImportHandler parkingsImportHandler,
                                       RoleAssignmentExtractor roleAssignmentExtractor,
                                       BackgroundJobs backgroundJobs) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.parkingsImportHandler = parkingsImportHandler;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.pathLinkImportHandler = pathLinkImportHandler;
        this.topographicPlaceImportHandler = topographicPlaceImportHandler;
        this.tariffZoneImportHandler = tariffZoneImportHandler;
        this.stopPlaceImportHandler = stopPlaceImportHandler;
        this.roleAssignmentExtractor = roleAssignmentExtractor;
        this.backgroundJobs = backgroundJobs;
    }


    public PublicationDeliveryStructure importPublicationDelivery(PublicationDeliveryStructure incomingPublicationDelivery) {
        return importPublicationDelivery(incomingPublicationDelivery, null);
    }

    @SuppressWarnings("unchecked")
    public PublicationDeliveryStructure importPublicationDelivery(PublicationDeliveryStructure incomingPublicationDelivery, ImportParams importParams) {

        if(roleAssignmentExtractor.getRoleAssignmentsForUser()
                .stream()
                .noneMatch(roleAssignment -> roleAssignment.r.equals(ROLE_EDIT_STOPS))) {
            throw new NotAuthenticatedException("Role: '" + ROLE_EDIT_STOPS + "' required for import!");
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

        String requestId = netexSiteFrame.getId();

        updateMappingContext(netexSiteFrame);

        Timer loggerTimer = new ImportLogger(new ImportLoggerTask(stopPlaceCounter, publicationDeliveryHelper.numberOfStops(netexSiteFrame), topographicPlaceCounter, netexSiteFrame.getId()));

        try {
            SiteFrame responseSiteframe = new SiteFrame();

            MDC.put(IMPORT_CORRELATION_ID, requestId);
            logger.info("Publication delivery contains site frame created at {}", netexSiteFrame.getCreated());

            responseSiteframe.withId(requestId + "-response").withVersion("1");

            topographicPlaceImportHandler.handleTopographicPlaces(netexSiteFrame, importParams, topographicPlaceCounter ,responseSiteframe);
            tariffZoneImportHandler.handleTariffZones(netexSiteFrame, importParams, tariffZoneCounter, responseSiteframe);
            stopPlaceImportHandler.handleStops(netexSiteFrame, importParams, stopPlaceCounter, responseSiteframe);
            parkingsImportHandler.handleParkings(netexSiteFrame, importParams, parkingCounter, responseSiteframe);
            pathLinkImportHandler.handlePathLinks(netexSiteFrame, importParams, pathLinkCounter, responseSiteframe);

            if(responseSiteframe.getTariffZones() != null && responseSiteframe.getTopographicPlaces() != null) {
                backgroundJobs.triggerStopPlaceUpdate();
            }
            return publicationDeliveryExporter.createPublicationDelivery(responseSiteframe);
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
