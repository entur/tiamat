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

    public StopPlace mergeQuays(String stopPlaceId, String fromQuayId, String toQuayId, String versionComment, boolean isDryRun) {

        logger.info("{} is about to merge quays {} -> {} of stop place {}", usernameFetcher.getUserNameForAuthenticatedUser(), fromQuayId, toQuayId, stopPlaceId);

        StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);
        Preconditions.checkArgument(stopPlace != null, "Attempting to quays from StopPlace [id = %s], but StopPlace does not exist.", stopPlaceId);

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(stopPlace));

        Preconditions.checkArgument(!stopPlace.isParentStopPlace(), "Cannot merge quays of parent StopPlace [id = %s].", stopPlaceId);

        StopPlace updatedStopPlace = stopPlaceVersionedSaverService.createCopy(stopPlace, StopPlace.class);

        Optional<Quay> fromQuayOpt = updatedStopPlace.getQuays().stream().filter(quay -> quay.getNetexId().equals(fromQuayId)).findFirst();
        Optional<Quay> toQuayOpt = updatedStopPlace.getQuays().stream().filter(quay -> quay.getNetexId().equals(toQuayId)).findFirst();

        Preconditions.checkArgument(fromQuayOpt.isPresent(), "Quay does not exist on StopPlace", fromQuayId);
        Preconditions.checkArgument(toQuayOpt.isPresent(), "Quay does not exist on StopPlace", toQuayId);

        Quay fromQuay = fromQuayOpt.get();
        Quay toQuay = toQuayOpt.get();

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

        updatedStopPlace.getQuays()
                .removeIf(quay -> quay.getNetexId().equals(fromQuayId));

        updatedStopPlace.setVersionComment(versionComment);

        if (!isDryRun) {
            //Save updated StopPlace
            updatedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace, updatedStopPlace);
        }

        return updatedStopPlace;
    }
}
