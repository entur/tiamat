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
import org.rutebanken.netex.model.FareZone;
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
                    .toList();

            List<org.rutebanken.tiamat.model.FareZone> tiamatFareZones = netexSiteFrame.getTariffZones().getTariffZone().stream()
                    .filter(this::isFareZone)
                    .map(jaxbElement -> (FareZone) jaxbElement.getValue())
                    .map(netexMapper::mapToTiamatModel)
                    .toList();

            logger.debug("Mapped {} tariff zones from netex to internal model", tiamatTariffZones.size());
            List<JAXBElement<? extends Zone_VersionStructure>> importedTariffZones = tariffZoneImporter.importTariffZones(tiamatTariffZones).stream()
                    .map(tariffZone -> new ObjectFactory().createTariffZone(tariffZone)).collect(Collectors.toList());
            logger.debug("Got {} imported tariffZones ", importedTariffZones.size());

            List<JAXBElement<? extends Zone_VersionStructure>> importedFareZones = fareZoneImporter.importFareZones(tiamatFareZones).stream()
                    .map(fareZone -> new ObjectFactory().createFareZone(fareZone)).collect(Collectors.toList());
            if (!importedTariffZones.isEmpty()) {
                responseSiteframe.withTariffZones(new TariffZonesInFrame_RelStructure().withTariffZone(importedTariffZones));
            }
            if (!importedFareZones.isEmpty()) {
                responseSiteframe.withTariffZones(new TariffZonesInFrame_RelStructure().withTariffZone(importedFareZones));
            }
        }
    }

    private boolean isTariffZone(JAXBElement<? extends Zone_VersionStructure> jaxbElement) {
        return jaxbElement.getValue() instanceof TariffZone;
    }

    private boolean isFareZone(JAXBElement<? extends Zone_VersionStructure> jaxbElement) {
        return jaxbElement.getValue() instanceof FareZone;
    }

}
