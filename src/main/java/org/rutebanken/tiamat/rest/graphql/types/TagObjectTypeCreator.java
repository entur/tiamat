package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLObjectType;
import org.rutebanken.tiamat.rest.graphql.scalars.DateScalar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Component
public class TagObjectTypeCreator {

    @Autowired
    private DateScalar dateScalar;

    private GraphQLObjectType graphQLObjectType = null;

    public GraphQLObjectType create() {
        if (graphQLObjectType == null) {
            graphQLObjectType = newObject()
                    .name(OUTPUT_TYPE_TAG)
                    .description(TAG_DESCRIPTION)
                    .field(newFieldDefinition()
                            .name(NAME)
                            .type(GraphQLString)
                            .description(TAG_NAME_DESCRIPTION))
                    .field(newFieldDefinition()
                            .name(TAG_ID_REFERENCE)
                            .type(GraphQLString)
                            .description(TAG_ID_REFERENCE_DESCRIPTION))
                    .field(newFieldDefinition()
                            .name("created")
                            .description("When this tag was added to the referenced entity")
                            .type(dateScalar.getGraphQLDateScalar()))
                    .field(newFieldDefinition()
                            .name(TAG_COMMENT)
                            .type(GraphQLString)
                            .description(TAG_COMMENT_DESCRIPTION))
                    .field(newFieldDefinition()
                            .name("removed")
                            .description("When this tag was removed")
                            .type(dateScalar.getGraphQLDateScalar()))
                    .field(newFieldDefinition()
                            .name("removedBy")
                            .description("Removed by user")
                            .type(GraphQLString))
                    .build();
        }
        return graphQLObjectType;
    }
}
