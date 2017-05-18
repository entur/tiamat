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
    protected TariffZone saveNewVersion(TariffZone existingVersion, TariffZone newVersion) {
        TariffZone saved = super.saveNewVersion(existingVersion, newVersion);
        tariffZonesLookupService.reset();
        return saved;
    }

    @Override
    public EntityInVersionRepository<TariffZone> getRepository() {
        return tariffZoneRepository;
    }
}
