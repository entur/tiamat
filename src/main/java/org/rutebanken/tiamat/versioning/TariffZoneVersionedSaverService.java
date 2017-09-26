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
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.rutebanken.tiamat.service.TariffZonesLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TariffZoneVersionedSaverService extends VersionedSaverService<TariffZone> {

    @Autowired
    private TariffZoneRepository tariffZoneRepository;

    @Autowired
    private TariffZonesLookupService tariffZonesLookupService;

    @Override
    public TariffZone saveNewVersion(TariffZone existingVersion, TariffZone newVersion) {
        TariffZone saved = super.saveNewVersion(existingVersion, newVersion);
        tariffZonesLookupService.reset();
        return saved;
    }

    @Override
    public EntityInVersionRepository<TariffZone> getRepository() {
        return tariffZoneRepository;
    }
}
