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

import org.rutebanken.netex.model.GroupOfTariffZones;
import org.rutebanken.tiamat.config.GroupOfTariffZonesConfig;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.versioning.save.GroupOffTariffZonesSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
@Component
public class GroupOfTariffZonesImporter {

    private static final Logger logger = LoggerFactory.getLogger(GroupOfTariffZonesImporter.class);

    private final NetexMapper netexMapper;
    private final GroupOffTariffZonesSaverService groupOffTariffZonesSaverService;
    private final GroupOfTariffZonesConfig groupOfTariffZonesConfig;

    public GroupOfTariffZonesImporter(NetexMapper netexMapper,
                                      GroupOffTariffZonesSaverService groupOffTariffZonesSaverService,
                                      GroupOfTariffZonesConfig groupOfTariffZonesConfig) {
        this.netexMapper = netexMapper;
        this.groupOffTariffZonesSaverService = groupOffTariffZonesSaverService;
        this.groupOfTariffZonesConfig = groupOfTariffZonesConfig;
    }

    public GroupOfTariffZonesImportResult importGroupOfTariffZones(List<org.rutebanken.tiamat.model.GroupOfTariffZones> groupOfTariffZones) {

        boolean externalVersioning = groupOfTariffZonesConfig.isExternalVersioning();
        Set<String> importedNetexIds = new HashSet<>();
        List<GroupOfTariffZones> importedGroups = new ArrayList<>();

        for (org.rutebanken.tiamat.model.GroupOfTariffZones incomingGroup : groupOfTariffZones) {
            logger.info("Importing group of tariff zone {}, version {} (external versioning: {})",
                    incomingGroup.getNetexId(), incomingGroup.getVersion(), externalVersioning);

            org.rutebanken.tiamat.model.GroupOfTariffZones saved = externalVersioning
                    ? groupOffTariffZonesSaverService.saveWithExternalVersioning(incomingGroup)
                    : groupOffTariffZonesSaverService.saveNewVersion(incomingGroup);

            importedNetexIds.add(saved.getNetexId());
            importedGroups.add(netexMapper.getFacade().map(saved, GroupOfTariffZones.class));
        }

        return new GroupOfTariffZonesImportResult(importedGroups, importedNetexIds);
    }

}
