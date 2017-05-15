package org.rutebanken.tiamat.versioning;


import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class TopographicPlaceVersionedSaverService extends VersionedSaverService<TopographicPlace> {

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private TopographicPlaceLookupService topographicPlaceLookupService;

    @Override
    protected TopographicPlace saveNewVersion(TopographicPlace existingVersion, TopographicPlace newVersion) {
        TopographicPlace saved = super.saveNewVersion(existingVersion, newVersion);
        topographicPlaceLookupService.reset();
        return saved;
    }

    @Override
    public EntityInVersionRepository<TopographicPlace> getRepository() {
        return topographicPlaceRepository;
    }
}
