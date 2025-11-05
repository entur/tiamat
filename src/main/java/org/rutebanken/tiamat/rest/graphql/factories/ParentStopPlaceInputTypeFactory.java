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

package org.rutebanken.tiamat.rest.graphql.factories;

import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLType;
import org.rutebanken.tiamat.rest.graphql.scalars.TransportModeScalar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CHILDREN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.INPUT_TYPE_PARENT_STOPPLACE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.URL;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALID_BETWEEN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VERSION_COMMENT;

/**
 * Factory for creating ParentStopPlace GraphQL input type.
 * ParentStopPlace input type is used for mutations to create/update parent stop places with child stops.
 */
@Component
public class ParentStopPlaceInputTypeFactory implements GraphQLTypeFactory {

    @Autowired
    private TransportModeScalar transportModeScalar;

    @Override
    public List<GraphQLType> createTypes() {
        // Cannot create type without dependencies.
        // Use createParentStopPlaceInputType(...) instead.
        throw new UnsupportedOperationException(
                "ParentStopPlaceInput requires common field list, ValidBetween type, and StopPlaceInput type. " +
                "Use createParentStopPlaceInputType(List, GraphQLInputObjectType, GraphQLInputObjectType) instead.");
    }

    @Override
    public String getFactoryName() {
        return "ParentStopPlaceInputTypeFactory";
    }

    /**
     * Creates the input object type for ParentStopPlace with required dependencies.
     *
     * @param commonInputFieldList the common input fields shared across types
     * @param validBetweenInputObjectType the ValidBetween input object type
     * @param stopPlaceInputObjectType the StopPlace input object type
     * @return the ParentStopPlace GraphQL input type
     */
    public GraphQLInputObjectType createParentStopPlaceInputType(
            List<GraphQLInputObjectField> commonInputFieldList,
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
                        .name(URL)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(CHILDREN)
                        .type(new GraphQLList(stopPlaceInputObjectType)))
                .build();
    }
}
