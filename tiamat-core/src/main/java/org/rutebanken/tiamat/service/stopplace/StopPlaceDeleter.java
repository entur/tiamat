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

import com.google.api.client.util.Preconditions;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.changelog.EntityChangedListener;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class StopPlaceDeleter {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceDeleter.class);

    private final StopPlaceRepository stopPlaceRepository;

    private final EntityChangedListener entityChangedListener;

    private final AuthorizationService authorizationService;

    private final UsernameFetcher usernameFetcher;

    private final MutateLock mutateLock;

    @Autowired
    public StopPlaceDeleter(StopPlaceRepository stopPlaceRepository, EntityChangedListener entityChangedListener, AuthorizationService authorizationService, UsernameFetcher usernameFetcher, MutateLock mutateLock) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.entityChangedListener = entityChangedListener;
        this.authorizationService = authorizationService;
        this.usernameFetcher = usernameFetcher;
        this.mutateLock = mutateLock;
    }

    @Transactional
    public boolean deleteStopPlace(String stopPlaceId) {

        return mutateLock.executeInLock(() -> {
            String usernameForAuthenticatedUser = usernameFetcher.getUserNameForAuthenticatedUser();
            logger.warn("About to delete stop place by ID {}. User: {}", stopPlaceId, usernameForAuthenticatedUser);

            List<StopPlace> stopPlaces = getAllVersionsOfStopPlace(stopPlaceId);

            if (stopPlaces.stream().anyMatch(stopPlace -> stopPlace.isParentStopPlace() || stopPlace.getParentSiteRef() != null)) {
                throw new IllegalArgumentException("Deleting parent stop place or childs of parent stop place is not allowed: " + stopPlaceId);
            }

            authorizationService.verifyCanDeleteEntities(stopPlaces);
            stopPlaceRepository.deleteAll(stopPlaces);
            notifyDeleted(stopPlaces);

            logger.warn("All versions ({}) of stop place {} deleted by user {}", stopPlaces.size(), stopPlaceId, usernameForAuthenticatedUser);

            return true;
        });
    }

    private List<StopPlace> getAllVersionsOfStopPlace(String stopPlaceId) {
        List<String> idList = new ArrayList<>();
        idList.add(stopPlaceId);

        List<StopPlace> stopPlaces = stopPlaceRepository.findAll(idList);

        Preconditions.checkArgument((stopPlaces != null && !stopPlaces.isEmpty()), "Attempting to fetch StopPlace [id = %s], but StopPlace does not exist.", stopPlaceId);

        return stopPlaces;
    }
    //This is to make sure entity is persisted before sending message
    @Transactional
    public void notifyDeleted(List<StopPlace> stopPlaces) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
            public void afterCommit(){
                Collections.sort(stopPlaces, Comparator.comparingLong(EntityInVersionStructure::getVersion));
                StopPlace newest = stopPlaces.getLast();
                entityChangedListener.onDelete(newest);
            }
        });


    }
}
