package org.rutebanken.tiamat.versioning;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.CountyAndMunicipalityLookupService;
import org.rutebanken.tiamat.versioning.util.AccessibilityAssessmentOptimizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;


@Transactional
@Service
public class StopPlaceVersionedSaverService extends VersionedSaverService<StopPlace> {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceVersionedSaverService.class);

    private final StopPlaceRepository stopPlaceRepository;

    private final VersionCreator versionCreator;

    private final AccessibilityAssessmentOptimizer accessibilityAssessmentOptimizer;

    private final CountyAndMunicipalityLookupService countyAndMunicipalityLookupService;

    @Autowired
    public StopPlaceVersionedSaverService(StopPlaceRepository stopPlaceRepository,
                                          VersionCreator versionCreator,
                                          AccessibilityAssessmentOptimizer accessibilityAssessmentOptimizer,
                                          CountyAndMunicipalityLookupService countyAndMunicipalityLookupService) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.versionCreator = versionCreator;
        this.accessibilityAssessmentOptimizer = accessibilityAssessmentOptimizer;
        this.countyAndMunicipalityLookupService = countyAndMunicipalityLookupService;
    }

    @Override
    public StopPlace saveNewVersion(StopPlace existingVersion, StopPlace newVersion) {

        super.validate(existingVersion, newVersion);

        logger.debug("Rearrange accessibility assessments for: {}", newVersion);
        accessibilityAssessmentOptimizer.optimizeAccessibilityAssessments(newVersion);

        StopPlace stopPlaceToSave;
        if (existingVersion == null) {
            logger.debug("Existing version is not present, which means new entity. {}", newVersion);
            stopPlaceToSave = newVersion;
            newVersion.setCreated(ZonedDateTime.now());
        } else {
            stopPlaceToSave = newVersion;

            stopPlaceToSave.setChanged(ZonedDateTime.now());
            // TODO: Add support for "valid from/to" being explicitly set

            logger.debug("About terminate previous version of {}", existingVersion.getNetexId());
            StopPlace existingStopPlace = stopPlaceRepository.findFirstByNetexIdAndVersion(existingVersion.getNetexId(), existingVersion.getVersion());
            logger.debug("Invalidate existing version for {},{}", existingStopPlace.getNetexId(), existingStopPlace.getVersion());
            existingStopPlace = versionCreator.terminateVersion(existingStopPlace, ZonedDateTime.now());
            stopPlaceRepository.save(existingStopPlace);
        }

        // Save latest version
        stopPlaceToSave = initiateOrIncrementVersions(stopPlaceToSave);
        countyAndMunicipalityLookupService.populateTopographicPlaceRelation(stopPlaceToSave);
        stopPlaceToSave = stopPlaceRepository.save( stopPlaceToSave);
        return stopPlaceToSave;
    }

    /**
     * Increment versions for stop place with children.
     * The object must have their netexId set, or else they will get an initial version
     * @param stopPlace with quays and accessibility assessment
     * @return modified StopPlace
     */
    public StopPlace initiateOrIncrementVersions(StopPlace stopPlace) {
        versionCreator.initiateOrIncrement(stopPlace);
        initiateOrIncrementVersionsForChildren(stopPlace);
        ZonedDateTime now = ZonedDateTime.now();
        stopPlace.getValidBetweens().add(new ValidBetween(now));
        return stopPlace;
    }

    private void initiateOrIncrementVersionsForChildren(StopPlace stopPlaceToSave) {

        versionCreator.initiateOrIncrementAccessibilityAssesmentVersion(stopPlaceToSave);

        if (stopPlaceToSave.getQuays() != null) {
            logger.debug("Initiating first versions for {} quays, accessibility assessment and limitations", stopPlaceToSave.getQuays().size());
            stopPlaceToSave.getQuays().forEach(quay -> {
                versionCreator.initiateOrIncrement(quay);
                versionCreator.initiateOrIncrementAccessibilityAssesmentVersion(quay);
            });
        }
    }
}
