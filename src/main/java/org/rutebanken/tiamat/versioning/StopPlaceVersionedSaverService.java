package org.rutebanken.tiamat.versioning;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;
import org.rutebanken.tiamat.changelog.EntityChangedListener;
import org.rutebanken.tiamat.diff.TiamatObjectDiffer;
import org.rutebanken.tiamat.diff.generic.GenericObjectDiffer;
import org.rutebanken.tiamat.importer.finder.NearbyStopPlaceFinder;
import org.rutebanken.tiamat.importer.finder.StopPlaceByQuayOriginalIdFinder;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.TariffZonesLookupService;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;
import org.rutebanken.tiamat.versioning.util.AccessibilityAssessmentOptimizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;
import java.time.Instant;
import java.util.stream.Collectors;



@Transactional
@Service
public class StopPlaceVersionedSaverService extends VersionedSaverService<StopPlace> {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceVersionedSaverService.class);

    private final StopPlaceRepository stopPlaceRepository;

    private final VersionCreator versionCreator;

    private final AccessibilityAssessmentOptimizer accessibilityAssessmentOptimizer;

    private final TopographicPlaceLookupService countyAndMunicipalityLookupService;

    private final TariffZonesLookupService tariffZonesLookupService;

    private final StopPlaceByQuayOriginalIdFinder stopPlaceByQuayOriginalIdFinder;

    private final NearbyStopPlaceFinder nearbyStopPlaceFinder;

    private final EntityChangedListener entityChangedListener;

    private final TiamatObjectDiffer tiamatObjectDiffer;


    @Autowired
    public StopPlaceVersionedSaverService(StopPlaceRepository stopPlaceRepository,
                                          VersionCreator versionCreator,
                                          AccessibilityAssessmentOptimizer accessibilityAssessmentOptimizer,
                                          TopographicPlaceLookupService countyAndMunicipalityLookupService,
                                          TariffZonesLookupService tariffZonesLookupService,
                                          StopPlaceByQuayOriginalIdFinder stopPlaceByQuayOriginalIdFinder,
                                          NearbyStopPlaceFinder nearbyStopPlaceFinder,
                                          EntityChangedListener entityChangedListener,
                                          TiamatObjectDiffer tiamatObjectDiffer) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.versionCreator = versionCreator;
        this.accessibilityAssessmentOptimizer = accessibilityAssessmentOptimizer;
        this.countyAndMunicipalityLookupService = countyAndMunicipalityLookupService;
        this.tariffZonesLookupService = tariffZonesLookupService;
        this.stopPlaceByQuayOriginalIdFinder = stopPlaceByQuayOriginalIdFinder;
        this.nearbyStopPlaceFinder = nearbyStopPlaceFinder;
        this.entityChangedListener = entityChangedListener;
        this.tiamatObjectDiffer = tiamatObjectDiffer;
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

        if (existingVersion == null) {
            logger.debug("Existing version is not present, which means new entity. {}", newVersion);
            newVersion.setCreated(Instant.now());
        } else {
            newVersion.setChanged(Instant.now());
            // TODO: Add support for "valid from/to" being explicitly set

            logger.debug("About to terminate previous version for {},{}", existingVersion.getNetexId(), existingVersion.getVersion());
            StopPlace existingStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(existingVersion.getNetexId());
            logger.debug("Found previous version {},{}", existingStopPlace.getNetexId(), existingStopPlace.getVersion());
            versionCreator.terminateVersion(existingStopPlace, Instant.now());
        }

        // Save latest version
        newVersion = initiateOrIncrementVersions(newVersion);
        countyAndMunicipalityLookupService.populateTopographicPlaceRelation(newVersion);
        tariffZonesLookupService.populateTariffZone(newVersion);
        newVersion = stopPlaceRepository.save( newVersion);
        if(existingVersion != null) {
           tiamatObjectDiffer.logDifference(existingVersion, newVersion);
        }

        if(newVersion.getQuays() != null) {
            stopPlaceByQuayOriginalIdFinder.updateCache(newVersion.getNetexId(),
                    newVersion.getQuays()
                            .stream()
                            .flatMap(q -> q.getOriginalIds().stream())
                            .collect(Collectors.toList()));
        }
        nearbyStopPlaceFinder.update(newVersion);
        entityChangedListener.onChange(newVersion);
        return newVersion;
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
        Instant now = Instant.now();

        ValidBetween validBetween;
        if (stopPlace.getValidBetween() != null) {
            validBetween = stopPlace.getValidBetween();
        } else {
            validBetween = new ValidBetween();
            stopPlace.setValidBetween(validBetween);
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
