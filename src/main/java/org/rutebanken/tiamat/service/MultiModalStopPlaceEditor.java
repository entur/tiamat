package org.rutebanken.tiamat.service;

import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

@Transactional
@Component
public class MultiModalStopPlaceEditor {


    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private ReflectionAuthorizationService authorizationService;


    public StopPlace createMultiModalParentStopPlace(List<String> childStopPlaceIds, EmbeddableMultilingualString name) {
        List<StopPlace> stopPlaces = stopPlaceRepository.findAll(childStopPlaceIds);

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, stopPlaces);

        StopPlace parentStopPlace = stopPlaceVersionedSaverService.saveNewVersion(new StopPlace(name));

        // TODO: Do not change all child versions
        // Terminate last version of stop place
        // Version ref must be decided. Should it point to a certain version?
        // What happens if you have a new version of the parent stop place?: Then the child stop place should be bumped as well
        stopPlaces.forEach(stopPlace -> {
            SiteRefStructure siteRefStructure = new SiteRefStructure();
            siteRefStructure.setRef(parentStopPlace.getNetexId());
            siteRefStructure.setVersion(String.valueOf(parentStopPlace.getVersion()));
            stopPlace.setParentSiteRef(siteRefStructure);
        });
        return parentStopPlace;
    }

    public StopPlace addToMultiModalParentStopPlace(String parentStopPlaceId, List<String> childStopPlaceIds) {

        StopPlace parentStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parentStopPlaceId);
        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(parentStopPlace));

        List<StopPlace> stopPlaces = stopPlaceRepository.findAll(childStopPlaceIds);
        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, stopPlaces);

        stopPlaces.forEach(stopPlace -> {
            SiteRefStructure siteRefStructure = new SiteRefStructure();
            siteRefStructure.setRef(parentStopPlace.getNetexId());
            stopPlace.setParentSiteRef(siteRefStructure);
        });

        return parentStopPlace;
    }

    public StopPlace removeFromMultiModalStopPlace(String parentStopPlaceId, List<String> childStopPlaceIds) {

        StopPlace parentStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parentStopPlaceId);
        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(parentStopPlace));

        List<StopPlace> stopPlaces = stopPlaceRepository.findAll(childStopPlaceIds);
        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, stopPlaces);

        stopPlaces.forEach(stopPlace -> {
            SiteRefStructure parentSiteRef = stopPlace.getParentSiteRef();
            if (parentSiteRef != null && parentSiteRef.getRef().equals(parentStopPlaceId)) {
                stopPlace.setParentSiteRef(null);
            }
        });

        return parentStopPlace;
    }
}
