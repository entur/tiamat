package org.rutebanken.tiamat.versioning;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.util.AccessibilityAssessmentOptimizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;


@Transactional
@Service
public class StopPlaceVersionedSaverService {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceVersionedSaverService.class);

    private final StopPlaceRepository stopPlaceRepository;

    private final VersionCreator versionCreator;

    private final AccessibilityAssessmentOptimizer accessibilityAssessmentOptimizer;

    @Autowired
    public StopPlaceVersionedSaverService(StopPlaceRepository stopPlaceRepository, VersionCreator versionCreator, AccessibilityAssessmentOptimizer accessibilityAssessmentOptimizer) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.versionCreator = versionCreator;
        this.accessibilityAssessmentOptimizer = accessibilityAssessmentOptimizer;
    }


    public StopPlace createNewVersion(StopPlace stopPlace) {

        if (stopPlace.getNetexId() == null) {
            stopPlace.setCreated(ZonedDateTime.now());
        } else {
            stopPlace.setChanged(ZonedDateTime.now());
        }
        return versionCreator.createNextVersion(stopPlace);
    }


    public StopPlace saveNewVersion(StopPlace newVersion) {
        return saveNewVersion(null, newVersion);
    }

    public StopPlace saveNewVersion(StopPlace existingVersion, StopPlace newVersion) {

        if (existingVersion == newVersion) {
            throw new IllegalArgumentException("Existing and new StopPlace must be different objects");
        }

        if(existingVersion != null && existingVersion.getNetexId() == null) {
            throw new IllegalArgumentException("Existing stop place must have netexId set: " + existingVersion);
        }

        logger.debug("Rearrange accessibility assessments for: {}", newVersion);
        accessibilityAssessmentOptimizer.optimizeAccessibilityAssessments(newVersion);

        StopPlace stopPlaceToSave;
        if (existingVersion == null) {
            logger.debug("Existing version is not present, which means new entity. Initiating version: {}", newVersion);
            stopPlaceToSave = versionCreator.initiateFirstVersion(newVersion);
        } else if (!existingVersion.getNetexId().equals(newVersion.getNetexId())) {
            throw new IllegalArgumentException("Existing and new StopPlace do not match: " + existingVersion.getNetexId() + " !=" + newVersion.getNetexId());
        } else {
            logger.debug("About terminate previous version of {}", existingVersion.getNetexId());

            stopPlaceToSave = newVersion;
            stopPlaceToSave.setChanged(ZonedDateTime.now());

            // TODO: Add support for "valid from/to" being explicitly set


            StopPlace existingStopPlace = stopPlaceRepository.findFirstByNetexIdAndVersion(existingVersion.getNetexId(), existingVersion.getVersion());
            logger.debug("Invalidate existing version for {},{}", existingStopPlace.getNetexId(), existingStopPlace.getVersion());
            existingStopPlace = versionCreator.terminateVersion(existingStopPlace, ZonedDateTime.now());
            stopPlaceRepository.save(existingStopPlace);
        }

        versionCreator.initiateOrIncrementNewVersionForChildren(stopPlaceToSave);

        // Save latest version
        stopPlaceToSave = stopPlaceRepository.save(stopPlaceToSave);
        return stopPlaceToSave;
    }
}
