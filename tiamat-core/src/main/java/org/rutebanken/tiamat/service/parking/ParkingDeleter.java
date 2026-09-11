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


package org.rutebanken.tiamat.service.parking;

import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.changelog.EntityChangedListener;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class ParkingDeleter {

    private static final Logger logger = LoggerFactory.getLogger(ParkingDeleter.class);

    private final EntityChangedListener entityChangedListener;

    private final AuthorizationService authorizationService;

    private final UsernameFetcher usernameFetcher;

    private ParkingRepository parkingRepository;

    @Autowired
    public ParkingDeleter(ParkingRepository parkingRepository,
                          EntityChangedListener entityChangedListener,
                          AuthorizationService authorizationService,
                          UsernameFetcher usernameFetcher) {
        this.parkingRepository = parkingRepository;
        this.entityChangedListener = entityChangedListener;
        this.authorizationService = authorizationService;
        this.usernameFetcher = usernameFetcher;
    }

    @Transactional
    public boolean deleteParking(String parkingId) {

        String usernameForAuthenticatedUser = usernameFetcher.getUserNameForAuthenticatedUser();
        logger.warn("About to delete parking by ID {}. User: {}", parkingId, usernameForAuthenticatedUser);

        List<Parking> parkings = parkingRepository.findByNetexId(parkingId);

        if(parkings.isEmpty()) {
            throw new IllegalArgumentException("Cannot find parking to delete from ID: " + parkingId);
        }

        // Permission is checked directly on the Parking entities, same as
        // ParkingUpdater does for edits. The auth framework's TiamatEntityResolver
        // resolves each Parking to its parent StopPlace internally (and falls back
        // to checking the parking itself if the parent no longer exists), so no
        // separate resolution is needed here.
        authorizationService.verifyCanEditEntities(parkings);

        parkingRepository.deleteAll(parkings);
        notifyDeleted(parkings);

        logger.warn("All versions ({}) of parking {} deleted by user {}", parkings.size(), parkingId, usernameForAuthenticatedUser);

        return true;
    }

    //This is to make sure entity is persisted before sending message
    @Transactional
    public void notifyDeleted(List<Parking> parkings) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
            @Override
            public void afterCommit(){
                entityChangedListener.onDelete(Collections.max(parkings, Comparator.comparing(c -> c.getVersion())));
            }
        });

    }
}
