package org.rutebanken.tiamat.versioning;


import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class TopographicPlaceVersionedSaverService extends VersionedSaverService<TopographicPlace> {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceVersionedSaverService.class);

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private VersionCreator versionCreator;

    @Override
    public TopographicPlace saveNewVersion(TopographicPlace existingVersion, TopographicPlace newVersion) {

        if(existingVersion == null) {
            newVersion.setCreated(ZonedDateTime.now());
        } else {
            TopographicPlace existingTopographicPlace = topographicPlaceRepository.findFirstByNetexIdAndVersion(existingVersion.getNetexId(), existingVersion.getVersion());
            existingTopographicPlace = versionCreator.terminateVersion(existingVersion, ZonedDateTime.now());
            topographicPlaceRepository.save(existingTopographicPlace);
        }

        versionCreator.initiateOrIncrement(newVersion);
        return topographicPlaceRepository.save(newVersion);
    }
}
