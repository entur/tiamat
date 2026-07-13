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

package org.rutebanken.tiamat.versioning.save;


import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.rutebanken.tiamat.service.TariffZonesLookupService;
import org.rutebanken.tiamat.versioning.validate.VersionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TariffZoneSaverService {

    private final TariffZoneRepository tariffZoneRepository;
    private final TariffZonesLookupService tariffZonesLookupService;
    private final DefaultVersionedSaverService defaultVersionedSaverService;
    private final VersionValidator versionValidator;

    @Autowired
    public TariffZoneSaverService(TariffZoneRepository tariffZoneRepository,
                                  TariffZonesLookupService tariffZonesLookupService,
                                  DefaultVersionedSaverService defaultVersionedSaverService,
                                  VersionValidator versionValidator) {
        this.tariffZoneRepository = tariffZoneRepository;
        this.tariffZonesLookupService = tariffZonesLookupService;
        this.defaultVersionedSaverService = defaultVersionedSaverService;
        this.versionValidator = versionValidator;
    }

    public TariffZone saveNewVersion(TariffZone newVersion) {
        TariffZone existingTariffZone;
        if (newVersion.getNetexId() != null) {
            existingTariffZone = tariffZoneRepository.findFirstByNetexIdOrderByVersionDesc(newVersion.getNetexId());
        } else {
            existingTariffZone = null;
        }
        TariffZone  saved = defaultVersionedSaverService.saveNewVersion(existingTariffZone, newVersion, tariffZoneRepository);
        tariffZonesLookupService.resetTariffZone();
        return saved;
    }

    public TariffZone saveNewVersion(TariffZone existingVersion, TariffZone newVersion) {
        versionValidator.validate(existingVersion, newVersion);
        TariffZone  saved = defaultVersionedSaverService.saveNewVersion(existingVersion, newVersion, tariffZoneRepository);
        tariffZonesLookupService.resetTariffZone();
        return saved;
    }

}
