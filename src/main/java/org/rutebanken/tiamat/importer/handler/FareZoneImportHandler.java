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

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class FareZoneImportHandler {

    private static final Logger logger = LoggerFactory.getLogger(FareZoneImportHandler.class);

    private final PublicationDeliveryHelper publicationDeliveryHelper;

    private final NetexMapper netexMapper;

    private final FareZoneImporter fareZoneImporter;

    public FareZoneImportHandler(PublicationDeliveryHelper publicationDeliveryHelper,
                                 NetexMapper netexMapper,
                                 FareZoneImporter fareZoneImporter) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.netexMapper = netexMapper;
        this.fareZoneImporter = fareZoneImporter;
    }


    public void handleFareZones(FareFrame netexFareFrame, ImportParams importParams, FareFrame responseFareframe) {


        if (publicationDeliveryHelper.hasFareZones(netexFareFrame) && importParams.importType != ImportType.ID_MATCH) {

            List<org.rutebanken.tiamat.model.FareZone> tiamatFareZones = netexFareFrame.getFareZones().getFareZone().stream()
                    .map(netexMapper::mapToTiamatModel)
                    .collect(Collectors.toList());
            logger.debug("Mapped {} fare zones from netex to internal model", tiamatFareZones.size());


            List<FareZone> importedFareZones = fareZoneImporter.importFareZones(tiamatFareZones);

            if (!importedFareZones.isEmpty()) {
                responseFareframe.withFareZones(new FareZonesInFrame_RelStructure().withFareZone(importedFareZones));
            }
        }
    }

}
