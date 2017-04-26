package org.rutebanken.tiamat.versioning;


import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TariffZoneVersionedSaverService extends VersionedSaverService<TariffZone> {

    @Autowired
    private TariffZoneRepository tariffZoneRepository;

    @Override
    public EntityInVersionRepository<TariffZone> getRepository() {
        return tariffZoneRepository;
    }
}
