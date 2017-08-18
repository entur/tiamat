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

        Set<StopPlace> childCopies = validateAndCopyPotentionalChildren(futureChildStopPlaces, parentStopPlace);

        parentStopPlace.getChildren().addAll(childCopies);
        return stopPlaceVersionedSaverService.saveNewVersion(parentStopPlace);
    }

    public StopPlace addToMultiModalParentStopPlace(String parentStopPlaceId, List<String> childStopPlaceIds) {
        StopPlace parentStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parentStopPlaceId);
        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(parentStopPlace));

        List<String> alreadyAdded = childStopPlaceIds
                .stream()
                .filter(child -> parentStopPlace.getChildren() != null
                        && parentStopPlace.getChildren().stream()
                            .anyMatch(existingChild -> child.equals(existingChild.getNetexId())))
                .collect(toList());

        if(!alreadyAdded.isEmpty()) {
            throw new IllegalArgumentException("Child stop place(s) " + alreadyAdded + " is already added to " + parentStopPlace);
        }

        StopPlace parentStopPlaceCopy = stopPlaceVersionedSaverService.createCopy(parentStopPlace, StopPlace.class);

        List<StopPlace> futureChildStopPlaces = childStopPlaceIds.stream().map(id -> stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(id)).collect(toList());
        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, futureChildStopPlaces);

        Set<StopPlace> childCopies = validateAndCopyPotentionalChildren(futureChildStopPlaces, parentStopPlace);

        parentStopPlaceCopy.getChildren().addAll(childCopies);
        return stopPlaceVersionedSaverService.saveNewVersion(parentStopPlace, parentStopPlaceCopy);
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

    private void validate(StopPlace potentialNewChild) {
        validateCurrentlyValid(potentialNewChild);
        validateNotParentStopPlace(potentialNewChild);
        validateNoParentSiteRef(potentialNewChild);
    }

    private Set<StopPlace> validateAndCopyPotentionalChildren(List<StopPlace> futureChildStopPlaces, StopPlace parentStopPlace) {
        return futureChildStopPlaces.stream()
                .map(existingVersion -> {
                    validate(existingVersion);

                    logger.info("Adding child stop place {} to parent stop place {}", existingVersion, parentStopPlace);
                    // Create copy to get rid of database primary keys, preparing it to be versioned under parent stop place.
                    StopPlace stopPlaceCopy = stopPlaceVersionedSaverService.createCopy(existingVersion, StopPlace.class);
                    return stopPlaceCopy;
                })
                .collect(toSet());
    }

    private void validateNoParentSiteRef(StopPlace potentialNewChild) {
        if(potentialNewChild.getParentSiteRef() != null && potentialNewChild.getParentSiteRef().getRef() != null) {
            throw new IllegalArgumentException("The stop place " + potentialNewChild.getNetexId() + " version " + potentialNewChild.getVersion() + " does already have parent site ref");
        }
    }

    private void validateNotParentStopPlace(StopPlace potentialNewChild) {
        if(potentialNewChild.isParentStopPlace()) {
            throw new IllegalArgumentException("The stop place " + potentialNewChild.getNetexId() + " version " + potentialNewChild.getVersion() + " is already a parent stop place");
        }
    }

    private void validateCurrentlyValid(StopPlace potentialNewChild) {
        if (potentialNewChild.getValidBetween() != null) {


            if (potentialNewChild.getValidBetween().getFromDate() != null && potentialNewChild.getValidBetween().getFromDate().isAfter(Instant.now())) {
                throw new RuntimeException("The stop place " + potentialNewChild.getNetexId()
                        + " version " + potentialNewChild.getVersion()
                        + " is not currently valid: from date = " + potentialNewChild.getValidBetween().getFromDate());
            }
            if (potentialNewChild.getValidBetween().getToDate() != null && potentialNewChild.getValidBetween().getToDate().isBefore(Instant.now())) {
                throw new RuntimeException("The stop place " + potentialNewChild.getNetexId()
                        + " version " + potentialNewChild.getVersion()
                        + " is not currently valid: to date = " + potentialNewChild.getValidBetween().getToDate());
            }
        }
    }
}
