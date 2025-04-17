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
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.ObjectMerger;
import org.rutebanken.tiamat.service.merge.AlternativeNamesMerger;
import org.rutebanken.tiamat.service.merge.KeyValuesMerger;
import org.rutebanken.tiamat.service.merge.PlaceEquipmentMerger;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.rutebanken.tiamat.versioning.util.CopiedEntity;
import org.rutebanken.tiamat.versioning.util.StopPlaceCopyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.MERGED_ID_KEY;
import static org.rutebanken.tiamat.service.stopplace.StopPlaceMerger.IGNORE_PROPERTIES_ON_MERGE;

@Transactional
@Component
public class StopPlaceQuayMerger {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceQuayMerger.class);

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private KeyValuesMerger keyValuesMerger;

    @Autowired
    private PlaceEquipmentMerger placeEquipmentMerger;

    @Autowired
    private AlternativeNamesMerger alternativeNamesMerger;

    @Autowired
    private UsernameFetcher usernameFetcher;

    @Autowired
    private StopPlaceCopyHelper stopPlaceCopyHelper;

    @Autowired
    private MutateLock mutateLock;

    public StopPlace mergeQuays(final String stopPlaceId, String fromQuayId, String toQuayId, String versionComment, boolean isDryRun) {

        return mutateLock.executeInLock(() -> {
            logger.info("{} is about to merge quays {} -> {} of stop place {}", usernameFetcher.getUserNameForAuthenticatedUser(), fromQuayId, toQuayId, stopPlaceId);

            final StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);
            Preconditions.checkArgument(stopPlace != null, "Attempting to quays from StopPlace [id = %s], but StopPlace does not exist.", stopPlaceId);

            authorizationService.verifyCanEditEntities( Arrays.asList(stopPlace));

            Preconditions.checkArgument(!stopPlace.isParentStopPlace(), "Cannot merge quays of parent StopPlace [id = %s].", stopPlaceId);

            Instant now = Instant.now();

            CopiedEntity<StopPlace> stopPlaceCopies = stopPlaceCopyHelper.createCopies(stopPlace);


            Optional<Quay> fromQuayOpt = stopPlaceCopies.getCopiedEntity().getQuays().stream().filter(quay -> quay.getNetexId().equals(fromQuayId)).findFirst();
            Optional<Quay> toQuayOpt = stopPlaceCopies.getCopiedEntity().getQuays().stream().filter(quay -> quay.getNetexId().equals(toQuayId)).findFirst();

            Preconditions.checkArgument(fromQuayOpt.isPresent(), "Quay does not exist on StopPlace", fromQuayId);
            Preconditions.checkArgument(toQuayOpt.isPresent(), "Quay does not exist on StopPlace", toQuayId);

            Quay fromQuay = fromQuayOpt.get();
            Quay toQuay = toQuayOpt.get();

            executeQuayMerge(fromQuay, toQuay);

            stopPlaceCopies.getCopiedEntity().getQuays()
                    .removeIf(quay -> quay.getNetexId().equals(fromQuayId));

            if (stopPlaceCopies.hasParent()) {
                stopPlaceCopies.getCopiedParent().setVersionComment(versionComment);
            } else {
                stopPlaceCopies.getCopiedEntity().setVersionComment(versionComment);
            }


            logger.info("Saving stop place after merging: {}", stopPlace.getNetexId());
            if (stopPlaceCopies.hasParent()) {
                if (!isDryRun) {
                    logger.info("Saving parent stop place {}. Returning parent of child: {}", stopPlaceCopies.getCopiedParent().getNetexId(), stopPlace.getNetexId());
                    return stopPlaceVersionedSaverService.saveNewVersion(stopPlaceCopies.getExistingParent(), stopPlaceCopies.getCopiedParent(), now);
                } else {
                    return stopPlaceCopies.getCopiedParent();
                }
            } else {
                if (!isDryRun) {
                    return stopPlaceVersionedSaverService.saveNewVersion(stopPlace, stopPlaceCopies.getCopiedEntity(), now);
                } else {
                    return stopPlaceCopies.getCopiedEntity();
                }
            }
        });
    }

    private void executeQuayMerge(Quay fromQuay, Quay toQuay) {
        ObjectMerger.copyPropertiesNotNull(fromQuay, toQuay, IGNORE_PROPERTIES_ON_MERGE);
        //Copy attributes to to-quay

        if (fromQuay.getKeyValues() != null) {
            keyValuesMerger.mergeKeyValues(fromQuay.getKeyValues(), toQuay.getKeyValues());
        }

        toQuay.getOrCreateValues(MERGED_ID_KEY).add(fromQuay.getNetexId());

        if (fromQuay.getPlaceEquipments() != null) {
            toQuay.setPlaceEquipments(placeEquipmentMerger.mergePlaceEquipments(fromQuay.getPlaceEquipments(), toQuay.getPlaceEquipments()));
        }

        if (fromQuay.getAlternativeNames() != null) {
            alternativeNamesMerger.mergeAlternativeNames(fromQuay.getAlternativeNames(), toQuay.getAlternativeNames());
        }

        if (fromQuay.getExternalLinks() != null) {
            toQuay.setExternalLinks(Stream.concat(toQuay.getExternalLinks().stream(), fromQuay.getExternalLinks().stream()).toList());
        }
    }
}
