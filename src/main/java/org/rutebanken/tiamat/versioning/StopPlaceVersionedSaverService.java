/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.versioning;

import org.rutebanken.tiamat.changelog.EntityChangedListener;
import org.rutebanken.tiamat.importer.finder.NearbyStopPlaceFinder;
import org.rutebanken.tiamat.importer.finder.StopPlaceByQuayOriginalIdFinder;
import org.rutebanken.tiamat.model.SiteElement;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
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
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;


@Transactional
@Service
public class StopPlaceVersionedSaverService extends VersionedSaverService<StopPlace> {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceVersionedSaverService.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private AccessibilityAssessmentOptimizer accessibilityAssessmentOptimizer;

    @Autowired
    private TopographicPlaceLookupService countyAndMunicipalityLookupService;

    @Autowired
    private TariffZonesLookupService tariffZonesLookupService;

    @Autowired
    private StopPlaceByQuayOriginalIdFinder stopPlaceByQuayOriginalIdFinder;

    @Autowired
    private NearbyStopPlaceFinder nearbyStopPlaceFinder;

    @Autowired
    private EntityChangedListener entityChangedListener;

    @Override
    public EntityInVersionRepository<StopPlace> getRepository() {
        return stopPlaceRepository;
    }

    @Override
    public StopPlace saveNewVersion(StopPlace existingVersion, StopPlace newVersion, Instant now) {

        super.validate(existingVersion, newVersion);

        if (newVersion.getParentSiteRef() != null && !newVersion.isParentStopPlace()) {
            throw new IllegalArgumentException("StopPlace " +
                    newVersion.getNetexId() +
                    " seems to be a child stop. Save the parent stop place instead: "
                    + newVersion.getParentSiteRef());
        }

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(newVersion));


        logger.debug("Rearrange accessibility assessments for: {}", newVersion);
        accessibilityAssessmentOptimizer.optimizeAccessibilityAssessments(newVersion);

        Instant newVersionValidFrom = validityUpdater.updateValidBetween(existingVersion, newVersion, now);

        if (existingVersion == null) {
            logger.debug("Existing version is not present, which means new entity. {}", newVersion);
            newVersion.setCreated(now);
        } else {
            newVersion.setChanged(now);
            logger.debug("About to terminate previous version for {},{}", existingVersion.getNetexId(), existingVersion.getVersion());
            StopPlace existingStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(existingVersion.getNetexId());
            authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(existingStopPlace));
            logger.debug("Found previous version {},{}. Terminating it.", existingStopPlace.getNetexId(), existingStopPlace.getVersion());
            validityUpdater.terminateVersion(existingStopPlace, newVersionValidFrom);
        }

        // Save latest version
        newVersion = initiateOrIncrementVersions(newVersion);

        newVersion.setChangedBy(usernameFetcher.getUserNameForAuthenticatedUser());
        logger.info("StopPlace [{}], version {} changed by user [{}]. {}", newVersion.getNetexId(), newVersion.getVersion(), newVersion.getChangedBy(), newVersion.getValidBetween());

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

        return newVersion;
    }

    /**
     * Increment versions for stop place with children.
     * The object must have their netexId set, or else they will get an initial version
     * @param stopPlace with quays and accessibility assessment
     * @param validFrom
     * @return modified StopPlace
     */
    public StopPlace initiateOrIncrementVersions(StopPlace stopPlace) {
        versionCreator.initiateOrIncrement(stopPlace);
        initiateOrIncrementVersionsForChildren(stopPlace);
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

            if(child.getName() != null && stopPlaceToSave.getName() != null && child.getName().equals(stopPlaceToSave.getName())) {
                logger.info("Name of child {}: {} is equal to parent's name {}. Clearing it", child.getNetexId(), child.getName(), stopPlaceToSave.getNetexId());
                child.setName(null);
            }

            child.setValidBetween(null);
            child.setTopographicPlace(null);
            child.getTariffZones().clear();
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
