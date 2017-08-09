package org.rutebanken.tiamat.rest.graphql.operation;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import org.rutebanken.tiamat.rest.graphql.MultiModalStopPlaceEditor;
import org.rutebanken.tiamat.rest.graphql.scalars.DateScalar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.embeddableMultiLingualStringInputObjectType;

@Component
public class MultiModalityOperationsBuilder {

    @Autowired
    private MultiModalStopPlaceEditor parentStopPlaceCreator;

    @Autowired
    private DateScalar dateScalar;

    public List<GraphQLFieldDefinition> getMultiModalityOperations(GraphQLObjectType stopPlaceObjectType) {
        List<GraphQLFieldDefinition> operations = new ArrayList<>();

        //Merge two StopPlaces
        operations.add(newFieldDefinition()
                .type(stopPlaceObjectType)
                .name(CREATE_MULTIMODAL_STOPPLACE)
                .description("Creates a new multimodal parent StopPlace")
                .argument(newArgument().name(STOP_PLACE_ID).type(new GraphQLList(GraphQLString)))
                .argument(newArgument().name(NAME).type(new GraphQLNonNull(embeddableMultiLingualStringInputObjectType)))
                .dataFetcher(environment -> parentStopPlaceCreator.createMultiModalParentStopPlace(environment.getArgument(STOP_PLACE_ID), environment.getArgument(NAME)))
                .build());

        //Merge two StopPlaces
        operations.add(newFieldDefinition()
                .type(stopPlaceObjectType)
                .name(ADD_TO_MULTIMODAL_STOPPLACE)
                .description("Adds a StopPlace to an existing ParentStopPlace")
                .argument(newArgument().name(PARENT_SITE_REF).type(GraphQLString))
                .argument(newArgument().name(STOP_PLACE_ID).type(new GraphQLList(GraphQLString)))
                .dataFetcher(environment -> parentStopPlaceCreator.addToMultiModalParentStopPlace(environment.getArgument(PARENT_SITE_REF), environment.getArgument(STOP_PLACE_ID)))
                .build());

        //Merge two StopPlaces
        operations.add(newFieldDefinition()
                .type(stopPlaceObjectType)
                .name(REMOVE_FROM_MULTIMODAL_STOPPLACE)
                .description("Removes a StopPlace from an existing ParentStopPlace")
                .argument(newArgument().name(PARENT_SITE_REF).type(GraphQLString))
                .argument(newArgument().name(STOP_PLACE_ID).type(new GraphQLList(GraphQLString)))
                .dataFetcher(environment -> parentStopPlaceCreator.removeFromMultiModalStopPlace(environment.getArgument(PARENT_SITE_REF), environment.getArgument(STOP_PLACE_ID)))
                .build());

        return operations;
    }

}
