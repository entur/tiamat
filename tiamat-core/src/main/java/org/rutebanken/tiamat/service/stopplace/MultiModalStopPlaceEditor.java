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

package org.rutebanken.tiamat.service.stopplace;

import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.PostalAddress;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
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
import static org.rutebanken.tiamat.versioning.save.DefaultVersionedSaverService.MILLIS_BETWEEN_VERSIONS;

@Transactional
@Component
public class MultiModalStopPlaceEditor {

    private static final Logger logger = LoggerFactory.getLogger(MultiModalStopPlaceEditor.class);

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private MutateLock mutateLock;

    @Autowired
    private VersionCreator versionCreator;

    public StopPlace createMultiModalParentStopPlace(List<String> childStopPlaceIds, EmbeddableMultilingualString name) {
        return createMultiModalParentStopPlace(childStopPlaceIds, name, null, null, null, null, null);
    }

    public StopPlace createMultiModalParentStopPlace(List<String> childStopPlaceIds, EmbeddableMultilingualString name, ValidBetween validBetween, String versionComment, Point geoJsonPoint, PostalAddress postalAddress, String url) {

        return mutateLock.executeInLock(() -> {

            logger.info("Create parent stop place with name {} and child stop place {}", name, childStopPlaceIds);

            verifyChildrenIdsNotNullOrEmpty(childStopPlaceIds);

            Instant fromDate = resolveFromDateOrNow(validBetween);

            // Fetch max versions of future child stop places
            List<StopPlace> futureChildStopPlaces = childStopPlaceIds.stream().map(id -> stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(id)).collect(toList());

            authorizationService.verifyCanEditEntities( futureChildStopPlaces);

            logger.info("Creating first version of parent stop place {}", name);
            final StopPlace parentStopPlace = new StopPlace(name);
            parentStopPlace.setParentStopPlace(true);
            parentStopPlace.setValidBetween(validBetween);
            parentStopPlace.setVersionComment(versionComment);
            parentStopPlace.setCentroid(geoJsonPoint);
            parentStopPlace.setPostalAddress(postalAddress);
            parentStopPlace.setUrl(url);

            Set<StopPlace> childCopies = validateAndCopyPotentionalChildren(futureChildStopPlaces, parentStopPlace, fromDate);
            parentStopPlace.getChildren().addAll(childCopies);

            Instant terminationDate = fromDate.minusMillis(MILLIS_BETWEEN_VERSIONS);

            terminatePreviousVersionsOfChildren(futureChildStopPlaces, terminationDate);
            stopPlaceRepository.saveAll(futureChildStopPlaces);

            return stopPlaceVersionedSaverService.saveNewVersion(null, parentStopPlace, fromDate);
        });
    }

    public StopPlace addToMultiModalParentStopPlace(String parentStopPlaceId, List<String> childStopPlaceIds) {
        return addToMultiModalParentStopPlace(parentStopPlaceId, childStopPlaceIds, null, null);
    }

    public StopPlace addToMultiModalParentStopPlace(String parentStopPlaceId, List<String> childStopPlaceIds, ValidBetween validBetween, String versionComment) {

        return mutateLock.executeInLock(() -> {

            logger.info("Add childs: {} to parent stop place {}", childStopPlaceIds, parentStopPlaceId);

            verifyChildrenIdsNotNullOrEmpty(childStopPlaceIds);

            Instant fromDate = resolveFromDateOrNow(validBetween);

            StopPlace parentStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parentStopPlaceId);

            if (parentStopPlace == null) {
                throw new IllegalArgumentException("Cannot fetch parent stop place from ID: " + parentStopPlaceId);
            }

            authorizationService.verifyCanEditEntities( Arrays.asList(parentStopPlace));

            List<String> alreadyAdded = childStopPlaceIds
                    .stream()
                    .filter(child -> parentStopPlace.getChildren() != null
                            && parentStopPlace.getChildren().stream()
                            .anyMatch(existingChild -> child.equals(existingChild.getNetexId())))
                    .collect(toList());

            if (!alreadyAdded.isEmpty()) {
                throw new IllegalArgumentException("Child stop place(s) " + alreadyAdded + " is already added to " + parentStopPlace);
            }

            StopPlace parentStopPlaceCopy = versionCreator.createCopy(parentStopPlace, StopPlace.class);

            parentStopPlaceCopy.setValidBetween(validBetween);
            parentStopPlaceCopy.setVersionComment(versionComment);

            List<StopPlace> futureChildStopPlaces = childStopPlaceIds.stream().map(id -> stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(id)).collect(toList());
            authorizationService.verifyCanEditEntities( futureChildStopPlaces);

            Set<StopPlace> childCopies = validateAndCopyPotentionalChildren(futureChildStopPlaces, parentStopPlace, fromDate);
            Instant terminationDate = fromDate.minusMillis(MILLIS_BETWEEN_VERSIONS);
            terminatePreviousVersionsOfChildren(futureChildStopPlaces, terminationDate);
            stopPlaceRepository.saveAll(futureChildStopPlaces);

            parentStopPlaceCopy.getChildren().addAll(childCopies);
            return stopPlaceVersionedSaverService.saveNewVersion(parentStopPlace, parentStopPlaceCopy, fromDate);
        });
    }

    public StopPlace removeFromMultiModalStopPlace(String parentStopPlaceId, List<String> childStopPlaceIds) {

        return mutateLock.executeInLock(() -> {

            logger.info("Remove childs: {} from parent stop place {}", childStopPlaceIds, parentStopPlaceId);

            StopPlace parentStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parentStopPlaceId);
            authorizationService.verifyCanEditEntities( Arrays.asList(parentStopPlace));

            if (parentStopPlace.getChildren().stream().noneMatch(stopPlace -> childStopPlaceIds.contains(stopPlace.getNetexId()))) {
                throw new IllegalArgumentException("The specified list of IDs does not match the list of parent stop place's children" + parentStopPlaceId + ". Incoming child IDs: " + childStopPlaceIds);
            }

            authorizationService.verifyCanEditEntities( parentStopPlace.getChildren());

            Instant now = Instant.now();

            StopPlace parentStopPlaceCopy = versionCreator.createCopy(parentStopPlace, StopPlace.class);

            parentStopPlaceCopy.getChildren().forEach(stopToRemove -> {
                if (childStopPlaceIds.contains(stopToRemove.getNetexId())) {
                    logger.info("Removing child stop place {} from parent stop place {}", stopToRemove.getNetexId(), parentStopPlace.getNetexId());
                    StopPlace stopToRemoveCopy = versionCreator.createCopy(stopToRemove, StopPlace.class);
                    stopToRemoveCopy.setParentSiteRef(null);

                    if (stopToRemoveCopy.getName() == null) {
                        logger.info("Setting name for removed child to parent's name: {} ({})", parentStopPlace.getName(), stopToRemoveCopy.getNetexId());
                        stopToRemoveCopy.setName(parentStopPlace.getName());
                    }

                    stopPlaceVersionedSaverService.saveNewVersion(stopToRemove, stopToRemoveCopy, now);
                }
            });

            parentStopPlaceCopy.getChildren().removeIf(childStopPlace -> childStopPlaceIds.contains(childStopPlace.getNetexId()));

            return stopPlaceVersionedSaverService.saveNewVersion(parentStopPlace, parentStopPlaceCopy, now);
        });
    }

    private void verifyChildrenIdsNotNullOrEmpty(List<String> childStopPlaceIds) {
        if(childStopPlaceIds == null || childStopPlaceIds.isEmpty()) {
            throw new IllegalArgumentException("The list of child stop place IDs cannot be empty.");
        }
    }

    private void validate(StopPlace potentialNewChild, Instant fromDate) {
        validateCurrentlyValid(potentialNewChild, fromDate);
        validateNotParentStopPlace(potentialNewChild);
        validateNoParentSiteRef(potentialNewChild);
    }

    private Set<StopPlace> validateAndCopyPotentionalChildren(List<StopPlace> futureChildStopPlaces, StopPlace parentStopPlace, Instant fromDate) {
        return futureChildStopPlaces.stream()
                .map(existingVersion -> {
                    validate(existingVersion, fromDate);

                    logger.info("Adding child stop place {} to parent stop place {}", existingVersion, parentStopPlace);
                    // Create copy to get rid of database primary keys, preparing it to be versioned under parent stop place.
                    StopPlace stopPlaceCopy = versionCreator.createCopy(existingVersion, StopPlace.class);
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

    private void validateCurrentlyValid(StopPlace potentialNewChild, Instant fromDate) {
        if (potentialNewChild.getValidBetween() != null) {


            if (potentialNewChild.getValidBetween().getFromDate() != null && potentialNewChild.getValidBetween().getFromDate().isAfter(fromDate)) {
                throw new RuntimeException("The potential child stop place " + potentialNewChild.getNetexId()
                        + " version " + potentialNewChild.getVersion()
                        + " is not currently valid: from date = " + potentialNewChild.getValidBetween().getFromDate()
                        + " expected to be after "+fromDate);
            }
            if (potentialNewChild.getValidBetween().getToDate() != null && potentialNewChild.getValidBetween().getToDate().isBefore(fromDate)) {
                throw new RuntimeException("The stop place " + potentialNewChild.getNetexId()
                        + " version " + potentialNewChild.getVersion()
                        + " is not currently valid: to date = " + potentialNewChild.getValidBetween().getToDate());
            }
        }
    }

    private void terminatePreviousVersionsOfChildren(List<StopPlace> childStopPlaces, Instant terminationDate) {
        childStopPlaces.forEach(futureChildStopPlace -> {
            if(futureChildStopPlace.getValidBetween() == null) {
                futureChildStopPlace.setValidBetween(new ValidBetween(terminationDate.minusSeconds(1), terminationDate));
            } else {
                futureChildStopPlace.getValidBetween().setToDate(terminationDate);
            }
        });
    }

    private Instant resolveFromDateOrNow(ValidBetween validBetween) {

        if(validBetween != null && validBetween.getFromDate() != null) {
            return validBetween.getFromDate();
        } else {
            return Instant.now();
        }
    }
}
