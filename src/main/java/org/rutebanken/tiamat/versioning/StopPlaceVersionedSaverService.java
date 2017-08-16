package org.rutebanken.tiamat.versioning;

import org.rutebanken.tiamat.changelog.EntityChangedListener;
import org.rutebanken.tiamat.diff.TiamatObjectDiffer;
import org.rutebanken.tiamat.importer.finder.NearbyStopPlaceFinder;
import org.rutebanken.tiamat.importer.finder.StopPlaceByQuayOriginalIdFinder;
import org.rutebanken.tiamat.model.SiteElement;
import org.rutebanken.tiamat.model.SiteRefStructure;
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

import java.time.Instant;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


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

        Instant validFrom;
        if (newVersion.getValidBetween() != null && newVersion.getValidBetween().getFromDate() != null) {
            validFrom = newVersion.getValidBetween().getFromDate();
        } else {
            validFrom = Instant.now();
        }

        if (existingVersion == null) {
            logger.debug("Existing version is not present, which means new entity. {}", newVersion);
            newVersion.setCreated(Instant.now());
        } else {
            newVersion.setChanged(Instant.now());
            // TODO: Add support for "valid from/to" being explicitly set

            logger.debug("About to terminate previous version for {},{}", existingVersion.getNetexId(), existingVersion.getVersion());
            StopPlace existingStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(existingVersion.getNetexId());
            logger.debug("Found previous version {},{}", existingStopPlace.getNetexId(), existingStopPlace.getVersion());
            versionCreator.terminateVersion(existingStopPlace, validFrom);
        }

        // Save latest version
        newVersion = initiateOrIncrementVersions(newVersion, validFrom);

        newVersion.setChangedBy(getUserNameForAuthenticatedUser());
        logger.info("StopPlace [{}], version {} changed by user [{}].", newVersion.getNetexId(), newVersion.getVersion(), newVersion.getChangedBy());

        countyAndMunicipalityLookupService.populateTopographicPlaceRelation(newVersion);
        tariffZonesLookupService.populateTariffZone(newVersion);
        clearUnwantedChildFields(newVersion);

        if(newVersion.getChildren() != null) {
            stopPlaceRepository.save(newVersion.getChildren());
            if(logger.isDebugEnabled()) {
                logger.debug("Saved children: {}", newVersion.getChildren().stream()
                        .map(sp -> "{id:" + sp.getId() + " netexId:" + sp.getNetexId() + " version:" + sp.getVersion() + "}")
                        .collect(Collectors.toList()));
            }
        }
        newVersion = stopPlaceRepository.save(newVersion);
        logger.debug("Saved stop place with id: {} and childs {}", newVersion.getId(), newVersion.getChildren().stream().map(ch -> ch.getId()).collect(toList()));

        updateParentSiteRefsForChilds(newVersion);

        if(existingVersion != null) {
           tiamatObjectDiffer.logDifference(existingVersion, newVersion);
        }

        updateQuaysCache(newVersion);

        nearbyStopPlaceFinder.update(newVersion);
        newVersion.getChildren().forEach(nearbyStopPlaceFinder::update);
        entityChangedListener.onChange(newVersion);
        newVersion.getChildren().forEach(entityChangedListener::onChange);
        return newVersion;
    }

    /**
     * Increment versions for stop place with children.
     * The object must have their netexId set, or else they will get an initial version
     * @param stopPlace with quays and accessibility assessment
     * @param validFrom
     * @return modified StopPlace
     */
    public StopPlace initiateOrIncrementVersions(StopPlace stopPlace, Instant validFrom) {
        versionCreator.initiateOrIncrement(stopPlace);
        initiateOrIncrementVersionsForChildren(stopPlace);

        ValidBetween validBetween;
        if (stopPlace.getValidBetween() != null) {
            validBetween = stopPlace.getValidBetween();
        } else {
            validBetween = new ValidBetween();
            stopPlace.setValidBetween(validBetween);
        }

        // new validFrom is set
        validBetween.setFromDate(validFrom);
        validBetween.setToDate(null);

        return stopPlace;
    }

    private void updateQuaysCache(StopPlace stopPlace) {
        if(stopPlace.getQuays() != null) {
            stopPlaceByQuayOriginalIdFinder.updateCache(stopPlace.getNetexId(),
                    stopPlace.getQuays()
                            .stream()
                            .flatMap(q -> q.getOriginalIds().stream())
                            .collect(toList()));
        }
        if(stopPlace.isParentStopPlace()) {
            if(stopPlace.getChildren() != null) {
                stopPlace.getChildren().forEach(this::updateQuaysCache);
            }
        }
    }

    private void clearUnwantedChildFields(StopPlace stopPlaceToSave) {
        if(stopPlaceToSave.getChildren() == null) return;
        stopPlaceToSave.getChildren().forEach(child -> {
            child.setName(null);
            child.setValidBetween(null);
            child.setTopographicPlace(null);
            child.setTariffZones(null);
        });
    }

    private void initiateOrIncrementVersionsForChildren(StopPlace stopPlaceToSave) {

        versionCreator.initiateOrIncrementAccessibilityAssesmentVersion(stopPlaceToSave);

        if (stopPlaceToSave.getAlternativeNames() != null) {
            versionCreator.initiateOrIncrementAlternativeNamesVersion(stopPlaceToSave.getAlternativeNames());
        }

        if (stopPlaceToSave.getQuays() != null) {
            logger.debug("Initiating first versions for {} quays, accessibility assessment and limitations", stopPlaceToSave.getQuays().size());
            stopPlaceToSave.getQuays().forEach(quay -> {
                initiateOrIncrementSiteElementVersion(quay);
            });
        }

        if(stopPlaceToSave.getChildren() != null) {
            logger.debug("Initiating versions for {} child stop places. Parent: {}", stopPlaceToSave.getChildren().size(), stopPlaceToSave.getNetexId());
            stopPlaceToSave.getChildren().forEach(child -> {
                initiateOrIncrementSiteElementVersion(child);
                initiateOrIncrementVersionsForChildren(child);
            });
        }
    }

    /**
     * Needs to be done after parent stop place has been assigned an ID
     * @param parentStopPlace saved parent stop place
     */
    private void updateParentSiteRefsForChilds(StopPlace parentStopPlace) {
        long count = 0;
        if(parentStopPlace.getChildren() != null) {
            count = parentStopPlace.getChildren().stream()
                .map(child -> {
                    SiteRefStructure siteRefStructure = new SiteRefStructure();
                    siteRefStructure.setRef(parentStopPlace.getNetexId());
                    siteRefStructure.setVersion(String.valueOf(parentStopPlace.getVersion()));
                    child.setParentSiteRef(siteRefStructure);
                    return child;

            }).count();
        }
        logger.info("Updated {} childs with parent site refs", count);
    }

    private void initiateOrIncrementSiteElementVersion(SiteElement siteElement) {
        versionCreator.initiateOrIncrement(siteElement);
        versionCreator.initiateOrIncrementAccessibilityAssesmentVersion(siteElement);
        if (siteElement.getAlternativeNames() != null) {
            versionCreator.initiateOrIncrementAlternativeNamesVersion(siteElement.getAlternativeNames());
        }
    }
}
