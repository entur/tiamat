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


import org.rutebanken.tiamat.model.GroupOfTariffZones;
import org.rutebanken.tiamat.repository.GroupOfTariffZonesRepository;
import org.rutebanken.tiamat.versioning.validate.VersionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupOffTariffZonesSaverService {

    private final GroupOfTariffZonesRepository groupOfTariffZonesRepository;
    private final DefaultVersionedSaverService defaultVersionedSaverService;
    private final VersionValidator versionValidator;

    @Autowired
    public GroupOffTariffZonesSaverService(GroupOfTariffZonesRepository groupOfTariffZonesRepository,
                                           DefaultVersionedSaverService defaultVersionedSaverService,
                                           VersionValidator versionValidator) {
        this.groupOfTariffZonesRepository = groupOfTariffZonesRepository;
        this.defaultVersionedSaverService = defaultVersionedSaverService;
        this.versionValidator = versionValidator;
    }

    public GroupOfTariffZones saveNewVersion(GroupOfTariffZones newVersion) {
        GroupOfTariffZones existingGroupOfTariffZone;
        if (newVersion.getNetexId() != null) {
            existingGroupOfTariffZone = groupOfTariffZonesRepository.findFirstByNetexIdOrderByVersionDesc(newVersion.getNetexId());
        } else {
            existingGroupOfTariffZone = null;
        }
        GroupOfTariffZones  saved = defaultVersionedSaverService.saveNewVersion(existingGroupOfTariffZone, newVersion, groupOfTariffZonesRepository);
        return saved;
    }

    public GroupOfTariffZones saveNewVersion(GroupOfTariffZones existingVersion, GroupOfTariffZones newVersion) {
        versionValidator.validate(existingVersion, newVersion);
        GroupOfTariffZones  saved = defaultVersionedSaverService.saveNewVersion(existingVersion, newVersion, groupOfTariffZonesRepository);
        return saved;
    }

}
