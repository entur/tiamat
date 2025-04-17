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
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.ObjectMerger;
import org.rutebanken.tiamat.service.merge.AlternativeNamesMerger;
import org.rutebanken.tiamat.service.merge.KeyValuesMerger;
import org.rutebanken.tiamat.service.merge.PlaceEquipmentMerger;
import org.rutebanken.tiamat.versioning.ValidityUpdater;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.rutebanken.tiamat.versioning.util.CopiedEntity;
import org.rutebanken.tiamat.versioning.util.StopPlaceCopyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.MERGED_ID_KEY;
import static org.rutebanken.tiamat.versioning.save.DefaultVersionedSaverService.MILLIS_BETWEEN_VERSIONS;

@Service
public class StopPlaceMerger {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceMerger.class);

    /**
     * Properties to ignore on merge.
     */
    public static final String[] IGNORE_PROPERTIES_ON_MERGE = {"keyValues", "placeEquipments", "accessibilityAssessment", "tariffZones", "alternativeNames", "transportMode", "airSubmode", "busSubmode", "funicularSubmode", "metroSubmode", "tramSubmode", "telecabinSubmode", "railSubmode", "waterSubmode", "externalLinks"};

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
    private StopPlaceCopyHelper stopPlaceCopyHelper;

    @Autowired
    private ValidityUpdater validityUpdater;

    @Autowired
    private MutateLock mutateLock;

    @Autowired
    private VersionCreator versionCreator;


    public StopPlace mergeStopPlaces(String fromStopPlaceId, String toStopPlaceId, String fromVersionComment, String toVersionComment, boolean isDryRun) {

        return mutateLock.executeInLock(() -> {
            logger.info("About to merge stop place {} into stop place {} with from comment {} and to comment {} ", fromStopPlaceId, toStopPlaceId, fromVersionComment, toVersionComment);

            StopPlace fromStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(fromStopPlaceId);
            StopPlace toStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(toStopPlaceId);

            validateArguments(fromStopPlace, toStopPlace);

            authorizationService.verifyCanEditEntities( Arrays.asList(fromStopPlace, toStopPlace));

            StopPlace fromStopPlaceToTerminate = versionCreator.createCopy(fromStopPlace, StopPlace.class);

            CopiedEntity<StopPlace> mergedStopPlaceCopy = stopPlaceCopyHelper.createCopies(toStopPlace);

            executeMerge(fromStopPlaceToTerminate, mergedStopPlaceCopy.getCopiedEntity(), fromVersionComment, toVersionComment, Optional.ofNullable(mergedStopPlaceCopy.getCopiedParent()));

            if (!isDryRun) {
                //Terminate validity of from-StopPlace
                Instant newVersionFromDate = Instant.now();
                validityUpdater.terminateVersion(fromStopPlaceToTerminate, newVersionFromDate.minusMillis(MILLIS_BETWEEN_VERSIONS));

                stopPlaceVersionedSaverService.saveNewVersion(fromStopPlace, fromStopPlaceToTerminate, newVersionFromDate);


                if (mergedStopPlaceCopy.hasParent()) {
                    logger.info("Saving parent stop place {}. Returning parent of child: {}", mergedStopPlaceCopy.getCopiedParent().getNetexId(), mergedStopPlaceCopy.getCopiedEntity().getNetexId());
                    return stopPlaceVersionedSaverService.saveNewVersion(mergedStopPlaceCopy.getExistingParent(), mergedStopPlaceCopy.getCopiedParent(), newVersionFromDate);

                } else {
                    return stopPlaceVersionedSaverService.saveNewVersion(mergedStopPlaceCopy.getExistingEntity(), mergedStopPlaceCopy.getCopiedEntity(), newVersionFromDate);
                }
            }
            return mergedStopPlaceCopy.getCopiedEntity();
        });
    }

    private void validateArguments(StopPlace fromStopPlace, StopPlace toStopPlace) {
        Preconditions.checkArgument(fromStopPlace != null, "Attempting merge from StopPlace [id = %s], but StopPlace does not exist.", fromStopPlace.getNetexId());
        Preconditions.checkArgument(toStopPlace != null, "Attempting merge to StopPlace [id = %s], but StopPlace does not exist.", toStopPlace.getNetexId());
        Preconditions.checkArgument(!fromStopPlace.isParentStopPlace(), "Cannot merge parent stop places. From stop place: [id = %s].", fromStopPlace.getNetexId());
        Preconditions.checkArgument(!toStopPlace.isParentStopPlace(), "Cannot merge parent stop places. To stop place: [id = %s].", toStopPlace);
        Preconditions.checkArgument(!(fromStopPlace.getParentSiteRef() != null && fromStopPlace.getParentSiteRef().getRef() != null), "Cannot merge from childs of multi modal stop places [id = %s].", fromStopPlace.getNetexId());
    }

    private void executeMerge(StopPlace fromStopPlaceToTerminate, StopPlace mergedStopPlace, String fromVersionComment, String toVersionComment, Optional<StopPlace> mergedStopPlaceParent) {
        transferQuays(fromStopPlaceToTerminate, mergedStopPlace);
        removeQuaysFromFromStopPlace(fromStopPlaceToTerminate, fromVersionComment);

        ObjectMerger.copyPropertiesNotNull(fromStopPlaceToTerminate, mergedStopPlace, IGNORE_PROPERTIES_ON_MERGE);

        if (fromStopPlaceToTerminate.getKeyValues() != null) {
            keyValuesMerger.mergeKeyValues(fromStopPlaceToTerminate.getKeyValues(), mergedStopPlace.getKeyValues());
        }

        mergedStopPlace.getOrCreateValues(MERGED_ID_KEY).add(fromStopPlaceToTerminate.getNetexId());

        if (fromStopPlaceToTerminate.getPlaceEquipments() != null) {
            mergedStopPlace.setPlaceEquipments(
                    placeEquipmentMerger.mergePlaceEquipments(fromStopPlaceToTerminate.getPlaceEquipments(), mergedStopPlace.getPlaceEquipments())
            );
        }

        if (mergedStopPlaceParent.isPresent()) {
            // Set the version comment on the parent if it is present
            // Avoid setting tariff zones and alternative names, as we are merging to a child of parent.
            // Childs does not have names or tariff zones.

            mergedStopPlaceParent.get().setVersionComment(toVersionComment);
        } else {
            mergedStopPlace.setVersionComment(toVersionComment);

            if (fromStopPlaceToTerminate.getTariffZones() != null) {
                fromStopPlaceToTerminate.getTariffZones().forEach(tz -> {
                    TariffZoneRef tariffZoneRef = new TariffZoneRef();
                    ObjectMerger.copyPropertiesNotNull(tz, tariffZoneRef);
                    mergedStopPlace.getTariffZones().add(tariffZoneRef);
                });
            }

            if (fromStopPlaceToTerminate.getAlternativeNames() != null) {
                alternativeNamesMerger.mergeAlternativeNames(fromStopPlaceToTerminate.getAlternativeNames(), mergedStopPlace.getAlternativeNames());
            }
        }
    }

    private void removeQuaysFromFromStopPlace(StopPlace fromStopPlaceToTerminate, String fromVersionComment) {
        fromStopPlaceToTerminate.getQuays().clear();
        fromStopPlaceToTerminate.setVersionComment(fromVersionComment);
    }

    private void transferQuays(StopPlace fromStopPlaceToTerminate, StopPlace mergedStopPlace) {
        fromStopPlaceToTerminate.getQuays().stream()
                .forEach(quay -> mergedStopPlace.getQuays().add(versionCreator.createCopy(quay, Quay.class)));
    }

}
