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
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.MutateLock;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class StopPlaceTerminator {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceTerminator.class);

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private UsernameFetcher usernameFetcher;

    @Autowired
    private MutateLock mutateLock;

    public StopPlace terminateStopPlace(String stopPlaceId, Instant timeOfTermination, String versionComment) {

        return mutateLock.executeInLock(() -> {
            logger.info("User {} is terminating stop {} at {} with comment '{}'", usernameFetcher.getUserNameForAuthenticatedUser(), stopPlaceId, timeOfTermination, versionComment);

            StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);

            if (stopPlace != null) {

                // Stop Place Saver service should always check that the user is authorized

                if (!stopPlace.isParentStopPlace() && stopPlace.getParentSiteRef() != null && stopPlace.getParentSiteRef().getRef() != null) {
                    throw new IllegalArgumentException("Cannot terminate child stop of multi modal stop place: " + stopPlaceId);
                }

                if (stopPlace.getValidBetween() != null && stopPlace.getValidBetween().getToDate() != null) {
                    throw new IllegalArgumentException("The stop place " + stopPlaceId + ", version " + stopPlace.getVersion() + " is already terminated at " + stopPlace.getValidBetween().getToDate());
                }

                Instant now = Instant.now();

                if (timeOfTermination.isBefore(now)) {
                    throw new IllegalArgumentException("Termination date " + timeOfTermination + " cannot be before now " + now);
                }

                StopPlace nextVersionStopPlace = stopPlaceVersionedSaverService.createCopy(stopPlace, StopPlace.class);

                logger.debug("End previous version {} of stop place {} at {} (now)", stopPlace.getVersion(), stopPlace.getNetexId(), now);
                stopPlace.getValidBetween().setToDate(now);

                nextVersionStopPlace.setValidBetween(new ValidBetween(now, timeOfTermination));
                logger.debug("Set valid betwen to {} for new version of stop place {}", nextVersionStopPlace.getValidBetween(), nextVersionStopPlace.getNetexId());
                nextVersionStopPlace.setVersionComment(versionComment);

                return stopPlaceVersionedSaverService.saveNewVersion(stopPlace, nextVersionStopPlace, now);
            } else {
                throw new IllegalArgumentException("Cannot find stop place to terminate: " + stopPlaceId + ". No changes executed.");
            }
        });
    }
}
