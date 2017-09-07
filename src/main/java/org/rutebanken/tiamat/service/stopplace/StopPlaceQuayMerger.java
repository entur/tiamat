package org.rutebanken.tiamat.service.stopplace;

import com.google.api.client.util.Preconditions;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.graphql.helpers.ObjectMerger;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.MERGED_ID_KEY;

@Transactional
@Component
public class StopPlaceQuayMerger {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceQuayMerger.class);

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private ReflectionAuthorizationService authorizationService;

    private static final String[] ignoreFields = {"keyValues", "placeEquipments", "accessibilityAssessment", "tariffZones", "alternativeNames"};

    public StopPlace mergeStopPlaces(String fromStopPlaceId, String toStopPlaceId, String fromVersionComment, String toVersionComment, boolean isDryRun) {

        logger.info("About to merge stop place {} into stop place {} with from comment {} and to comment {} ", fromStopPlaceId, toStopPlaceId, fromVersionComment, toVersionComment);

        StopPlace fromStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(fromStopPlaceId);
        StopPlace toStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(toStopPlaceId);

        Preconditions.checkArgument(fromStopPlace != null, "Attempting merge from StopPlace [id = %s], but StopPlace does not exist.", fromStopPlaceId);
        Preconditions.checkArgument(toStopPlace != null, "Attempting merge to StopPlace [id = %s], but StopPlace does not exist.", toStopPlaceId);

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

        ObjectMerger.copyPropertiesNotNull(fromStopPlaceToTerminate, mergedStopPlace, ignoreFields);

        mergedStopPlace.setVersionComment(toVersionComment);

        if (fromStopPlaceToTerminate.getKeyValues() != null) {
            mergeKeyValues(fromStopPlaceToTerminate.getKeyValues(), mergedStopPlace.getKeyValues());
        }

        mergedStopPlace.getOrCreateValues(MERGED_ID_KEY).add(fromStopPlaceToTerminate.getNetexId());

        if (fromStopPlaceToTerminate.getPlaceEquipments() != null) {
            mergedStopPlace.setPlaceEquipments(
                    mergePlaceEquipments(fromStopPlaceToTerminate.getPlaceEquipments(), mergedStopPlace.getPlaceEquipments())
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
            mergeAlternativeNames(fromStopPlaceToTerminate.getAlternativeNames(), mergedStopPlace.getAlternativeNames());
        }


        if (!isDryRun) {
            //Terminate validity of from-StopPlace
            terminateEntity(fromStopPlaceToTerminate);
            stopPlaceVersionedSaverService.saveNewVersion(fromStopPlace, fromStopPlaceToTerminate);
            return stopPlaceVersionedSaverService.saveNewVersion(toStopPlace, mergedStopPlace);
        }
        return mergedStopPlace;
    }

    public StopPlace mergeQuays(String stopPlaceId, String fromQuayId, String toQuayId, String versionComment, boolean isDryRun) {
        StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);
        Preconditions.checkArgument(stopPlace != null, "Attempting to quays from StopPlace [id = %s], but StopPlace does not exist.", stopPlaceId);

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(stopPlace));

        StopPlace updatedStopPlace = stopPlaceVersionedSaverService.createCopy(stopPlace, StopPlace.class);

        Optional<Quay> fromQuayOpt = updatedStopPlace.getQuays().stream().filter(quay -> quay.getNetexId().equals(fromQuayId)).findFirst();
        Optional<Quay> toQuayOpt = updatedStopPlace.getQuays().stream().filter(quay -> quay.getNetexId().equals(toQuayId)).findFirst();

        Preconditions.checkArgument(fromQuayOpt.isPresent(), "Quay does not exist on StopPlace", fromQuayId);
        Preconditions.checkArgument(toQuayOpt.isPresent(), "Quay does not exist on StopPlace", toQuayId);

        Quay fromQuay = fromQuayOpt.get();
        Quay toQuay = toQuayOpt.get();

        ObjectMerger.copyPropertiesNotNull(fromQuay, toQuay, ignoreFields);
        //Copy attributes to to-quay

        if (fromQuay.getKeyValues() != null) {
            mergeKeyValues(fromQuay.getKeyValues(), toQuay.getKeyValues());
        }

        toQuay.getOrCreateValues(MERGED_ID_KEY).add(fromQuay.getNetexId());

        if (fromQuay.getPlaceEquipments() != null) {
            toQuay.setPlaceEquipments(mergePlaceEquipments(fromQuay.getPlaceEquipments(), toQuay.getPlaceEquipments()));
        }

        if (fromQuay.getAlternativeNames() != null) {
            mergeAlternativeNames(fromQuay.getAlternativeNames(), toQuay.getAlternativeNames());
        }

        updatedStopPlace.getQuays()
                .removeIf(quay -> quay.getNetexId().equals(fromQuayId));

        updatedStopPlace.setVersionComment(versionComment);

        if (!isDryRun) {
            //Save updated StopPlace
            updatedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace, updatedStopPlace);
        }

        return updatedStopPlace;
    }

    public void mergeKeyValues(Map<String, Value> fromKeyValues, Map<String, Value> toKeyValues) {
        fromKeyValues.keySet()
                .forEach(key -> {
                    if (toKeyValues.containsKey(key)) {
                        toKeyValues.get(key).getItems().addAll(fromKeyValues.get(key).getItems());
                    } else {
                        Value value = fromKeyValues.get(key);

                        List<String> valueItems = new ArrayList<>();
                        valueItems.addAll(value.getItems());

                        toKeyValues.put(key, new Value(valueItems));
                    }
                });
    }

    public PlaceEquipment mergePlaceEquipments(PlaceEquipment fromPlaceEquipments, PlaceEquipment toPlaceEquipments) {
        if (fromPlaceEquipments != null) {
            if (toPlaceEquipments == null) {
                toPlaceEquipments = new PlaceEquipment();
            }
            List<InstalledEquipment_VersionStructure> fromInstalledEquipment = fromPlaceEquipments.getInstalledEquipment();
            List<InstalledEquipment_VersionStructure> toInstalledEquipment = toPlaceEquipments.getInstalledEquipment();
            if (fromInstalledEquipment != null) {
                fromInstalledEquipment.forEach(eq -> {
                    toInstalledEquipment.add(
                            stopPlaceVersionedSaverService.createCopy(eq, InstalledEquipment_VersionStructure.class)
                    );
                });
            }
        }
        return toPlaceEquipments;
    }

    void mergeAlternativeNames(List<AlternativeName> fromAlternativeNames, List<AlternativeName> toAlternativeNames) {
        if (fromAlternativeNames != null) {
            fromAlternativeNames.forEach(altName -> {
                AlternativeName mergedAltName = new AlternativeName();
                ObjectMerger.copyPropertiesNotNull(altName, mergedAltName);
                toAlternativeNames.add(mergedAltName);
            });
        }
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
