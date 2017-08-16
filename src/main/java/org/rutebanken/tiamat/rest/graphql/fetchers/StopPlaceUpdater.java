package org.rutebanken.tiamat.rest.graphql.fetchers;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Preconditions;
import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.graphql.helpers.CleanupHelper;
import org.rutebanken.tiamat.rest.graphql.mappers.StopPlaceMapper;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Service("stopPlaceUpdater")
@Transactional
class StopPlaceUpdater implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceUpdater.class);

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private ReflectionAuthorizationService authorizationService;

    @Autowired
    private StopPlaceMapper stopPlaceMapper;

    @Override
    public Object get(DataFetchingEnvironment environment) {
        List<Field> fields = environment.getFields();
        CleanupHelper.trimValues(environment.getArguments());
        StopPlace stopPlace = null;
        for (Field field : fields) {
            if (field.getName().equals(MUTATE_STOPPLACE)) {
                stopPlace = createOrUpdateStopPlace(environment, false);
            } else if (field.getName().equals(MUTATE_PARENT_STOPPLACE)) {
                stopPlace = createOrUpdateStopPlace(environment, true);
            }
        }
        return Arrays.asList(stopPlace);
    }


    private StopPlace createOrUpdateStopPlace(DataFetchingEnvironment environment, boolean mutateParent) {
        StopPlace updatedStopPlace;
        StopPlace existingVersion = null;

        Map input = environment.getArgument(OUTPUT_TYPE_STOPPLACE);
        if (input == null) {
            input = environment.getArgument(OUTPUT_TYPE_PARENT_STOPPLACE);
        }

        if (input != null) {

            String netexId = (String) input.get(ID);
            if (netexId != null) {
                logger.info("About to update StopPlace {}", netexId);

                existingVersion = findAndVerify(netexId);

                if (mutateParent) {
                    Preconditions.checkArgument(existingVersion.isParentStopPlace(),
                            "Attempting to update StopPlace as parent [id = %s], but StopPlace is not a parent", netexId);
                } else {
                    Preconditions.checkArgument(!existingVersion.isParentStopPlace(),
                            "Attempting to update parent StopPlace [id = %s] with incorrect mutation. Use %s", netexId, MUTATE_PARENT_STOPPLACE);
                }

                updatedStopPlace = stopPlaceVersionedSaverService.createCopy(existingVersion, StopPlace.class);

            } else {
                Preconditions.checkArgument(!mutateParent,
                        "Cannot create new parent stop place. Use mutation %s", CREATE_MULTIMODAL_STOPPLACE);

                logger.info("Creating new StopPlace");
                updatedStopPlace = new StopPlace();
            }

            if (updatedStopPlace != null) {
                boolean hasValuesChanged = stopPlaceMapper.populateStopPlaceFromInput(input, updatedStopPlace);

                if (updatedStopPlace.isParentStopPlace()) {
                    hasValuesChanged |= handleChildStops(input, updatedStopPlace);
                }

                if (hasValuesChanged) {
                    authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(existingVersion, updatedStopPlace));

                    if (updatedStopPlace.getName() == null || Strings.isNullOrEmpty(updatedStopPlace.getName().getValue())) {
                        throw new IllegalArgumentException("Updated stop place must have name set: " + updatedStopPlace);
                    }

                    updatedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(existingVersion, updatedStopPlace);

                    return updatedStopPlace;
                }
            }
        }
        return existingVersion;
    }

    private boolean handleChildStops(Map input, StopPlace updatedParentStopPlace) {
        boolean updated = false;

        int updatedCount = 0;
        if (input.get(CHILDREN) != null) {
            List childObjects = (List) input.get(CHILDREN);
            logger.info("Incoming child stop objects: {}", childObjects);

            for (Object childStopObject : childObjects) {
                Map childStopMap = (Map) childStopObject;
                String childNetexId = (String) childStopMap.get(ID);

                if (updatedParentStopPlace.getChildren().stream().noneMatch(child -> child.getNetexId().equals(childNetexId))) {
                    throw new RuntimeException("Parent " + updatedParentStopPlace.getNetexId() + " does not already contain this child " + childNetexId + ". Cannot continue.");
                }

                logger.info("Finding existing child stop place from parent: {}", updatedParentStopPlace.getNetexId());
                StopPlace existingChildStopPlace = updatedParentStopPlace.getChildren().stream().filter(c -> c.getNetexId().equals(childNetexId)).findFirst().orElse(null);
                verifyStopPlaceNotNull(existingChildStopPlace, childNetexId);

                // Next line is not strictly required. As the child will alway belong to the parent.
                verifyCorrectParentSet(existingChildStopPlace, updatedParentStopPlace);

                authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(existingChildStopPlace));

                logger.info("Populating changes for child stop {} (parent: {})", childNetexId, updatedParentStopPlace.getNetexId());
                updated |= stopPlaceMapper.populateStopPlaceFromInput((Map) childStopMap, existingChildStopPlace);
                updatedCount++;
                // Verify after changes applied as well
                authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(existingChildStopPlace));
            }

            logger.info("Applied changes for {} child stops. Parent stop contains {} child stops", updatedCount, updatedParentStopPlace.getChildren().size());
        }
        return updated;
    }

    private StopPlace findAndVerify(String netexId) {
        StopPlace existingStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
        verifyStopPlaceNotNull(existingStopPlace, netexId);
        return existingStopPlace;
    }

    private void verifyStopPlaceNotNull(StopPlace existingStopPlace, String netexId) {
        Preconditions.checkArgument(existingStopPlace != null, "Attempting to update StopPlace [id = %s], but StopPlace does not exist.", netexId);
    }

    private void verifyCorrectParentSet(StopPlace existingChildStop, StopPlace existingParentStop) {
        Preconditions.checkArgument(existingChildStop.getParentSiteRef() != null,
                "Attempting to update StopPlace child [id = %s], but it does not belong to any parent.", existingChildStop.getNetexId());

        Preconditions.checkArgument(existingChildStop.getParentSiteRef().getRef().equals(existingParentStop.getNetexId()),
                "Attempting to update StopPlace child [id = %s], but it does not belong to parent %s.", existingChildStop.getNetexId(), existingParentStop.getNetexId());

        Preconditions.checkArgument(existingChildStop.getParentSiteRef().getVersion().equals(String.valueOf(existingParentStop.getVersion())),
                "Attempting to update StopPlace child [id = %s], but it does not refer to parent %s in correct version: %s.",
                existingChildStop.getNetexId(),
                existingParentStop.getNetexId(),
                existingParentStop.getVersion());

    }
}
