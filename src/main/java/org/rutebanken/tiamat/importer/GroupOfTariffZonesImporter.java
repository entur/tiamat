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
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.versioning.save.GroupOffTariffZonesSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Transactional
@Component
public class GroupOfTariffZonesImporter {

    private static final Logger logger = LoggerFactory.getLogger(GroupOfTariffZonesImporter.class);

    private final NetexMapper netexMapper;
    private final GroupOffTariffZonesSaverService groupOffTariffZonesSaverService;

    public GroupOfTariffZonesImporter(NetexMapper netexMapper,
                                      GroupOffTariffZonesSaverService groupOffTariffZonesSaverService) {
        this.netexMapper = netexMapper;
        this.groupOffTariffZonesSaverService = groupOffTariffZonesSaverService;
    }

    public List<GroupOfTariffZones> importGroupOfTariffZones(List<org.rutebanken.tiamat.model.GroupOfTariffZones> groupOfTariffZones) {

        return groupOfTariffZones
                .stream()
                .peek(incomingGoTZ -> logger.info("Importing group of tariff zone {}, version {}",
                        incomingGoTZ.getNetexId(), incomingGoTZ.getVersion()))
                .map(groupOffTariffZonesSaverService::saveNewVersion)
                .map(savedGoTZ -> netexMapper.getFacade().map(savedGoTZ, GroupOfTariffZones.class))
                .collect(toList());
    }

}
