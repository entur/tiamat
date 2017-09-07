package org.rutebanken.tiamat.service.stopplace;

import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StopPlaceReopener {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceQuayDeleter.class);

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private UsernameFetcher usernameFetcher;

    public StopPlace reopenStopPlace(String stopPlaceId, String versionComment) {

        logger.info("User {} is reopening stop place {} with comment {}", usernameFetcher.getUserNameForAuthenticatedUser(), stopPlaceId, versionComment);

        StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);

        if (stopPlace != null) {

            // TODO: Assert that version of stop place is not currently "open"

            StopPlace nextVersionStopPlace = stopPlaceVersionedSaverService.createCopy(stopPlace, StopPlace.class);
            nextVersionStopPlace.setVersionComment(versionComment);

            return stopPlaceVersionedSaverService.saveNewVersion(stopPlace, nextVersionStopPlace);
        }
        return stopPlace;
    }

}
