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

import jakarta.xml.bind.JAXBElement;
import org.rutebanken.netex.model.FareFrame;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.FareZonesInFrame_RelStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.Zone_VersionStructure;
import org.rutebanken.tiamat.importer.FareZoneImporter;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.importer.TariffZoneImporter;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class TariffZoneImportHandler {

    private static final Logger logger = LoggerFactory.getLogger(TariffZoneImportHandler.class);

    private final PublicationDeliveryHelper publicationDeliveryHelper;

    private final NetexMapper netexMapper;

    private final TariffZoneImporter tariffZoneImporter;

    private final FareZoneImporter fareZoneImporter;

    public TariffZoneImportHandler(PublicationDeliveryHelper publicationDeliveryHelper,
                                   NetexMapper netexMapper,
                                   TariffZoneImporter tariffZoneImporter,
                                   FareZoneImporter fareZoneImporter) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.netexMapper = netexMapper;
        this.tariffZoneImporter = tariffZoneImporter;
        this.fareZoneImporter = fareZoneImporter;
    }


    public void handleTariffZones(SiteFrame netexSiteFrame, ImportParams importParams, AtomicInteger tariffZoneImportedCounter, SiteFrame responseSiteframe) {


        if (publicationDeliveryHelper.hasTariffZones(netexSiteFrame) && importParams.importType != ImportType.ID_MATCH) {
            List<org.rutebanken.tiamat.model.TariffZone> tiamatTariffZones = netexSiteFrame.getTariffZones().getTariffZone().stream()
                    .filter(this::isTariffZone)
                    .map(jaxbElement -> (TariffZone) jaxbElement.getValue())
                    .map(netexMapper::mapToTiamatModel)
                    .collect(Collectors.toList());

            List<org.rutebanken.tiamat.model.FareZone> tiamatFareZones = netexSiteFrame.getTariffZones().getTariffZone().stream()
                    .filter(this::isFareZone)
                    .map(jaxbElement -> (FareZone) jaxbElement.getValue())
                    .map(netexMapper::mapToTiamatModel)
                    .collect(Collectors.toList());

            logger.debug("Mapped {} tariff zones from netex to internal model", tiamatTariffZones.size());
            List<JAXBElement<? extends Zone_VersionStructure>> importedTariffZones = tariffZoneImporter.importTariffZones(tiamatTariffZones).stream()
                    .map(tariffZone -> new ObjectFactory().createTariffZone(tariffZone)).collect(Collectors.toList());
            logger.debug("Got {} imported tariffZones ", importedTariffZones.size());

            List<JAXBElement<? extends Zone_VersionStructure>> importedFareZones = fareZoneImporter.importFareZones(tiamatFareZones).getImportedFareZones().stream()
                    .map(fareZone -> new ObjectFactory().createFareZone(fareZone)).collect(Collectors.toList());
            if (!importedTariffZones.isEmpty()) {
                responseSiteframe.withTariffZones(new TariffZonesInFrame_RelStructure().withTariffZone(importedTariffZones));
            }
            if (!importedFareZones.isEmpty()) {
                responseSiteframe.withTariffZones(new TariffZonesInFrame_RelStructure().withTariffZone(importedFareZones));
            }
        }
    }

    /**
     * Handle fare zones from FareFrame.
     * Extracts and imports FareZones from FareFrame, populating the response FareFrame.
     *
     * @param netexFareFrame Input FareFrame containing fare zones to import
     * @param importParams Import parameters
     * @param tariffZoneImportedCounter Counter for imported zones
     * @param responseFareFrame Response FareFrame to populate with imported zones
     * @return Set of imported FareZone netexIds (for external versioning cleanup)
     */
    public java.util.Set<String> handleFareZonesFromFareFrame(
            FareFrame netexFareFrame,
            ImportParams importParams,
            AtomicInteger tariffZoneImportedCounter,
            FareFrame responseFareFrame) {

        if (!publicationDeliveryHelper.hasFareZonesInFareFrame(netexFareFrame)) {
            logger.debug("No fare zones found in FareFrame");
            return java.util.Collections.emptySet();
        }

        if (importParams.importType == ImportType.ID_MATCH) {
            logger.debug("Skipping fare zone import for ID_MATCH import type");
            return java.util.Collections.emptySet();
        }

        logger.info("Processing {} fare zones from FareFrame",
                netexFareFrame.getFareZones().getFareZone().size());

        // Extract FareZone objects from the frame
        List<org.rutebanken.tiamat.model.FareZone> tiamatFareZones = netexFareFrame
                .getFareZones()
                .getFareZone()
                .stream()
                .map(netexMapper::mapToTiamatModel)
                .collect(Collectors.toList());

        logger.debug("Mapped {} fare zones from NeTEx to internal model", tiamatFareZones.size());

        // Import using the existing FareZoneImporter - now returns ImportResult
        org.rutebanken.tiamat.importer.FareZoneImportResult importResult = fareZoneImporter.importFareZones(tiamatFareZones);

        logger.debug("Imported {} fare zones", importResult.getImportedFareZones().size());

        // Update counter
        tariffZoneImportedCounter.addAndGet(importResult.getImportedFareZones().size());

        // Populate response FareFrame if there are imported zones
        if (!importResult.getImportedFareZones().isEmpty()) {
            FareZonesInFrame_RelStructure fareZonesInFrame = new FareZonesInFrame_RelStructure();
            fareZonesInFrame.getFareZone().addAll(importResult.getImportedFareZones());
            responseFareFrame.setFareZones(fareZonesInFrame);
        }

        return importResult.getImportedNetexIds();
    }

    private boolean isTariffZone(JAXBElement<? extends Zone_VersionStructure> jaxbElement) {
        return jaxbElement.getValue() instanceof TariffZone;
    }

    private boolean isFareZone(JAXBElement<? extends Zone_VersionStructure> jaxbElement) {
        return jaxbElement.getValue() instanceof FareZone;
    }

}
