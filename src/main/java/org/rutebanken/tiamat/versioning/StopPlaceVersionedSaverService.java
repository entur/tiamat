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
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.rutebanken.tiamat.service.TariffZonesLookupService;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;
import org.rutebanken.tiamat.service.metrics.MetricsService;
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
    
    @Autowired
    private ReferenceResolver referenceResolver;

    @Override
    public EntityInVersionRepository<StopPlace> getRepository() {
        return stopPlaceRepository;
    }

    @Override
    public StopPlace saveNewVersion(StopPlace existingVersion, StopPlace newVersion, Instant defaultValidFrom) {

        super.validate(existingVersion, newVersion);

        if (newVersion.getParentSiteRef() != null && !newVersion.isParentStopPlace()) {
            throw new IllegalArgumentException("StopPlace " +
                    newVersion.getNetexId() +
                    " seems to be a child stop. Save the parent stop place instead: "
                    + newVersion.getParentSiteRef());
        }

        if(!newVersion.getTariffZones().isEmpty()) {
            for(TariffZoneRef tariffZoneRef : newVersion.getTariffZones()) {
                TariffZone tariffZone = referenceResolver.resolve(tariffZoneRef);
                if(tariffZone == null) {
                    throw new IllegalArgumentException("StopPlace refers to non existing tariff zone: " + tariffZoneRef);
                }
            }
        }

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(newVersion));

        Instant changed = Instant.now();

        logger.debug("Rearrange accessibility assessments for: {}", newVersion);
        accessibilityAssessmentOptimizer.optimizeAccessibilityAssessments(newVersion);

        Instant newVersionValidFrom = validityUpdater.updateValidBetween(existingVersion, newVersion, defaultValidFrom);

        if (existingVersion == null) {
            logger.debug("Existing version is not present, which means new entity. {}", newVersion);
            newVersion.setCreated(changed);
        } else {
            newVersion.setChanged(changed);
            logger.debug("About to terminate previous version for {},{}", existingVersion.getNetexId(), existingVersion.getVersion());
            StopPlace existingStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(existingVersion.getNetexId());
            authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(existingStopPlace));
            logger.debug("Found previous version {},{}. Terminating it.", existingStopPlace.getNetexId(), existingStopPlace.getVersion());
            validityUpdater.terminateVersion(existingStopPlace, newVersionValidFrom.minusMillis(MILLIS_BETWEEN_VERSIONS));
        }

        // Save latest version
        newVersion =  versionIncrementor.initiateOrIncrementVersions(newVersion);

        newVersion.setChangedBy(usernameFetcher.getUserNameForAuthenticatedUser());
        logger.info("StopPlace [{}], version {} changed by user [{}]. {}", newVersion.getNetexId(), newVersion.getVersion(), newVersion.getChangedBy(), newVersion.getValidBetween());

        countyAndMunicipalityLookupService.populateTopographicPlaceRelation(newVersion);
        tariffZonesLookupService.populateTariffZone(newVersion);
        clearUnwantedChildFields(newVersion);

        if(newVersion.getChildren() != null) {
            newVersion.getChildren().forEach(child -> child.setChanged(changed));
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
        metricsService.registerEntitySaved(newVersion.getClass());

        updateQuaysCache(newVersion);

        nearbyStopPlaceFinder.update(newVersion);
        newVersion.getChildren().forEach(nearbyStopPlaceFinder::update);
        entityChangedListener.onChange(newVersion);

        return newVersion;
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

            if(child.getName() != null
                    && stopPlaceToSave.getName() != null
                    && child.getName().getValue().equalsIgnoreCase(stopPlaceToSave.getName().getValue())
                    && (child.getName().getLang() == null || child.getName().getLang().equalsIgnoreCase(stopPlaceToSave.getName().getLang()))) {
                logger.info("Name of child {}: {} is equal to parent's name {}. Clearing it", child.getNetexId(), stopPlaceToSave.getName(), stopPlaceToSave.getNetexId());
                child.setName(null);
            }

            child.setValidBetween(null);
        });
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
                    child.setParentSiteRef(new SiteRefStructure(parentStopPlace.getNetexId(), String.valueOf(parentStopPlace.getVersion())));
                    return child;

            }).count();
        }
        logger.info("Updated {} childs with parent site refs", count);
    }


}
