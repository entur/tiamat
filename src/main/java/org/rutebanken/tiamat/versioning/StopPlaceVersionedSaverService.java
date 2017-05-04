package org.rutebanken.tiamat.versioning;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.ValidBetweenRepository;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;
import org.rutebanken.tiamat.service.TariffZonesLookupService;
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

    private final ValidBetweenRepository validBetweenRepository;

    private final VersionCreator versionCreator;

    private final AccessibilityAssessmentOptimizer accessibilityAssessmentOptimizer;

    private final TopographicPlaceLookupService countyAndMunicipalityLookupService;

    private final TariffZonesLookupService tariffZonesLookupService;

    @Autowired
    public StopPlaceVersionedSaverService(StopPlaceRepository stopPlaceRepository,
                                          ValidBetweenRepository validBetweenRepository,
                                          VersionCreator versionCreator,
                                          AccessibilityAssessmentOptimizer accessibilityAssessmentOptimizer,
                                          TopographicPlaceLookupService countyAndMunicipalityLookupService,
                                          TariffZonesLookupService tariffZonesLookupService) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.validBetweenRepository = validBetweenRepository;
        this.versionCreator = versionCreator;
        this.accessibilityAssessmentOptimizer = accessibilityAssessmentOptimizer;
        this.countyAndMunicipalityLookupService = countyAndMunicipalityLookupService;
        this.tariffZonesLookupService = tariffZonesLookupService;
    }

    @Override
    public EntityInVersionRepository<StopPlace> getRepository() {
        return stopPlaceRepository;
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

            logger.debug("About to terminate previous version for {},{}", existingVersion.getNetexId(), existingVersion.getVersion());
            StopPlace existingStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(existingVersion.getNetexId());
            logger.debug("Found previous version {},{}", existingStopPlace.getNetexId(), existingStopPlace.getVersion());
            existingStopPlace = versionCreator.terminateVersion(existingStopPlace, ZonedDateTime.now());

            if (existingStopPlace.getValidBetweens() != null && !existingStopPlace.getValidBetweens().isEmpty()) {
                validBetweenRepository.save(existingStopPlace.getValidBetweens());
            }
        }

        // Save latest version
        stopPlaceToSave = initiateOrIncrementVersions(stopPlaceToSave);
        countyAndMunicipalityLookupService.populateTopographicPlaceRelation(stopPlaceToSave);
        tariffZonesLookupService.populateTariffZone(stopPlaceToSave);
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

        ValidBetween validBetween;
        if (!stopPlace.getValidBetweens().isEmpty()) {
            validBetween = stopPlace.getValidBetweens().get(0);
        } else {
            validBetween = new ValidBetween();
            stopPlace.getValidBetweens().add(validBetween);
        }

        if (validBetween.getFromDate() == null) {
            validBetween.setFromDate(now);
            validBetween.setToDate(null);
        }

        return stopPlace;
    }

    private void initiateOrIncrementVersionsForChildren(StopPlace stopPlaceToSave) {

        versionCreator.initiateOrIncrementAccessibilityAssesmentVersion(stopPlaceToSave);

        if (stopPlaceToSave.getAlternativeNames() != null) {
            versionCreator.initiateOrIncrementAlternativeNamesVersion(stopPlaceToSave.getAlternativeNames());
        }

        if (stopPlaceToSave.getQuays() != null) {
            logger.debug("Initiating first versions for {} quays, accessibility assessment and limitations", stopPlaceToSave.getQuays().size());
            stopPlaceToSave.getQuays().forEach(quay -> {
                versionCreator.initiateOrIncrement(quay);
                versionCreator.initiateOrIncrementAccessibilityAssesmentVersion(quay);
                if (quay.getAlternativeNames() != null) {
                    versionCreator.initiateOrIncrementAlternativeNamesVersion(quay.getAlternativeNames());
                }
            });
        }
    }
}
