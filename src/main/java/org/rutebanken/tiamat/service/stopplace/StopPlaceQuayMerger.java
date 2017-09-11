package org.rutebanken.tiamat.service.stopplace;

import com.google.api.client.util.Preconditions;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.MERGED_ID_KEY;
import static org.rutebanken.tiamat.service.stopplace.StopPlaceMerger.IGNORE_PROPERTIES_ON_MERGE;

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

    @Autowired
    private KeyValuesMerger keyValuesMerger;

    @Autowired
    private PlaceEquipmentMerger placeEquipmentMerger;

    @Autowired
    private AlternativeNamesMerger alternativeNamesMerger;

    @Autowired
    private UsernameFetcher usernameFetcher;

    @Autowired
    private ChildFromParentResolver childFromParentResolver;

    public StopPlace mergeQuays(final String stopPlaceId, String fromQuayId, String toQuayId, String versionComment, boolean isDryRun) {

        logger.info("{} is about to merge quays {} -> {} of stop place {}", usernameFetcher.getUserNameForAuthenticatedUser(), fromQuayId, toQuayId, stopPlaceId);

        final StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);
        Preconditions.checkArgument(stopPlace != null, "Attempting to quays from StopPlace [id = %s], but StopPlace does not exist.", stopPlaceId);

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(stopPlace));

        Preconditions.checkArgument(!stopPlace.isParentStopPlace(), "Cannot merge quays of parent StopPlace [id = %s].", stopPlaceId);

        // Check if there is any parent stop place
        final Optional<StopPlace> parentStopPlace;
        final StopPlace existingParentStopPlace;
        final StopPlace updatedStopPlace;

        if(stopPlace.getParentSiteRef() != null && stopPlace.getParentSiteRef().getRef() != null) {
            logger.info("The stop place having its quays merged ({}) is a child of parent: {}", stopPlace.getNetexId(), stopPlace.getParentSiteRef());

            existingParentStopPlace = stopPlaceRepository.findFirstByNetexIdAndVersion(stopPlace.getParentSiteRef().getRef(),
                    Long.parseLong(stopPlace.getParentSiteRef().getVersion()));

            StopPlace parentCopy = stopPlaceVersionedSaverService.createCopy(existingParentStopPlace, StopPlace.class);
            updatedStopPlace = childFromParentResolver.resolveChildFromParent(parentCopy, stopPlace.getNetexId(), stopPlace.getVersion());

            parentStopPlace = Optional.of(parentCopy);
        } else {
            parentStopPlace = Optional.empty();
            existingParentStopPlace = null;
            updatedStopPlace = stopPlaceVersionedSaverService.createCopy(stopPlace, StopPlace.class);
        }

        Optional<Quay> fromQuayOpt = updatedStopPlace.getQuays().stream().filter(quay -> quay.getNetexId().equals(fromQuayId)).findFirst();
        Optional<Quay> toQuayOpt = updatedStopPlace.getQuays().stream().filter(quay -> quay.getNetexId().equals(toQuayId)).findFirst();

        Preconditions.checkArgument(fromQuayOpt.isPresent(), "Quay does not exist on StopPlace", fromQuayId);
        Preconditions.checkArgument(toQuayOpt.isPresent(), "Quay does not exist on StopPlace", toQuayId);

        Quay fromQuay = fromQuayOpt.get();
        Quay toQuay = toQuayOpt.get();

        executeQuayMerge(fromQuay, toQuay);

        updatedStopPlace.getQuays()
                .removeIf(quay -> quay.getNetexId().equals(fromQuayId));

        if(parentStopPlace.isPresent()) {
            parentStopPlace.get().setVersionComment(versionComment);
        } else {
            updatedStopPlace.setVersionComment(versionComment);
        }


        logger.info("Saving stop place after merging: {}", stopPlace.getNetexId());
        if(parentStopPlace.isPresent()) {
            if(!isDryRun) {
                logger.info("Saving parent stop place {}. Returning parent of child: {}", parentStopPlace.get().getNetexId(), stopPlace.getNetexId());
                return stopPlaceVersionedSaverService.saveNewVersion(existingParentStopPlace, parentStopPlace.get());
            } else {
                return parentStopPlace.get();
            }
        } else {
            if(!isDryRun) {
                return stopPlaceVersionedSaverService.saveNewVersion(stopPlace, updatedStopPlace);
            } else {
                return updatedStopPlace;
            }
        }
    }

    private void executeQuayMerge(Quay fromQuay, Quay toQuay) {
        ObjectMerger.copyPropertiesNotNull(fromQuay, toQuay, IGNORE_PROPERTIES_ON_MERGE);
        //Copy attributes to to-quay

        if (fromQuay.getKeyValues() != null) {
            keyValuesMerger.mergeKeyValues(fromQuay.getKeyValues(), toQuay.getKeyValues());
        }

        toQuay.getOrCreateValues(MERGED_ID_KEY).add(fromQuay.getNetexId());

        if (fromQuay.getPlaceEquipments() != null) {
            toQuay.setPlaceEquipments(placeEquipmentMerger.mergePlaceEquipments(fromQuay.getPlaceEquipments(), toQuay.getPlaceEquipments()));
        }

        if (fromQuay.getAlternativeNames() != null) {
            alternativeNamesMerger.mergeAlternativeNames(fromQuay.getAlternativeNames(), toQuay.getAlternativeNames());
        }
    }
}
