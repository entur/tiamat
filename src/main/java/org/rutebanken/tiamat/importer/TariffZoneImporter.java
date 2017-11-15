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

import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.versioning.TariffZoneSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Transactional
@Component
public class TariffZoneImporter {

    private static final Logger logger = LoggerFactory.getLogger(TariffZoneImporter.class);

    @Autowired
    private NetexMapper netexMapper;

    @Autowired
    private TariffZoneSaverService tariffZoneSaverService;

    public List<TariffZone> importTariffZones(List<org.rutebanken.tiamat.model.TariffZone> tariffZones) {

        return tariffZones
                .stream()
                .peek(incomingTariffZone -> logger.info("Importing tariff zone {}, version {}, name {}. Has polygon? {}",
                        incomingTariffZone.getNetexId(), incomingTariffZone.getVersion(),
                        incomingTariffZone.getName(), incomingTariffZone.getPolygon() == null ? false : incomingTariffZone.getPolygon().getExteriorRing() != null))
                .map(incomingTariffZone -> tariffZoneSaverService.saveNewVersion(incomingTariffZone))
                .map(savedTariffZone -> netexMapper.getFacade().map(savedTariffZone, TariffZone.class))
                .collect(toList());
    }

}
