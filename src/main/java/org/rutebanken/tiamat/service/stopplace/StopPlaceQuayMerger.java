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
import org.rutebanken.tiamat.versioning.CopiedEntity;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.rutebanken.tiamat.versioning.util.StopPlaceCopyHelper;
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
    private StopPlaceCopyHelper stopPlaceCopyHelper;

    public StopPlace mergeQuays(final String stopPlaceId, String fromQuayId, String toQuayId, String versionComment, boolean isDryRun) {

        logger.info("{} is about to merge quays {} -> {} of stop place {}", usernameFetcher.getUserNameForAuthenticatedUser(), fromQuayId, toQuayId, stopPlaceId);

        final StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);
        Preconditions.checkArgument(stopPlace != null, "Attempting to quays from StopPlace [id = %s], but StopPlace does not exist.", stopPlaceId);

        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(stopPlace));

        Preconditions.checkArgument(!stopPlace.isParentStopPlace(), "Cannot merge quays of parent StopPlace [id = %s].", stopPlaceId);

        CopiedEntity<StopPlace> stopPlaceCopies = stopPlaceCopyHelper.createCopies(stopPlace);


        Optional<Quay> fromQuayOpt = stopPlaceCopies.getCopiedEntity().getQuays().stream().filter(quay -> quay.getNetexId().equals(fromQuayId)).findFirst();
        Optional<Quay> toQuayOpt = stopPlaceCopies.getCopiedEntity().getQuays().stream().filter(quay -> quay.getNetexId().equals(toQuayId)).findFirst();

        Preconditions.checkArgument(fromQuayOpt.isPresent(), "Quay does not exist on StopPlace", fromQuayId);
        Preconditions.checkArgument(toQuayOpt.isPresent(), "Quay does not exist on StopPlace", toQuayId);

        Quay fromQuay = fromQuayOpt.get();
        Quay toQuay = toQuayOpt.get();

        executeQuayMerge(fromQuay, toQuay);

        stopPlaceCopies.getCopiedEntity().getQuays()
                .removeIf(quay -> quay.getNetexId().equals(fromQuayId));

        if(stopPlaceCopies.hasParent()) {
            stopPlaceCopies.getCopiedParent().setVersionComment(versionComment);
        } else {
            stopPlaceCopies.getCopiedEntity().setVersionComment(versionComment);
        }


        logger.info("Saving stop place after merging: {}", stopPlace.getNetexId());
        if(stopPlaceCopies.hasParent()) {
            if(!isDryRun) {
                logger.info("Saving parent stop place {}. Returning parent of child: {}", stopPlaceCopies.getCopiedParent().getNetexId(), stopPlace.getNetexId());
                return stopPlaceVersionedSaverService.saveNewVersion(stopPlaceCopies.getExistingParent(), stopPlaceCopies.getCopiedParent());
            } else {
                return stopPlaceCopies.getCopiedParent();
            }
        } else {
            if(!isDryRun) {
                return stopPlaceVersionedSaverService.saveNewVersion(stopPlace, stopPlaceCopies.getCopiedEntity());
            } else {
                return stopPlaceCopies.getCopiedEntity();
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
