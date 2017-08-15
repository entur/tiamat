package org.rutebanken.tiamat.service;

import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

@Transactional
@Component
public class MultiModalStopPlaceEditor {

    private static final Logger logger = LoggerFactory.getLogger(MultiModalStopPlaceEditor.class);

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private ReflectionAuthorizationService authorizationService;


    public StopPlace createMultiModalParentStopPlace(List<String> childStopPlaceIds, EmbeddableMultilingualString name) {

        // Fetch max versions of future child stop places
        List<StopPlace> futureChildStopPlaces = childStopPlaceIds.stream().map(id -> stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(id)).collect(toList());

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, futureChildStopPlaces);

        logger.info("Creating first version of parent stop place {}", name);
        final StopPlace parentStopPlace = new StopPlace(name);
        parentStopPlace.setParentStopPlace(true);

        Set<StopPlace> childCopies = futureChildStopPlaces.stream()
                .map(existingVersion -> {
                    if (existingVersion.getValidBetween() != null) {
                        if (existingVersion.getValidBetween().getFromDate() != null && existingVersion.getValidBetween().getFromDate().isAfter(Instant.now())) {
                            throw new RuntimeException("The stop place " + existingVersion.getNetexId() + " version " + existingVersion.getVersion() + " is not currently valid: from date = " + existingVersion.getValidBetween().getFromDate());
                        }
                        if (existingVersion.getValidBetween().getToDate() != null && existingVersion.getValidBetween().getToDate().isBefore(Instant.now())) {
                            throw new RuntimeException("The stop place " + existingVersion.getNetexId() + " version " + existingVersion.getVersion() + " is not currently valid: to date = " + existingVersion.getValidBetween().getToDate());
                        }
                    }

                    if(existingVersion.isParentStopPlace()) {
                        throw new IllegalArgumentException("The stop place " + existingVersion.getNetexId() + " version " + existingVersion.getVersion() + " is already a parent stop place");
                    }

                    if(existingVersion.getParentSiteRef().getRef() != null) {
                        throw new IllegalArgumentException("The stop place " + existingVersion.getNetexId() + " version " + existingVersion.getVersion() + " does already have parent site ref");
                    }

                    logger.info("Adding child stop place {} to new parent stop place {}", existingVersion, parentStopPlace);
                    // Create copy to get rid of database primary keys, preparing it to be versioned under parent stop place.
                    StopPlace stopPlaceCopy = stopPlaceVersionedSaverService.createCopy(existingVersion, StopPlace.class);
                    return stopPlaceCopy;
                })
                .collect(toSet());

        parentStopPlace.setChildren(childCopies);
        return stopPlaceVersionedSaverService.saveNewVersion(parentStopPlace);
    }

    public StopPlace addToMultiModalParentStopPlace(String parentStopPlaceId, List<String> childStopPlaceIds) {
        // What happens if you have a new version of the parent stop place?: Then the child stop place should be bumped as well
        StopPlace parentStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parentStopPlaceId);
        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(parentStopPlace));

        List<StopPlace> stopPlaces = stopPlaceRepository.findAll(childStopPlaceIds);
        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, stopPlaces);

        stopPlaces.forEach(stopPlace -> {
            SiteRefStructure siteRefStructure = new SiteRefStructure();
            siteRefStructure.setRef(parentStopPlace.getNetexId());
            stopPlace.setParentSiteRef(siteRefStructure);
        });

        return parentStopPlace;
    }

    public StopPlace removeFromMultiModalStopPlace(String parentStopPlaceId, List<String> childStopPlaceIds) {

        StopPlace parentStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parentStopPlaceId);
        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(parentStopPlace));

        List<StopPlace> stopPlaces = stopPlaceRepository.findAll(childStopPlaceIds);
        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, stopPlaces);

        stopPlaces.forEach(stopPlace -> {
            SiteRefStructure parentSiteRef = stopPlace.getParentSiteRef();
            if (parentSiteRef != null && parentSiteRef.getRef().equals(parentStopPlaceId)) {
                stopPlace.setParentSiteRef(null);
            }
        });

        return parentStopPlace;
    }

}
