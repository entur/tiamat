package org.rutebanken.tiamat.rest.graphql.operations;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import org.rutebanken.tiamat.service.MultiModalStopPlaceEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.embeddableMultiLingualStringInputObjectType;

@Component
public class MultiModalityOperationsBuilder {

    @Autowired
    private MultiModalStopPlaceEditor parentStopPlaceEditor;

    public List<GraphQLFieldDefinition> getMultiModalityOperations(GraphQLObjectType parentStopPlaceObjectType) {
        List<GraphQLFieldDefinition> operations = new ArrayList<>();

        operations.add(newFieldDefinition()
                .type(parentStopPlaceObjectType)
                .name(CREATE_MULTIMODAL_STOPPLACE)
                .description("Creates a new multimodal parent StopPlace")
                .argument(newArgument().name(STOP_PLACE_ID).type(new GraphQLList(GraphQLString)))
                .argument(newArgument().name(NAME).type(new GraphQLNonNull(embeddableMultiLingualStringInputObjectType)))
                .dataFetcher(environment -> parentStopPlaceEditor.createMultiModalParentStopPlace(environment.getArgument(STOP_PLACE_ID), getEmbeddableString((Map) environment.getArgument(NAME))))
                .build());

        operations.add(newFieldDefinition()
                .type(parentStopPlaceObjectType)
                .name(ADD_TO_MULTIMODAL_STOPPLACE)
                .description("Adds a StopPlace to an existing ParentStopPlace")
                .argument(newArgument().name(PARENT_SITE_REF).type(GraphQLString))
                .argument(newArgument().name(STOP_PLACE_ID).type(new GraphQLList(GraphQLString)))
                .dataFetcher(environment -> parentStopPlaceEditor.addToMultiModalParentStopPlace(environment.getArgument(PARENT_SITE_REF), environment.getArgument(STOP_PLACE_ID)))
                .build());

        operations.add(newFieldDefinition()
                .type(parentStopPlaceObjectType)
                .name(REMOVE_FROM_MULTIMODAL_STOPPLACE)
                .description("Removes a StopPlace from an existing ParentStopPlace")
                .argument(newArgument().name(PARENT_SITE_REF).type(GraphQLString))
                .argument(newArgument().name(STOP_PLACE_ID).type(new GraphQLList(GraphQLString)))
                .dataFetcher(environment -> parentStopPlaceEditor.removeFromMultiModalStopPlace(environment.getArgument(PARENT_SITE_REF), environment.getArgument(STOP_PLACE_ID)))
                .build());

        return operations;
    }

}
