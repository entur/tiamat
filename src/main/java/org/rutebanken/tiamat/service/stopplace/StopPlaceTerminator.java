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

import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.rutebanken.tiamat.versioning.ValidityUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class StopPlaceTerminator {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceQuayDeleter.class);

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private ValidityUpdater validityUpdater;

    @Autowired
    private UsernameFetcher usernameFetcher;

    public StopPlace terminateStopPlace(String stopPlaceId, Instant timeOfTermination, String versionComment) {

        logger.info("User {} is terminating stop {} at {} with comment {}", usernameFetcher, stopPlaceId, timeOfTermination, versionComment);

        StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);

        // Stop Place Saver service should always check that the user is authorized

        if(!stopPlace.isParentStopPlace() && stopPlace.getParentSiteRef() != null && stopPlace.getParentSiteRef().getRef() != null) {
            throw new IllegalArgumentException("Cannot terminate child stop of multi modal stop place: "+stopPlaceId);
        }

        if (stopPlace != null) {
            StopPlace nextVersionStopPlace = stopPlaceVersionedSaverService.createCopy(stopPlace, StopPlace.class);

            nextVersionStopPlace.setVersionComment(versionComment);

            validityUpdater.terminateVersion(nextVersionStopPlace, timeOfTermination);

            return stopPlaceVersionedSaverService.saveNewVersion(stopPlace, nextVersionStopPlace);
        } else {
            logger.warn("Cannot find stop place to terminate: {}. No changes executed.", stopPlaceId);
        }
        return stopPlace;
    }
}
