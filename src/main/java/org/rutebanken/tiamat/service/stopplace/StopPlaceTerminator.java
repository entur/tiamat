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
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.rutebanken.tiamat.versioning.VersionCreator;
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

    @Autowired
    private VersionCreator versionCreator;

    public StopPlace terminateStopPlace(String stopPlaceId, Instant suggestedTimeOfTermination, String versionComment) {

        return mutateLock.executeInLock(() -> {

            Instant now = Instant.now();
            Instant timeOfTermination;

            if (suggestedTimeOfTermination.isBefore(now)) {
                logger.warn("Termination date {} cannot be before now {}. Setting now as time of termination for {}", suggestedTimeOfTermination, now, stopPlaceId);
                timeOfTermination = now;
            } else {
                timeOfTermination = suggestedTimeOfTermination;
            }

            logger.info("User {} is terminating stop {} at {} with comment '{}'", usernameFetcher.getUserNameForAuthenticatedUser(), stopPlaceId, timeOfTermination, versionComment);

            StopPlace previousStopPlaceVersion = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);

            if (previousStopPlaceVersion != null) {

                // Stop Place Saver service should always check that the user is authorized

                if (!previousStopPlaceVersion.isParentStopPlace() && previousStopPlaceVersion.getParentSiteRef() != null && previousStopPlaceVersion.getParentSiteRef().getRef() != null) {
                    throw new IllegalArgumentException("Cannot terminate child stop of multi modal stop place: " + stopPlaceId);
                }

                if (previousStopPlaceVersion.getValidBetween() != null && previousStopPlaceVersion.getValidBetween().getToDate() != null) {
                    throw new IllegalArgumentException("The stop place " + stopPlaceId + ", version " + previousStopPlaceVersion.getVersion() + " is already terminated at " + previousStopPlaceVersion.getValidBetween().getToDate());
                }

                StopPlace nextVersionStopPlace = versionCreator.createCopy(previousStopPlaceVersion, StopPlace.class);

                logger.debug("End previous version {} of stop place {} at {} (now)", previousStopPlaceVersion.getVersion(), previousStopPlaceVersion.getNetexId(), now);
                previousStopPlaceVersion.getValidBetween().setToDate(now);

                nextVersionStopPlace.setValidBetween(new ValidBetween(now, timeOfTermination));
                logger.debug("Set valid betwen to {} for new version of stop place {}", nextVersionStopPlace.getValidBetween(), nextVersionStopPlace.getNetexId());
                nextVersionStopPlace.setVersionComment(versionComment);

                return stopPlaceVersionedSaverService.saveNewVersion(previousStopPlaceVersion, nextVersionStopPlace, now);
            } else {
                throw new IllegalArgumentException("Cannot find stop place to terminate: " + stopPlaceId + ". No changes executed.");
            }
        });
    }
}
