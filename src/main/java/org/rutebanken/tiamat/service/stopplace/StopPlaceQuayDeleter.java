package org.rutebanken.tiamat.service.stopplace;

import com.google.api.client.util.Preconditions;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Autowired
    private UsernameFetcher usernameFetcher;

    @Autowired
    private ChildFromParentResolver childFromParentResolver;

    public StopPlace deleteQuay(String stopPlaceId, String quayId, String versionComment) {

        logger.warn("{} is deleting quay {} from stop place {} with comment {}", usernameFetcher.getUserNameForAuthenticatedUser(), quayId, stopPlaceId, versionComment);

        StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);

        Preconditions.checkArgument(stopPlace != null, "Attempting to delete StopPlace [id = %s], but StopPlace does not exist.", stopPlaceId);

        Preconditions.checkArgument(!stopPlace.isParentStopPlace(), "Cannot merge quays of parent stop place: [id = %s].", stopPlaceId);

        Optional<Quay> optionalQuay = stopPlace.getQuays().stream().filter(quay -> quay.getNetexId().equals(quayId)).findFirst();
        Preconditions.checkArgument(optionalQuay.isPresent(), "Attempting to delete Quay [id = %s], but Quay does not exist on StopPlace [id = %s].", quayId, stopPlaceId);

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(stopPlace));

        final Optional<StopPlace> parentStopPlace;
        final StopPlace existingParentStopPlace;
        final StopPlace nextVersionStopPlace;

        if(stopPlace.getParentSiteRef() != null && stopPlace.getParentSiteRef().getRef() != null) {

            existingParentStopPlace = stopPlaceRepository.findFirstByNetexIdAndVersion(stopPlace.getParentSiteRef().getRef(),
                    Long.parseLong(stopPlace.getParentSiteRef().getVersion()));

            StopPlace parentCopy = stopPlaceVersionedSaverService.createCopy(existingParentStopPlace, StopPlace.class);
            nextVersionStopPlace = childFromParentResolver.resolveChildFromParent(parentCopy, stopPlace.getNetexId(), stopPlace.getVersion());

            parentStopPlace = Optional.of(parentCopy);
        } else {
            parentStopPlace = Optional.empty();
            existingParentStopPlace = null;
            nextVersionStopPlace = stopPlaceVersionedSaverService.createCopy(stopPlace, StopPlace.class);
        }


        nextVersionStopPlace.getQuays().removeIf(quay -> quay.getNetexId().equals(quayId));

        if(parentStopPlace.isPresent()) {
            parentStopPlace.get().setVersionComment(versionComment);
        } else {
            nextVersionStopPlace.setVersionComment(versionComment);
        }


        if(parentStopPlace.isPresent()) {
            logger.info("Saving parent stop place {}. Returning parent of child: {}", parentStopPlace.get().getNetexId(), stopPlace.getNetexId());
            return stopPlaceVersionedSaverService.saveNewVersion(existingParentStopPlace, parentStopPlace.get());
        } else {
            return stopPlaceVersionedSaverService.saveNewVersion(stopPlace, nextVersionStopPlace);
        }
    }
}
