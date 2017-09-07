package org.rutebanken.tiamat.service.stopplace;

import com.google.api.client.util.Preconditions;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.changelog.EntityChangedListener;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

@Service
public class StopPlaceDeleter {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceDeleter.class);

    private final StopPlaceRepository stopPlaceRepository;

    private final EntityChangedListener entityChangedListener;

    private final ReflectionAuthorizationService authorizationService;

    private final UsernameFetcher usernameFetcher;

    @Autowired
    public StopPlaceDeleter(StopPlaceRepository stopPlaceRepository, EntityChangedListener entityChangedListener, ReflectionAuthorizationService authorizationService, UsernameFetcher usernameFetcher) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.entityChangedListener = entityChangedListener;
        this.authorizationService = authorizationService;
        this.usernameFetcher = usernameFetcher;
    }

    public boolean deleteStopPlace(String stopPlaceId) {

        String usernameForAuthenticatedUser = usernameFetcher.getUserNameForAuthenticatedUser();
        logger.warn("About to delete stop place by ID {}. User: {}", stopPlaceId, usernameForAuthenticatedUser);

        List<StopPlace> stopPlaces = getAllVersionsOfStopPlace(stopPlaceId);

        if (stopPlaces.stream().anyMatch(stopPlace -> stopPlace.isParentStopPlace())) {
            throw new IllegalArgumentException("Deleting parent stop place is not accepted: " + stopPlaceId);
        }

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, stopPlaces);
        stopPlaceRepository.delete(stopPlaces);
        notifyDeleted(stopPlaces);

        logger.warn("All versions ({}) of stop place {} deleted by user {}", stopPlaces.size(), stopPlaceId, usernameForAuthenticatedUser);

        return true;
    }

    private List<StopPlace> getAllVersionsOfStopPlace(String stopPlaceId) {
        List<String> idList = new ArrayList<>();
        idList.add(stopPlaceId);

        List<StopPlace> stopPlaces = stopPlaceRepository.findAll(idList);

        Preconditions.checkArgument((stopPlaces != null && !stopPlaces.isEmpty()), "Attempting to fetch StopPlace [id = %s], but StopPlace does not exist.", stopPlaceId);

        return stopPlaces;
    }

    private void notifyDeleted(List<StopPlace> stopPlaces) {
        Collections.sort(stopPlaces,
                (o1, o2) -> Long.compare(o1.getVersion(), o2.getVersion()));
        StopPlace newest = stopPlaces.get(stopPlaces.size() - 1);
        entityChangedListener.onDelete(newest);
    }
}
