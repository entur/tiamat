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

package org.rutebanken.tiamat.versioning;


import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.rutebanken.tiamat.service.ObjectMerger;
import org.rutebanken.tiamat.service.TariffZonesLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * No history for tariff zones. Overwrites existing version for tariff zone
 */
@Component
public class TariffZoneSaverService extends VersionedSaverService<TariffZone> {

    private static final Logger logger = LoggerFactory.getLogger(TariffZoneSaverService.class);

    private final TariffZoneRepository tariffZoneRepository;
    private final TariffZonesLookupService tariffZonesLookupService;

    public TariffZoneSaverService(TariffZoneRepository tariffZoneRepository, TariffZonesLookupService tariffZonesLookupService) {
        this.tariffZoneRepository = tariffZoneRepository;
        this.tariffZonesLookupService = tariffZonesLookupService;
    }


    @Override
    public TariffZone saveNewVersion(TariffZone existingVersion, TariffZone newVersion) {
        return saveNewVersion(newVersion);
    }

    @Override
    public TariffZone saveNewVersion(TariffZone newVersion) {

        TariffZone existing = tariffZoneRepository.findFirstByNetexIdOrderByVersionDesc(newVersion.getNetexId());

        TariffZone result;
        if(existing != null) {
            BeanUtils.copyProperties(newVersion, existing, "id", "created", "version");
            existing.setValidBetween(null);
            existing.setChanged(Instant.now());
            versionIncrementor.incrementVersion(existing);
            result = tariffZoneRepository.save(existing);

        } else {
            newVersion.setCreated(Instant.now());
            newVersion.setVersion(1L);
            result = tariffZoneRepository.save(newVersion);
        }

        logger.info("Saved tariff zone {}, version {}, name {}", result.getNetexId(), result.getVersion(), result.getName());

        tariffZonesLookupService.reset();
        metricsService.registerEntitySaved(newVersion.getClass());
        return result;
    }

    @Override
    public EntityInVersionRepository<TariffZone> getRepository() {
        return tariffZoneRepository;
    }
}
