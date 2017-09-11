package org.rutebanken.tiamat.service.stopplace;

import com.google.api.client.util.Preconditions;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.graphql.helpers.ObjectMerger;
import org.rutebanken.tiamat.service.merge.AlternativeNamesMerger;
import org.rutebanken.tiamat.service.merge.KeyValuesMerger;
import org.rutebanken.tiamat.service.merge.PlaceEquipmentMerger;
import org.rutebanken.tiamat.versioning.CopiedEntity;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.rutebanken.tiamat.versioning.util.StopPlaceCopyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.MERGED_ID_KEY;

@Service
public class StopPlaceMerger {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceQuayMerger.class);

    /**
     * Properties to ignore on merge.
     */
    public static final String[] IGNORE_PROPERTIES_ON_MERGE = {"keyValues", "placeEquipments", "accessibilityAssessment", "tariffZones", "alternativeNames"};

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private ReflectionAuthorizationService authorizationService;

    @Autowired
    private KeyValuesMerger keyValuesMerger;

    @Autowired
    private PlaceEquipmentMerger placeEquipmentMerger;

    @Autowired
    private AlternativeNamesMerger alternativeNamesMerger;

    @Autowired
    private StopPlaceCopyHelper stopPlaceCopyHelper;

    public StopPlace mergeStopPlaces(String fromStopPlaceId, String toStopPlaceId, String fromVersionComment, String toVersionComment, boolean isDryRun) {

        logger.info("About to merge stop place {} into stop place {} with from comment {} and to comment {} ", fromStopPlaceId, toStopPlaceId, fromVersionComment, toVersionComment);

        StopPlace fromStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(fromStopPlaceId);
        StopPlace toStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(toStopPlaceId);

        validateArguments(fromStopPlace, toStopPlace);

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(fromStopPlace, toStopPlace));

        StopPlace fromStopPlaceToTerminate = stopPlaceVersionedSaverService.createCopy(fromStopPlace, StopPlace.class);

        CopiedEntity<StopPlace> mergedStopPlaceCopy = stopPlaceCopyHelper.createCopies(toStopPlace);

        executeMerge(fromStopPlaceToTerminate, mergedStopPlaceCopy.getCopiedEntity(), fromVersionComment, toVersionComment, Optional.ofNullable(mergedStopPlaceCopy.getCopiedParent()));

        if (!isDryRun) {
            //Terminate validity of from-StopPlace
            terminateEntity(fromStopPlaceToTerminate);
            stopPlaceVersionedSaverService.saveNewVersion(fromStopPlace, fromStopPlaceToTerminate);


            if(mergedStopPlaceCopy.hasParent()) {
                logger.info("Saving parent stop place {}. Returning parent of child: {}", mergedStopPlaceCopy.getCopiedParent().getNetexId(), mergedStopPlaceCopy.getCopiedEntity().getNetexId());
                return stopPlaceVersionedSaverService.saveNewVersion(mergedStopPlaceCopy.getExistingParent(), mergedStopPlaceCopy.getCopiedParent());

            } else {
                return stopPlaceVersionedSaverService.saveNewVersion(mergedStopPlaceCopy.getExistingEntity(), mergedStopPlaceCopy.getCopiedEntity());
            }
        }
        return mergedStopPlaceCopy.getCopiedEntity();
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

        if(mergedStopPlaceParent.isPresent()) {
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
                .forEach(quay -> mergedStopPlace.getQuays().add(stopPlaceVersionedSaverService.createCopy(quay, Quay.class)));
    }

    private EntityInVersionStructure terminateEntity(EntityInVersionStructure entity) {
        // Terminate validity for "from"-stopPlace
        if (entity.getValidBetween() != null) {
            entity.getValidBetween().setToDate(Instant.now());
        } else {
            entity.setValidBetween(new ValidBetween(null, Instant.now()));
        }
        return entity;
    }
}
