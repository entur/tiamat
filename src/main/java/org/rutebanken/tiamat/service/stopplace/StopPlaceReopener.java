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
import org.rutebanken.tiamat.service.MutateLock;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StopPlaceReopener {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceReopener.class);

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private UsernameFetcher usernameFetcher;

    @Autowired
    private MutateLock mutateLock;

    public StopPlace reopenStopPlace(String stopPlaceId, String versionComment) {

        return mutateLock.executeInLock(() -> {
            logger.info("User {} is reopening stop place {} with comment {}", usernameFetcher.getUserNameForAuthenticatedUser(), stopPlaceId, versionComment);

            StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);

            if (stopPlace != null) {

                // TODO: Assert that version of stop place is not currently "open"

                StopPlace nextVersionStopPlace = stopPlaceVersionedSaverService.createCopy(stopPlace, StopPlace.class);
                nextVersionStopPlace.setVersionComment(versionComment);

                return stopPlaceVersionedSaverService.saveNewVersion(stopPlace, nextVersionStopPlace);
            }
            return stopPlace;
        });
    }

}
