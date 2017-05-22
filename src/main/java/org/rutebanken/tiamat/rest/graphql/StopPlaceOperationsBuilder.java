package org.rutebanken.tiamat.rest.graphql;

import com.google.api.client.util.Preconditions;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;

@Component
@Transactional
public class StopPlaceOperationsBuilder {

    private final String STOP_PLACE_ID = "stopPlaceId";
    private final String FROM_STOP_PLACE_ID = "fromStopPlaceId";
    private final String TO_STOP_PLACE_ID = "toStopPlaceId";
    private final String FROM_QUAY_ID = "fromQuayId";
    private final String TO_QUAY_ID = "toQuayId";
    private final String MERGE_STOP_PLACES = "mergeStopPlaces";
    private final String MERGE_QUAYS = "mergeQuays";

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    public List<GraphQLFieldDefinition> getStopPlaceOperations(GraphQLObjectType stopPlaceObjectType) {
        List<GraphQLFieldDefinition> operations = new ArrayList<>();

        //Merge two StopPlaces
        operations.add(newFieldDefinition()
                .type(stopPlaceObjectType)
                .name(MERGE_STOP_PLACES)
                .description("Merges two StopPlaces by terminating 'from'-StopPlace, and copying quays/values into 'to'-StopPlace")
                .argument(newArgument().name(FROM_STOP_PLACE_ID).type(new GraphQLNonNull(GraphQLString)))
                .argument(newArgument().name(TO_STOP_PLACE_ID).type(new GraphQLNonNull(GraphQLString)))
                .dataFetcher(environment -> mergeStopPlaces(environment.getArgument(FROM_STOP_PLACE_ID), environment.getArgument(TO_STOP_PLACE_ID)))
                .build());

        //Move Quay from one StopPlace to another
        operations.add(newFieldDefinition()
                .type(stopPlaceObjectType)
                .name(MERGE_QUAYS)
                .description("Merges two Quays on a StopPlace.")
                .argument(newArgument().name(STOP_PLACE_ID).type(new GraphQLNonNull(GraphQLString)))
                .argument(newArgument().name(FROM_QUAY_ID).type(new GraphQLNonNull(GraphQLString)))
                .argument(newArgument().name(TO_QUAY_ID).type(new GraphQLNonNull(GraphQLString)))
                .dataFetcher(environment -> mergeQuays(environment.getArgument(STOP_PLACE_ID), environment.getArgument(FROM_QUAY_ID), environment.getArgument(TO_QUAY_ID)))
                .build());

        return operations;
    }

    protected StopPlace mergeStopPlaces(String fromStopPlaceId, String toStopPlaceId) {
        StopPlace fromStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(fromStopPlaceId);
        StopPlace toStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(toStopPlaceId);

        Preconditions.checkArgument(fromStopPlace != null, "Attempting merge from StopPlace [id = %s], but StopPlace does not exist.", fromStopPlaceId);
        Preconditions.checkArgument(toStopPlace != null, "Attempting merge to StopPlace [id = %s], but StopPlace does not exist.", toStopPlaceId);

        StopPlace fromStopPlaceToTerminate = stopPlaceVersionedSaverService.createCopy(fromStopPlace, StopPlace.class);
        terminateEntity(fromStopPlaceToTerminate);

        //Terminate validity of from-StopPlace
        fromStopPlaceToTerminate = stopPlaceVersionedSaverService.saveNewVersion(fromStopPlace, fromStopPlaceToTerminate);

        // create new, detached copy of from-StopPlace to move quays/attributes
        fromStopPlaceToTerminate = stopPlaceVersionedSaverService.createCopy(fromStopPlaceToTerminate, StopPlace.class);

        //New version of merged StopPlace
        StopPlace mergedStopPlace = stopPlaceVersionedSaverService.createCopy(toStopPlace, StopPlace.class);

        // Keep importedId
        mergedStopPlace.getOriginalIds().addAll(fromStopPlaceToTerminate.getOriginalIds());

        //Transfer quays
        mergedStopPlace.getQuays().addAll(fromStopPlaceToTerminate.getQuays());

        mergedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(toStopPlace, mergedStopPlace);

        return mergedStopPlace;
    }

    protected StopPlace mergeQuays(String stopPlaceId, String fromQuayId, String toQuayId) {
        StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceId);
        Preconditions.checkArgument(stopPlace != null, "Attempting to quays from StopPlace [id = %s], but StopPlace does not exist.", stopPlaceId);

        StopPlace updatedStopPlace = stopPlaceVersionedSaverService.createCopy(stopPlace, StopPlace.class);

        Optional<Quay> fromQuayOpt = updatedStopPlace.getQuays().stream().filter(quay -> quay.getNetexId().equals(fromQuayId)).findFirst();
        Optional<Quay> toQuayOpt = updatedStopPlace.getQuays().stream().filter(quay -> quay.getNetexId().equals(toQuayId)).findFirst();

        Preconditions.checkArgument(fromQuayOpt.isPresent(), "Quay does not exist on StopPlace", fromQuayId);
        Preconditions.checkArgument(toQuayOpt.isPresent(), "Quay does not exist on StopPlace", toQuayId);

        Quay fromQuay = fromQuayOpt.get();
        Quay toQuay = toQuayOpt.get();

        //Copy attributes to to-quay
        toQuay.getOriginalIds().addAll(fromQuay.getOriginalIds());

        updatedStopPlace.getQuays()
                .removeIf(quay -> quay.getNetexId().equals(fromQuayId));

        //Save updated StopPlace
        updatedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace, updatedStopPlace);

        return updatedStopPlace;
    }

    private EntityInVersionStructure terminateEntity(EntityInVersionStructure entity) {
        // Terminate validity for "from"-stopPlace
        if (entity.getValidBetweens() != null && !entity.getValidBetweens().isEmpty()) {
            entity.getValidBetweens().get(0).setToDate(Instant.now());
        } else {
            entity.getValidBetweens().add(new ValidBetween(null, Instant.now()));
        }
        return entity;
    }
}
