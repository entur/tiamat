package org.rutebanken.tiamat.rest.graphql.types;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLObjectType;
import org.springframework.stereotype.Component;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.EXTERNAL_LINK_LOCATION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.EXTERNAL_LINK_NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.INPUT_TYPE_EXTERNAL_LINK;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_EXTERNAL_LINK;

@Component
public class ExternalLinkObjectTypeCreator {

    public GraphQLObjectType externalLinkObjectType() {
        return newObject()
                .name(OUTPUT_TYPE_EXTERNAL_LINK)
                .field(newFieldDefinition()
                        .name(EXTERNAL_LINK_NAME)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(EXTERNAL_LINK_LOCATION)
                        .type(GraphQLString))
                .build();
    }
    public GraphQLInputObjectType externalLinkInputType() {
        return newInputObject()
                .name(INPUT_TYPE_EXTERNAL_LINK)
                .field(newInputObjectField()
                        .name(EXTERNAL_LINK_NAME)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(EXTERNAL_LINK_LOCATION)
                        .type(GraphQLString))
                .build();
    }
}
