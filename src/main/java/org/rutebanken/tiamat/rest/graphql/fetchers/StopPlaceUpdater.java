/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.rest.graphql.fetchers;

import com.google.api.client.util.Preconditions;
import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.rest.graphql.helpers.CleanupHelper;
import org.rutebanken.tiamat.rest.graphql.mappers.StopPlaceMapper;
import org.rutebanken.tiamat.rest.validation.StopPlaceMutationValidator;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Service("stopPlaceUpdater")
@Transactional
class StopPlaceUpdater implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceUpdater.class);

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceMapper stopPlaceMapper;

    @Autowired
    private StopPlaceMutationValidator stopPlaceMutationValidator;

    @Autowired
    private MutateLock mutateLock;

    @Autowired
    private VersionCreator versionCreator;

    @Override
    public Object get(DataFetchingEnvironment environment) {
        List<Field> fields = environment.getMergedField().getFields();
        CleanupHelper.trimValues(environment.getArguments());
        StopPlace stopPlace = null;
        for (Field field : fields) {
            if (field.getName().equals(MUTATE_STOPPLACE)) {
                stopPlace = createOrUpdateStopPlaceInLock(environment, false);
            } else if (field.getName().equals(MUTATE_PARENT_STOPPLACE)) {
                stopPlace = createOrUpdateStopPlaceInLock(environment, true);
            }
        }
        return Arrays.asList(stopPlace);
    }


    private StopPlace createOrUpdateStopPlaceInLock(DataFetchingEnvironment environment, boolean mutateParent) {
        return mutateLock.executeInLock(() -> createOrUpdateStopPlace(environment, mutateParent));
    }

    private StopPlace createOrUpdateStopPlace(DataFetchingEnvironment environment, boolean mutateParent) {
        StopPlace updatedStopPlace;
        StopPlace existingStopPlace = null;

        Map input = environment.getArgument(OUTPUT_TYPE_STOPPLACE);
        if (input == null) {
            input = environment.getArgument(OUTPUT_TYPE_PARENT_STOPPLACE);
        }

        if (input != null) {

            String netexId = (String) input.get(ID);
            if (netexId != null) {

                logger.info("About to update StopPlace {}", netexId);

                existingStopPlace = stopPlaceMutationValidator.validateStopPlaceUpdate(netexId, mutateParent);
                updatedStopPlace = versionCreator.createCopy(existingStopPlace, StopPlace.class);

            } else {
                Preconditions.checkArgument(!mutateParent,
                        "Cannot create new parent stop place. Use mutation %s", CREATE_MULTI_MODAL_STOPPLACE);

                logger.info("Creating new StopPlace");
                updatedStopPlace = new StopPlace();
            }

            if (updatedStopPlace != null) {
                boolean hasValuesChanged = stopPlaceMapper.populateStopPlaceFromInput(input, updatedStopPlace);

                Set<String> childStopsUpdated;
                if (updatedStopPlace.isParentStopPlace()) {
                    childStopsUpdated = handleChildStops(input, updatedStopPlace);
                    hasValuesChanged |= !childStopsUpdated.isEmpty();
                } else {
                    childStopsUpdated = new HashSet<>();
                }

                if (hasValuesChanged) {
                    stopPlaceMutationValidator.validateStopPlaceName(updatedStopPlace);

                    updatedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(existingStopPlace, updatedStopPlace, childStopsUpdated);

                    return updatedStopPlace;
                }
            }
        }
        return existingStopPlace;
    }

    private Set<String> handleChildStops(Map input, StopPlace updatedParentStopPlace) {
        Set<String> childStopsUpdated = new HashSet<>();

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
                stopPlaceMutationValidator.verifyStopPlaceNotNull(existingChildStopPlace, childNetexId);

                // Next line is not strictly required. As the child will always belong to the parent.
                stopPlaceMutationValidator.validateChildBelongsToParent(existingChildStopPlace, updatedParentStopPlace);

                logger.info("Populating changes for child stop {} (parent: {})", childNetexId, updatedParentStopPlace.getNetexId());
                boolean wasUpdated = stopPlaceMapper.populateStopPlaceFromInput((Map) childStopMap, existingChildStopPlace);;

                if (wasUpdated) {
                    childStopsUpdated.add(existingChildStopPlace.getNetexId());
                }
            }

            logger.info("Applied changes for {} child stops. Parent stop contains {} child stops", childStopsUpdated.size(), updatedParentStopPlace.getChildren().size());
        }
        return childStopsUpdated;
    }
}
