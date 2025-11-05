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

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.scalars.ExtendedScalars.GraphQLBigDecimal;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.*;

/**
 * Factory for creating Quay GraphQL types (both output and input types).
 * Quays represent boarding/alighting points within a stop place.
 */
@Component
public class QuayTypeFactory implements GraphQLTypeFactory {

    @Autowired
    private CommonFieldsFactory commonFieldsFactory;

    @Override
    public List<GraphQLType> createTypes() {
        // This method creates types without the full common field list.
        // Use createQuayTypes(commonFieldsList) instead when you need the merged common fields.
        List<GraphQLFieldDefinition> commonOutputFields = commonFieldsFactory.createCommonOutputFieldList();
        List<GraphQLInputObjectField> commonInputFields = commonFieldsFactory.createCommonInputFieldList(
                embeddableMultiLingualStringInputObjectType);

        return List.of(
                createQuayObjectType(commonOutputFields),
                createQuayInputObjectType(commonInputFields)
        );
    }

    /**
     * Creates Quay types with a fully merged common field list.
     * This is the preferred method when calling from the main schema.
     *
     * @param commonFieldsList the merged common field list including zone fields
     * @return list containing Quay output type and input type
     */
    public List<GraphQLType> createQuayTypes(List<GraphQLFieldDefinition> commonFieldsList) {
        List<GraphQLInputObjectField> commonInputFields = commonFieldsFactory.createCommonInputFieldList(
                embeddableMultiLingualStringInputObjectType);

        return List.of(
                createQuayObjectType(commonFieldsList),
                createQuayInputObjectType(commonInputFields)
        );
    }

    @Override
    public String getFactoryName() {
        return "QuayTypeFactory";
    }

    /**
     * Creates the output object type for Quay.
     */
    private GraphQLObjectType createQuayObjectType(List<GraphQLFieldDefinition> commonFieldsList) {
        return newObject()
                .name(OUTPUT_TYPE_QUAY)
                .fields(commonFieldsList)
                .field(newFieldDefinition()
                        .name(COMPASS_BEARING)
                        .type(GraphQLBigDecimal))
                .field(newFieldDefinition()
                        .name(ALTERNATIVE_NAMES)
                        .type(new GraphQLList(alternativeNameObjectType)))
                .field(newFieldDefinition()
                        .name(BOARDING_POSITIONS)
                        .type(new GraphQLList(boardingPositionsObjectType))
                )
                .build();
    }

    /**
     * Creates the input object type for Quay.
     */
    private GraphQLInputObjectType createQuayInputObjectType(List<GraphQLInputObjectField> graphQLCommonInputObjectFieldsList) {
        return newInputObject()
                .name(INPUT_TYPE_QUAY)
                .fields(graphQLCommonInputObjectFieldsList)
                .field(newInputObjectField()
                        .name(COMPASS_BEARING)
                        .type(GraphQLBigDecimal))
                .field(newInputObjectField()
                        .name(BOARDING_POSITIONS)
                        .type(new GraphQLList(boardingPositionsInputObjectType)))
                .build();
    }
}