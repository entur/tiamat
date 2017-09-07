package org.rutebanken.tiamat.service;

import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.rutebanken.tiamat.versioning.ValidityUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class StopPlaceTerminator {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceQuayDeleter.class);

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private ValidityUpdater validityUpdater;

    @Autowired
    private UsernameFetcher usernameFetcher;

    public StopPlace terminateStopPlace(String stopPlaceId, Instant timeOfTermination, String versionComment) {

        logger.info("User {} is terminating stop {} at {} with comment {}", usernameFetcher, stopPlaceId, timeOfTermination, versionComment);

        StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);

        // Stop Place Saver service should always check that the user is authorized

        if (stopPlace != null) {
            StopPlace nextVersionStopPlace = stopPlaceVersionedSaverService.createCopy(stopPlace, StopPlace.class);

            nextVersionStopPlace.setVersionComment(versionComment);

            validityUpdater.terminateVersion(nextVersionStopPlace, timeOfTermination);

            return stopPlaceVersionedSaverService.saveNewVersion(stopPlace, nextVersionStopPlace);
        } else {
            logger.warn("Cannot find stop place to terminate: {}. No changes executed.", stopPlaceId);
        }
        return stopPlace;
    }
}
