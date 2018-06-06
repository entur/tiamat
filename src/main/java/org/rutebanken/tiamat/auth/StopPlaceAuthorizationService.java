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

import com.google.common.collect.Sets;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

/**
 * This authorization service is implemented for handling multi modal stops.
 */
@Service
public class StopPlaceAuthorizationService {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceAuthorizationService.class);

    private final ReflectionAuthorizationService authorizationService;

    @Autowired
    public StopPlaceAuthorizationService(ReflectionAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public void assertEditAuthorized(StopPlace existingVersion, StopPlace newVersion) {
        boolean accessToAllChildren;


        // In case of standard stop place, the user should as usual be authorized to edit
        if (newVersion.isParentStopPlace()) {
            // Only child stops that the user has access to should be provided with the new version
            // If the stop place already contains children the user does not have access to, the user does not have access to terminate the stop place.
            authorizationService.assertAuthorized(ROLE_EDIT_STOPS, newVersion.getChildren());

            if (existingVersion != null) {
                Set<String> existingChildrenIds = existingVersion.getChildren().stream().map(s -> s.getNetexId()).collect(Collectors.toSet());
                Set<String> newChildrenIds = newVersion.getChildren().stream().map(s -> s.getNetexId()).collect(Collectors.toSet());

                Sets.SetView<String> difference = Sets.difference(existingChildrenIds, newChildrenIds);

                if (!difference.isEmpty()) {
                    logger.info("Childrens differ: {}", difference);
                    accessToAllChildren = authorizationService.isAuthorized(ROLE_EDIT_STOPS, existingVersion.getChildren());
                    if (!accessToAllChildren) {
                        logger.info("Detected a situation where the user does not have access to all existing child stops {}. About to check new version's termination date: {}.",
                                existingChildrenIds,
                                newVersion.getValidBetween());
                        if (newVersion.getValidBetween() != null && newVersion.getValidBetween().getToDate() != null) {
                            throw new AccessDeniedException("The user does not have access to all child stops, and can therefore not set termination date for the parent stop place " + newVersion.getNetexId());
                        }
                    }
                }
            }
        } else {
            authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(newVersion));
        }

    }
}
