package org.rutebanken.tiamat.rest.graphql;

import com.google.api.client.util.Preconditions;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.changelog.EntityChangedListener;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

@Component
public class StopPlaceQuayDeleter {

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private EntityChangedListener entityChangedListener;

    protected boolean deleteStopPlace(String stopPlaceId) {
        List<StopPlace> stopPlaces = getAllVersionsOfStopPlace(stopPlaceId);

        stopPlaceRepository.delete(stopPlaces);
        notifyDeleted(stopPlaces);
        return true;
    }

    public StopPlace deleteQuay(String stopPlaceId, String quayId) {
        StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);

        Preconditions.checkArgument(stopPlace != null, "Attempting to delete StopPlace [id = %s], but StopPlace does not exist.", stopPlaceId);

        Optional<Quay> optionalQuay = stopPlace.getQuays().stream().filter(quay -> quay.getNetexId().equals(quayId)).findFirst();
        Preconditions.checkArgument(optionalQuay.isPresent(), "Attempting to delete Quay [id = %s], but Quay does not exist on StopPlace [id = %s].", quayId, stopPlaceId);

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, stopPlace);

        StopPlace nextVersionStopPlace = stopPlaceVersionedSaverService.createCopy(stopPlace, StopPlace.class);

        nextVersionStopPlace.getQuays().removeIf(quay -> quay.getNetexId().equals(quayId));

        return stopPlaceVersionedSaverService.saveNewVersion(stopPlace, nextVersionStopPlace);
    }

    private List<StopPlace> getAllVersionsOfStopPlace(String stopPlaceId) {
        List<String> idList = new ArrayList<>();
        idList.add(stopPlaceId);

        List<StopPlace> stopPlaces = stopPlaceRepository.findAll(idList);

        Preconditions.checkArgument((stopPlaces != null && !stopPlaces.isEmpty()), "Attempting to fetch StopPlace [id = %s], but StopPlace does not exist.", stopPlaceId);

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, stopPlaces);
        return stopPlaces;
    }

    private void notifyDeleted(List<StopPlace> stopPlaces) {
        Collections.sort(stopPlaces,
                (o1, o2) -> Long.compare(o1.getVersion(), o2.getVersion()));
        StopPlace newest = stopPlaces.get(stopPlaces.size() - 1);
        entityChangedListener.onDelete(newest);
    }
}
