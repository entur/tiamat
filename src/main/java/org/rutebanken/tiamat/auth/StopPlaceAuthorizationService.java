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

package org.rutebanken.tiamat.auth;

import org.rutebanken.tiamat.diff.TiamatObjectDiffer;
import org.rutebanken.tiamat.diff.generic.Difference;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This authorization service is implemented mainly for handling multi modal stops.
 * Generic authorization logic should be implemented in and handled by {@link AuthorizationService}.
 */
@Service
public class StopPlaceAuthorizationService {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceAuthorizationService.class);

    private final AuthorizationService authorizationService;

    private final TiamatObjectDiffer tiamatObjectDiffer;

    @Autowired
    public StopPlaceAuthorizationService(AuthorizationService authorizationService, TiamatObjectDiffer tiamatObjectDiffer) {
        this.authorizationService = authorizationService;
        this.tiamatObjectDiffer = tiamatObjectDiffer;
    }

    public void assertAuthorizedToEdit(StopPlace existingVersion, StopPlace newVersion) {
        assertAuthorizedToEdit(existingVersion, newVersion, new HashSet<String>());
    }

    /**
     * Assert that the user is authorized to edit the stop place.
     * <p>
     * If the stop place is a parent stop place, the following will be checked:
     * If the user is not authorized to edit all childs stops, it can still edit a child stop the user is authorized to edit, but it cannot terminate the validity of the parent stop.
     * In this situation, the newVersion of the stop must only be populated with the children that are relevant to edit.
     * If the newVersion of the stop place contains all current children, and the user does not have authorization to edit those stop places, authorization is not granted.
     * <p>
     * If the stop place is a normal mono modal stop place, the {@link AuthorizationService} will be called directly.
     *  @param existingVersion the current version of the stop place, persisted
     * @param newVersion      the new version of the same stop place, containing the changed state. If type is parent stop place, only child stops that the user would be authorized to change and edit, should be present.
     * @param childStopsUpdated
     */
    public void assertAuthorizedToEdit(StopPlace existingVersion, StopPlace newVersion, Set<String> childStopsUpdated) {

        if (newVersion.isParentStopPlace() && existingVersion != null) {
            // Only child stops that the user has access to should be provided with the new version
            // If the stop place already contains children the user does not have access to, the user does not have access to terminate the stop place.

            boolean accessToAllChildren = authorizationService.canEditEntities(existingVersion.getChildren());
            if (!accessToAllChildren) {
                // This user does not have access to all children.
                // Could the user still be allowed to edit a child?

                Set<StopPlace> mustBeAuthorizedToEditTheseChildren = newVersion.getChildren().stream()
                        .filter(newVersionOfChild -> childStopsUpdated.contains(newVersionOfChild.getNetexId()))
                        .collect(Collectors.toSet());

                if (mustBeAuthorizedToEditTheseChildren.isEmpty()) {
                    // Cannot accept empty list.
                    mustBeAuthorizedToEditTheseChildren = newVersion.getChildren();
                }

                logger.debug("Must be authorized to edit these children: {}", mustBeAuthorizedToEditTheseChildren.stream().map(child -> child.getNetexId()).toList());
                authorizationService.verifyCanEditEntities( mustBeAuthorizedToEditTheseChildren);

                Set<String> existingChildrenIds = existingVersion.getChildren().stream().map(s -> s.getNetexId()).collect(Collectors.toSet());

                if (newVersion.getValidBetween() != null && newVersion.getValidBetween().getToDate() != null) {
                    throw new AccessDeniedException("The user does not have access to all child stops, and can therefore not set termination date for the parent stop place " + newVersion.getNetexId());
                }

                logger.info("Detected a situation where the user does not have access to all existing child stops {} of parent stop {}. Access is still granted because the user has access to the following child stops: {}.",
                        existingChildrenIds, newVersion.getNetexId(), mustBeAuthorizedToEditTheseChildren);
            }
        } else {
            authorizationService.verifyCanEditEntities( Arrays.asList(newVersion));
        }

    }

    private boolean compareChild(StopPlace newVersionOfChild, StopPlace existingVersion) {
        if (existingVersion == null) {
            return true;
        }
        Optional<StopPlace> matchingExistingChild = existingVersion.getChildren().stream().filter(existingChild -> newVersionOfChild.getNetexId().equals(existingChild.getNetexId())).findFirst();

        if (matchingExistingChild.isPresent()) {
            return stopShouldBeAuthorized(newVersionOfChild, matchingExistingChild.get());
        }
        logger.info("Cannot find matching existing child {}. Return true.", newVersionOfChild.getNetexId());
        return true;
    }

    private boolean stopShouldBeAuthorized(StopPlace newVersionOfChild, StopPlace matchingExistingChild) {
        try {
            List<Difference> differenceList = tiamatObjectDiffer.compareObjects(matchingExistingChild, newVersionOfChild);

            if (differenceList.isEmpty()) {
                logger.info("Child has NOT changed {} {}", newVersionOfChild.getNetexId(), newVersionOfChild.getStopPlaceType());
                // Disable authorization check for this stop. It has not been changed.
                return false;
            } else {
                logger.info("Child has changed {} {}", newVersionOfChild.getNetexId(), newVersionOfChild.getStopPlaceType());
                return true;
            }
        } catch (IllegalAccessException e) {
            logger.warn("Could not compare children", e);
            return true;
        }
    }


}
