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

import org.rutebanken.netex.model.GroupOfTariffZones;
import org.rutebanken.netex.model.GroupsOfTariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.tiamat.importer.GroupOfTariffZonesImporter;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GroupOfTariffZonesImportHandler {

    private static final Logger logger = LoggerFactory.getLogger(GroupOfTariffZonesImportHandler.class);

    private final PublicationDeliveryHelper publicationDeliveryHelper;

    private final NetexMapper netexMapper;

    private final GroupOfTariffZonesImporter groupOfTariffZonesImporter;
    

    public GroupOfTariffZonesImportHandler(PublicationDeliveryHelper publicationDeliveryHelper,
                                           NetexMapper netexMapper,
                                           GroupOfTariffZonesImporter groupOfTariffZonesImporter) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.netexMapper = netexMapper;
        this.groupOfTariffZonesImporter = groupOfTariffZonesImporter;
    }


    public void handleGroupOfTariffZones(SiteFrame netexSiteFrame, ImportParams importParams, SiteFrame responseSiteframe) {


        if (publicationDeliveryHelper.hasGroupOfTariffZones(netexSiteFrame) && importParams.importType != ImportType.ID_MATCH) {
            List<org.rutebanken.tiamat.model.GroupOfTariffZones> tiamatGroupOfTariffZones = netexSiteFrame.getGroupsOfTariffZones().getGroupOfTariffZones().stream()
                    .map(netexMapper::mapToTiamatModel)
                    .collect(Collectors.toList());
            

            logger.debug("Mapped {} group tariff zones from netex to internal model", tiamatGroupOfTariffZones.size());
            final List<GroupOfTariffZones> importedGroupOfTariffZones = groupOfTariffZonesImporter.importGroupOfTariffZones(tiamatGroupOfTariffZones);
            
            logger.debug("Got {} imported group of tariffZones ", importedGroupOfTariffZones.size());

            if (!importedGroupOfTariffZones.isEmpty()) {
                responseSiteframe.withGroupsOfTariffZones(new GroupsOfTariffZonesInFrame_RelStructure().withGroupOfTariffZones(importedGroupOfTariffZones));

            }

        }
    }

}
