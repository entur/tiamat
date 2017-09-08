package org.rutebanken.tiamat.service.stopplace;

import com.google.api.client.util.Preconditions;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.graphql.helpers.ObjectMerger;
import org.rutebanken.tiamat.service.merge.AlternativeNamesMerger;
import org.rutebanken.tiamat.service.merge.KeyValuesMerger;
import org.rutebanken.tiamat.service.merge.PlaceEquipmentMerger;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;

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


    public StopPlace mergeStopPlaces(String fromStopPlaceId, String toStopPlaceId, String fromVersionComment, String toVersionComment, boolean isDryRun) {

        logger.info("About to merge stop place {} into stop place {} with from comment {} and to comment {} ", fromStopPlaceId, toStopPlaceId, fromVersionComment, toVersionComment);

        StopPlace fromStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(fromStopPlaceId);
        StopPlace toStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(toStopPlaceId);

        Preconditions.checkArgument(fromStopPlace != null, "Attempting merge from StopPlace [id = %s], but StopPlace does not exist.", fromStopPlaceId);
        Preconditions.checkArgument(toStopPlace != null, "Attempting merge to StopPlace [id = %s], but StopPlace does not exist.", toStopPlaceId);

        Preconditions.checkArgument(!fromStopPlace.isParentStopPlace(), "Cannot merge parent stop places. From stop place: [id = %s].", fromStopPlaceId);
        Preconditions.checkArgument(!toStopPlace.isParentStopPlace(), "Cannot merge parent stop places. To stop place: [id = %s].", toStopPlace);

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(fromStopPlace, toStopPlace));

        StopPlace fromStopPlaceToTerminate = stopPlaceVersionedSaverService.createCopy(fromStopPlace, StopPlace.class);

        //New version of merged StopPlace
        final StopPlace mergedStopPlace = stopPlaceVersionedSaverService.createCopy(toStopPlace, StopPlace.class);

        //Transfer quays
        fromStopPlaceToTerminate.getQuays().stream()
                .forEach(quay -> mergedStopPlace.getQuays().add(stopPlaceVersionedSaverService.createCopy(quay, Quay.class)));

        //Remove quays from from-StopPlace
        fromStopPlaceToTerminate.getQuays().clear();
        fromStopPlaceToTerminate.setVersionComment(fromVersionComment);

        ObjectMerger.copyPropertiesNotNull(fromStopPlaceToTerminate, mergedStopPlace, IGNORE_PROPERTIES_ON_MERGE);

        mergedStopPlace.setVersionComment(toVersionComment);

        if (fromStopPlaceToTerminate.getKeyValues() != null) {
            keyValuesMerger.mergeKeyValues(fromStopPlaceToTerminate.getKeyValues(), mergedStopPlace.getKeyValues());
        }

        mergedStopPlace.getOrCreateValues(MERGED_ID_KEY).add(fromStopPlaceToTerminate.getNetexId());

        if (fromStopPlaceToTerminate.getPlaceEquipments() != null) {
            mergedStopPlace.setPlaceEquipments(
                    placeEquipmentMerger.mergePlaceEquipments(fromStopPlaceToTerminate.getPlaceEquipments(), mergedStopPlace.getPlaceEquipments())
            );
        }


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


        if (!isDryRun) {
            //Terminate validity of from-StopPlace
            terminateEntity(fromStopPlaceToTerminate);
            stopPlaceVersionedSaverService.saveNewVersion(fromStopPlace, fromStopPlaceToTerminate);
            return stopPlaceVersionedSaverService.saveNewVersion(toStopPlace, mergedStopPlace);
        }
        return mergedStopPlace;
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
