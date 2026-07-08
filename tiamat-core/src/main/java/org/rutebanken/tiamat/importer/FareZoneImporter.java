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

import org.rutebanken.netex.model.FareZone;
import org.rutebanken.tiamat.config.FareZoneConfig;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.versioning.save.FareZoneSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Transactional
@Component
public class FareZoneImporter {

    private static final Logger logger = LoggerFactory.getLogger(FareZoneImporter.class);

    @Autowired
    private NetexMapper netexMapper;

    @Autowired
    private FareZoneSaverService fareZoneSaverService;

    @Autowired
    private FareZoneConfig fareZoneConfig;


    public FareZoneImportResult importFareZones(List<org.rutebanken.tiamat.model.FareZone> fareZones) {
        Set<String> importedNetexIds = new HashSet<>();

        List<FareZone> importedFareZones = fareZones
                .stream()
                .peek(incomingFareZone -> logger.info("Importing fare zone {}, version {}, name {}. Has polygon? {}",
                        incomingFareZone.getNetexId(), incomingFareZone.getVersion(),
                        incomingFareZone.getName(), incomingFareZone.getPolygon() != null && incomingFareZone.getPolygon().getExteriorRing() != null))
                .map(incomingFareZone -> {
                    org.rutebanken.tiamat.model.FareZone saved;

                    if (fareZoneConfig.isExternalVersioning()) {
                        saved = fareZoneSaverService.saveWithExternalVersioning(incomingFareZone);
                        if (saved != null) {
                            logger.debug("Saved FareZone {} with external versioning", saved.getNetexId());
                        } else {
                            logger.warn("FareZone {} was not saved due to validation failure", incomingFareZone.getNetexId());
                        }
                    } else {
                        saved = fareZoneSaverService.saveNewVersion(incomingFareZone);
                        logger.debug("Saved FareZone {} with default versioning", saved.getNetexId());
                    }

                    if (saved != null && saved.getNetexId() != null) {
                        importedNetexIds.add(saved.getNetexId());
                    }

                    return saved;
                })
                .filter(Objects::nonNull) // Filter out validation failures
                .map(savedFareZone -> netexMapper.getFacade().map(savedFareZone, FareZone.class))
                .toList();

        return new FareZoneImportResult(importedFareZones, importedNetexIds);
    }

}
