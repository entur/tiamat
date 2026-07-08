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
import org.rutebanken.tiamat.config.FareZoneConfig;
import org.rutebanken.tiamat.exporter.PublicationDeliveryCreator;
import org.rutebanken.tiamat.importer.handler.TariffZoneImportHandler;
import org.rutebanken.tiamat.netex.mapping.NetexMappingContextThreadLocal;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.service.batch.BackgroundJobs;
import org.rutebanken.tiamat.versioning.save.FareZoneSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Importer for fare zones from FareFrame.
 * Mirrors PublicationDeliveryTariffZoneImporter but operates on FareFrame instead of SiteFrame.
 */
@Service
public class PublicationDeliveryFareFrameImporter {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryFareFrameImporter.class);
    public static final String IMPORT_CORRELATION_ID = "importCorrelationId";

    private final PublicationDeliveryHelper publicationDeliveryHelper;
    private final PublicationDeliveryCreator publicationDeliveryCreator;
    private final TariffZoneImportHandler tariffZoneImportHandler;
    private final BackgroundJobs backgroundJobs;
    private final FareZoneConfig fareZoneConfig;
    private final FareZoneSaverService fareZoneSaverService;

    @Autowired
    public PublicationDeliveryFareFrameImporter(
            PublicationDeliveryHelper publicationDeliveryHelper,
            PublicationDeliveryCreator publicationDeliveryCreator,
            TariffZoneImportHandler tariffZoneImportHandler,
            BackgroundJobs backgroundJobs,
            FareZoneConfig fareZoneConfig,
            FareZoneSaverService fareZoneSaverService) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.publicationDeliveryCreator = publicationDeliveryCreator;
        this.tariffZoneImportHandler = tariffZoneImportHandler;
        this.backgroundJobs = backgroundJobs;
        this.fareZoneConfig = fareZoneConfig;
        this.fareZoneSaverService = fareZoneSaverService;
    }

    public PublicationDeliveryStructure importPublicationDelivery(
            PublicationDeliveryStructure incomingPublicationDelivery) {
        return importPublicationDelivery(incomingPublicationDelivery, null);
    }

    @SuppressWarnings("unchecked")
    public PublicationDeliveryStructure importPublicationDelivery(
            PublicationDeliveryStructure incomingPublicationDelivery,
            ImportParams importParams) {

        if (incomingPublicationDelivery.getDataObjects() == null) {
            String responseMessage = "Received publication delivery but it does not contain any data objects.";
            logger.warn(responseMessage);
            throw new RuntimeException(responseMessage);
        }

        if (importParams == null) {
            importParams = new ImportParams();
        }

        logger.info("Got publication delivery for FareFrame import with {} frames and description {}",
                incomingPublicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame().size(),
                incomingPublicationDelivery.getDescription());

        AtomicInteger fareZoneCounter = new AtomicInteger(0);

        // Extract FareFrame
        FareFrame netexFareFrame = publicationDeliveryHelper.findFareFrame(incomingPublicationDelivery);

        if (netexFareFrame == null) {
            String errorMessage = "FareFrame import requested but no FareFrame found in publication delivery";
            logger.warn(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        String requestId = netexFareFrame.getId();

        // Update mapping context (using FareFrame as Common_VersionFrameStructure)
        NetexMappingContextThreadLocal.updateMappingContext(netexFareFrame);

        try {
            MDC.put(IMPORT_CORRELATION_ID, requestId);
            logger.info("Publication delivery contains FareFrame created at {}", netexFareFrame.getCreated());

            // Create response FareFrame
            FareFrame responseFareFrame = new FareFrame();
            responseFareFrame.withId(requestId + "-response").withVersion("1");

            // Import fare zones from FareFrame and collect imported netexIds
            Set<String> importedNetexIds = tariffZoneImportHandler.handleFareZonesFromFareFrame(
                    netexFareFrame,
                    importParams,
                    fareZoneCounter,
                    responseFareFrame
            );

            // Cleanup orphaned FareZones if external versioning is enabled
            if (fareZoneConfig.isExternalVersioning() && !importedNetexIds.isEmpty()) {
                int deletedCount = fareZoneSaverService.deleteAllExcept(importedNetexIds);
                logger.info("External versioning cleanup: deleted {} orphaned FareZones", deletedCount);
            }

            // Trigger background job if zones were imported
            if (responseFareFrame.getFareZones() != null) {
                backgroundJobs.triggerStopPlaceUpdate();
            }

            logger.info("Imported {} fare zones from FareFrame", fareZoneCounter.get());

            // Create publication delivery with FareFrame only
            return publicationDeliveryCreator.createPublicationDelivery(responseFareFrame);
        } finally {
            MDC.remove(IMPORT_CORRELATION_ID);
        }
    }
}