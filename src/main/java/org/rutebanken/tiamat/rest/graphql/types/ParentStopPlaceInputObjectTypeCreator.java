package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import org.rutebanken.tiamat.rest.graphql.scalars.TransportModeScalar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Component
public class ParentStopPlaceInputObjectTypeCreator {

    @Autowired
    private TransportModeScalar transportModeScalar;

    public GraphQLInputObjectType create(List<GraphQLInputObjectField> commonInputFieldList,
                                         GraphQLInputObjectType validBetweenInputObjectType,
                                         GraphQLInputObjectType stopPlaceInputObjectType) {
        return newInputObject()
                .name(INPUT_TYPE_PARENT_STOPPLACE)
                .fields(commonInputFieldList)
                .fields(transportModeScalar.createTransportModeInputFieldsList())
                .field(newInputObjectField()
                        .name(VERSION_COMMENT)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(VALID_BETWEEN)
                        .type(validBetweenInputObjectType))
                .field(newInputObjectField()
                        .name(CHILDREN)
                        .type(new GraphQLList(stopPlaceInputObjectType)))
                .build();
        
    }
}
