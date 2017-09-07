package org.rutebanken.tiamat.service;

import com.google.api.client.util.Preconditions;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
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

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private EntityChangedListener entityChangedListener;

    @Autowired
    private ReflectionAuthorizationService authorizationService;


    public boolean deleteStopPlace(String stopPlaceId) {

        logger.warn("About to delete stop place by ID {}", stopPlaceId);

        List<StopPlace> stopPlaces = getAllVersionsOfStopPlace(stopPlaceId);

        stopPlaceRepository.delete(stopPlaces);
        notifyDeleted(stopPlaces);

        logger.warn("All versions ({}) of stop place {} deleted", stopPlaces.size(), stopPlaceId);

        return true;
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
