package org.rutebanken.tiamat.rest.graphql.operations;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNonNull;
import org.rutebanken.tiamat.rest.graphql.types.TagObjectTypeCreator;
import org.rutebanken.tiamat.service.TagCreator;
import org.rutebanken.tiamat.service.TagRemover;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Component
public class TagOperationsBuilder {

    @Autowired
    private TagObjectTypeCreator tagObjectTypeCreator;

    @Autowired
    private TagRemover tagRemover;

    @Autowired
    private TagCreator tagCreator;

    public List<GraphQLFieldDefinition> getTagOperations() {

        List<GraphQLArgument> createAndRemoveArguments = Arrays.asList(

                newArgument()
                        .name(TAG_ID_REFERENCE)
                        .type(new GraphQLNonNull(GraphQLString))
                        .description(TAG_ID_REFERENCE_DESCRIPTION)
                        .build(),

                newArgument()
                        .name(TAG_NAME)
                        .type(new GraphQLNonNull(GraphQLString))
                        .description(TAG_NAME_DESCRIPTION)
                        .build(),
                newArgument()
                        .name(TAG_COMMENT)
                        .type(GraphQLString)
                        .description(TAG_COMMENT_DESCRIPTION)
                        .build());

        return Arrays.asList(
                newFieldDefinition()
                        .type(tagObjectTypeCreator.create())
                        .name(REMOVE_TAG)
                        .description("Remove tag from referenced entity")
                        .argument(createAndRemoveArguments)
                        .dataFetcher(environment ->
                                tagRemover.removeTag(
                                        environment.getArgument(TAG_NAME),
                                        environment.getArgument(TAG_ID_REFERENCE),
                                        environment.getArgument(TAG_COMMENT)))
                        .build(),
                newFieldDefinition()
                        .type(tagObjectTypeCreator.create())
                        .name(CREATE_TAG)
                        .description("Create tag for referenced entity.")
                        .argument(createAndRemoveArguments)
                        .dataFetcher(environment ->
                                tagCreator.createTag(
                                        environment.getArgument(TAG_NAME),
                                        environment.getArgument(TAG_ID_REFERENCE),
                                        environment.getArgument(TAG_COMMENT)))
                        .build()
        );
    }

}
