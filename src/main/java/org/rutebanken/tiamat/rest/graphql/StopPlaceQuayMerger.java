package org.rutebanken.tiamat.rest.graphql;

import com.google.api.client.util.Preconditions;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

@Transactional
@Component
public class StopPlaceQuayMerger {


    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private AuthorizationService authorizationService;



    protected StopPlace mergeStopPlaces(String fromStopPlaceId, String toStopPlaceId, String versionComment) {
        StopPlace fromStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(fromStopPlaceId);
        StopPlace toStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(toStopPlaceId);

        Preconditions.checkArgument(fromStopPlace != null, "Attempting merge from StopPlace [id = %s], but StopPlace does not exist.", fromStopPlaceId);
        Preconditions.checkArgument(toStopPlace != null, "Attempting merge to StopPlace [id = %s], but StopPlace does not exist.", toStopPlaceId);

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, fromStopPlace, toStopPlace);

        StopPlace fromStopPlaceToTerminate = stopPlaceVersionedSaverService.createCopy(fromStopPlace, StopPlace.class);

        //New version of merged StopPlace
        final StopPlace mergedStopPlace = stopPlaceVersionedSaverService.createCopy(toStopPlace, StopPlace.class);

        //Transfer quays
        fromStopPlaceToTerminate.getQuays().stream()
                .forEach(quay -> mergedStopPlace.getQuays().add(stopPlaceVersionedSaverService.createCopy(quay, Quay.class)));

        // Keep importedId
        mergedStopPlace.getOriginalIds().addAll(fromStopPlaceToTerminate.getOriginalIds());

        //Remove quays from from-StopPlace
        fromStopPlaceToTerminate.getQuays().clear();

        //Terminate validity of from-StopPlace
        terminateEntity(fromStopPlaceToTerminate);
        stopPlaceVersionedSaverService.saveNewVersion(fromStopPlace, fromStopPlaceToTerminate);

        mergedStopPlace.setVersionComment(versionComment);

        return stopPlaceVersionedSaverService.saveNewVersion(toStopPlace, mergedStopPlace);
    }

    protected StopPlace mergeQuays(String stopPlaceId, String fromQuayId, String toQuayId, String versionComment) {
        StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);
        Preconditions.checkArgument(stopPlace != null, "Attempting to quays from StopPlace [id = %s], but StopPlace does not exist.", stopPlaceId);

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, stopPlace);

        StopPlace updatedStopPlace = stopPlaceVersionedSaverService.createCopy(stopPlace, StopPlace.class);

        Optional<Quay> fromQuayOpt = updatedStopPlace.getQuays().stream().filter(quay -> quay.getNetexId().equals(fromQuayId)).findFirst();
        Optional<Quay> toQuayOpt = updatedStopPlace.getQuays().stream().filter(quay -> quay.getNetexId().equals(toQuayId)).findFirst();

        Preconditions.checkArgument(fromQuayOpt.isPresent(), "Quay does not exist on StopPlace", fromQuayId);
        Preconditions.checkArgument(toQuayOpt.isPresent(), "Quay does not exist on StopPlace", toQuayId);

        Quay fromQuay = fromQuayOpt.get();
        Quay toQuay = toQuayOpt.get();

        //Copy attributes to to-quay
        toQuay.getOriginalIds().addAll(fromQuay.getOriginalIds());

        updatedStopPlace.getQuays()
                .removeIf(quay -> quay.getNetexId().equals(fromQuayId));

        updatedStopPlace.setVersionComment(versionComment);

        //Save updated StopPlace
        updatedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace, updatedStopPlace);

        return updatedStopPlace;
    }

    private EntityInVersionStructure terminateEntity(EntityInVersionStructure entity) {
        // Terminate validity for "from"-stopPlace
        if (entity.getValidBetween()!=null) {
            entity.getValidBetween().setToDate(Instant.now());
        } else {
            entity.setValidBetween(new ValidBetween(null, Instant.now()));
        }
        return entity;
    }
}
