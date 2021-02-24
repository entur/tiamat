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


import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.rutebanken.tiamat.service.FareZonesLookupService;
import org.rutebanken.tiamat.versioning.validate.VersionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FareZoneSaverService {

    private final FareZoneRepository fareZoneRepository;
    private final FareZonesLookupService fareZonesLookupService;
    private final DefaultVersionedSaverService defaultVersionedSaverService;
    private final VersionValidator versionValidator;

    @Autowired
    public FareZoneSaverService(FareZoneRepository fareZoneRepository,
                                FareZonesLookupService fareZonesLookupService,
                                DefaultVersionedSaverService defaultVersionedSaverService,
                                VersionValidator versionValidator) {
        this.fareZoneRepository = fareZoneRepository;
        this.fareZonesLookupService = fareZonesLookupService;
        this.defaultVersionedSaverService = defaultVersionedSaverService;
        this.versionValidator = versionValidator;
    }

    public FareZone saveNewVersion(FareZone newVersion) {
        FareZone existingFareZone;
        if (newVersion.getNetexId() != null) {
            existingFareZone = fareZoneRepository.findFirstByNetexIdOrderByVersionDesc(newVersion.getNetexId());
        } else {
            existingFareZone = null;
        }
        FareZone  saved = defaultVersionedSaverService.saveNewVersion(existingFareZone, newVersion, fareZoneRepository);
        fareZonesLookupService.reset();
        return saved;
    }

    public FareZone saveNewVersion(FareZone existingVersion, FareZone newVersion) {
        versionValidator.validate(existingVersion, newVersion);
        FareZone  saved = defaultVersionedSaverService.saveNewVersion(existingVersion, newVersion, fareZoneRepository);
        fareZonesLookupService.reset();
        return saved;
    }

}
