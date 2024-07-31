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
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.tiamat.exporter.PublicationDeliveryCreator;
import org.rutebanken.tiamat.importer.handler.GroupOfTariffZonesImportHandler;
import org.rutebanken.tiamat.importer.handler.TariffZoneImportHandler;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.service.batch.BackgroundJobs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

import static org.rutebanken.tiamat.netex.mapping.NetexMappingContextThreadLocal.updateMappingContext;

@Service
public class PublicationDeliveryTariffZoneImporter {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryTariffZoneImporter.class);

    public static final String IMPORT_CORRELATION_ID = "importCorrelationId";


    private final PublicationDeliveryHelper publicationDeliveryHelper;
    private final PublicationDeliveryCreator publicationDeliveryCreator;
    private final TariffZoneImportHandler tariffZoneImportHandler;
    private final GroupOfTariffZonesImportHandler groupOfTariffZonesImportHandler;
    private final BackgroundJobs backgroundJobs;

    @Autowired
    public PublicationDeliveryTariffZoneImporter(PublicationDeliveryHelper publicationDeliveryHelper,
                                                 PublicationDeliveryCreator publicationDeliveryCreator,
                                                 TariffZoneImportHandler tariffZoneImportHandler,
                                                 GroupOfTariffZonesImportHandler groupOfTariffZonesImportHandler,
                                                 BackgroundJobs backgroundJobs) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.publicationDeliveryCreator = publicationDeliveryCreator;
        this.tariffZoneImportHandler = tariffZoneImportHandler;
        this.groupOfTariffZonesImportHandler = groupOfTariffZonesImportHandler;
        this.backgroundJobs = backgroundJobs;
    }


    public PublicationDeliveryStructure importPublicationDelivery(PublicationDeliveryStructure incomingPublicationDelivery) {
        return importPublicationDelivery(incomingPublicationDelivery, null);
    }

    @SuppressWarnings("unchecked")
    public PublicationDeliveryStructure importPublicationDelivery(PublicationDeliveryStructure incomingPublicationDelivery, ImportParams importParams) {

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

        AtomicInteger tariffZoneCounter = new AtomicInteger(0);

        // Currently only supporting one site frame per publication delivery
        SiteFrame netexSiteFrame = publicationDeliveryHelper.findSiteFrame(incomingPublicationDelivery);

        String requestId = netexSiteFrame.getId();

        updateMappingContext(netexSiteFrame);

        try {
            SiteFrame responseSiteFrame = new SiteFrame();

            MDC.put(IMPORT_CORRELATION_ID, requestId);
            logger.info("Publication delivery contains site frame created at {}", netexSiteFrame.getCreated());

            responseSiteFrame.withId(requestId + "-response").withVersion("1");

            tariffZoneImportHandler.handleTariffZones(netexSiteFrame, importParams, tariffZoneCounter, responseSiteFrame);
            groupOfTariffZonesImportHandler.handleGroupOfTariffZones(netexSiteFrame,importParams,responseSiteFrame);

            if(responseSiteFrame.getTariffZones() != null || responseSiteFrame.getTopographicPlaces() != null) {
                backgroundJobs.triggerStopPlaceUpdate();
            }
            return publicationDeliveryCreator.createPublicationDelivery(responseSiteFrame);
        } finally {
            MDC.remove(IMPORT_CORRELATION_ID);
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
