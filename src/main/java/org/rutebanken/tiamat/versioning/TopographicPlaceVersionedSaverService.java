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
    public TopographicPlace saveNewVersion(TopographicPlace existingTopographicPlace, TopographicPlace newVersion) {

        if(existingTopographicPlace == null) {
            if (newVersion.getNetexId() != null) {
                existingTopographicPlace = topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(newVersion.getNetexId());
                if (existingTopographicPlace != null) {
                    logger.info("Found existing topographic place from netexId {}", existingTopographicPlace.getNetexId());
                }
            }
        }

        if(existingTopographicPlace == null) {
            newVersion.setCreated(ZonedDateTime.now());
            // If the new incoming version has the version attribute set, reset it.
            // For tiamat, this is the first time this topographic place is saved
            newVersion.setVersion(-1L);
        } else {
            newVersion.setVersion(existingTopographicPlace.getVersion());
            existingTopographicPlace = versionCreator.terminateVersion(existingTopographicPlace, ZonedDateTime.now());
            topographicPlaceRepository.save(existingTopographicPlace);
        }

        versionCreator.initiateOrIncrement(newVersion);
        return topographicPlaceRepository.save(newVersion);
    }
}
