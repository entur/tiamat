package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLObjectType;
import org.rutebanken.tiamat.rest.graphql.scalars.DateScalar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_TAG;

@Component
public class TagObjectTypeCreator {

    @Autowired
    private DateScalar dateScalar;

    public GraphQLObjectType create() {
        return newObject()
                .name(OUTPUT_TYPE_TAG)
                .field(newFieldDefinition()
                        .name(NAME)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name("ID ref")
                        .description("Reference to ID")
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name("created")
                        .description("When this tag was added to the referenced entity")
                        .type(dateScalar.getGraphQLDateScalar()))
                .field(newFieldDefinition()
                        .name("comment")
                        .type(GraphQLString))
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
}
