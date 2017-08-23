package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLInputObjectType;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Component
public class TagInputObjectTypeCreator {

    private GraphQLInputObjectType tagInputObjectType = null;

    public GraphQLInputObjectType create() {

        if (tagInputObjectType == null) {
            tagInputObjectType = newInputObject()
                    .name(INPUT_TYPE_TAG)
                    .description(TAG_DESCRIPTION)
                    .field(newInputObjectField()
                            .name(NAME)
                            .type(GraphQLString)
                            .description(TAG_NAME_DESCRIPTION))
                    .field(newInputObjectField()
                            .name(TAG_COMMENT)
                            .type(GraphQLString)
                            .description(TAG_COMMENT_DESCRIPTION))
                    .field(newInputObjectField()
                            .name(TAG_ID_REFERENCE)
                            .type(GraphQLString)
                            .description(TAG_ID_REFERENCE_DESCRIPTION))
                    .build();
        }
        return tagInputObjectType;
    }
}
