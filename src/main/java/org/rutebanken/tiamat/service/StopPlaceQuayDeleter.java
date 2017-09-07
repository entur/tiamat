package org.rutebanken.tiamat.service;

import com.google.api.client.util.Preconditions;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.changelog.EntityChangedListener;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.rutebanken.tiamat.versioning.ValidityUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

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


    public StopPlace deleteQuay(String stopPlaceId, String quayId, String versionComment) {
        StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);

        Preconditions.checkArgument(stopPlace != null, "Attempting to delete StopPlace [id = %s], but StopPlace does not exist.", stopPlaceId);

        Optional<Quay> optionalQuay = stopPlace.getQuays().stream().filter(quay -> quay.getNetexId().equals(quayId)).findFirst();
        Preconditions.checkArgument(optionalQuay.isPresent(), "Attempting to delete Quay [id = %s], but Quay does not exist on StopPlace [id = %s].", quayId, stopPlaceId);

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(stopPlace));

        StopPlace nextVersionStopPlace = stopPlaceVersionedSaverService.createCopy(stopPlace, StopPlace.class);

        nextVersionStopPlace.getQuays().removeIf(quay -> quay.getNetexId().equals(quayId));

        nextVersionStopPlace.setVersionComment(versionComment);

        return stopPlaceVersionedSaverService.saveNewVersion(stopPlace, nextVersionStopPlace);
    }
}
