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
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.versioning.CopiedEntity;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.rutebanken.tiamat.versioning.util.StopPlaceCopyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

@Component
public class StopPlaceQuayDeleter {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceQuayDeleter.class);

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private ReflectionAuthorizationService authorizationService;

    @Autowired
    private UsernameFetcher usernameFetcher;

    @Autowired
    private StopPlaceCopyHelper stopPlaceCopyHelper;

    @Autowired
    private MutateLock mutateLock;

    public StopPlace deleteQuay(String stopPlaceId, String quayId, String versionComment) {

        return mutateLock.executeInLock(() -> {
            logger.warn("{} is deleting quay {} from stop place {} with comment {}", usernameFetcher.getUserNameForAuthenticatedUser(), quayId, stopPlaceId, versionComment);

            StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);

            Preconditions.checkArgument(stopPlace != null, "Attempting to delete StopPlace [id = %s], but StopPlace does not exist.", stopPlaceId);

            Preconditions.checkArgument(!stopPlace.isParentStopPlace(), "Cannot merge quays of parent stop place: [id = %s].", stopPlaceId);

            Optional<Quay> optionalQuay = stopPlace.getQuays().stream().filter(quay -> quay.getNetexId().equals(quayId)).findFirst();
            Preconditions.checkArgument(optionalQuay.isPresent(), "Attempting to delete Quay [id = %s], but Quay does not exist on StopPlace [id = %s].", quayId, stopPlaceId);

            authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(stopPlace));

            Instant instant = Instant.now();

            CopiedEntity<StopPlace> stopPlaceCopies = stopPlaceCopyHelper.createCopies(stopPlace);

            stopPlaceCopies.getCopiedEntity().getQuays().removeIf(quay -> quay.getNetexId().equals(quayId));

            if (stopPlaceCopies.hasParent()) {
                stopPlaceCopies.getCopiedParent().setVersionComment(versionComment);
            } else {
                stopPlaceCopies.getCopiedEntity().setVersionComment(versionComment);
            }


            if (stopPlaceCopies.hasParent()) {
                logger.info("Saving parent stop place {}. Returning parent of child: {}", stopPlaceCopies.getCopiedParent().getNetexId(), stopPlace.getNetexId());
                return stopPlaceVersionedSaverService.saveNewVersion(stopPlaceCopies.getExistingParent(), stopPlaceCopies.getCopiedParent(), instant);
            } else {
                return stopPlaceVersionedSaverService.saveNewVersion(stopPlaceCopies.getExistingEntity(), stopPlaceCopies.getCopiedEntity(), instant);
            }
        });
    }
}
