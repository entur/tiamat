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
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.*;

/**
 * Factory for creating StopPlace input GraphQL type.
 * This is used for mutations (create/update) of stop places.
 */
@Component
public class StopPlaceInputTypeFactory implements GraphQLTypeFactory {

    @Autowired
    private CommonFieldsFactory commonFieldsFactory;

    @Autowired
    private TransportModeScalar transportModeScalar;

    @Override
    public List<GraphQLType> createTypes() {
        List<GraphQLInputObjectField> commonInputFields = commonFieldsFactory.createCommonInputFieldList(
                embeddableMultiLingualStringInputObjectType);

        // Note: This factory needs ValidBetween, Quay, and TopographicPlace input types to be created first
        // These are passed as parameters to maintain initialization order
        return List.of();  // Will be populated after refactoring init() method
    }

    @Override
    public String getFactoryName() {
        return "StopPlaceInputTypeFactory";
    }

    /**
     * Creates the input object type for StopPlace.
     * This method is public to allow the main schema to call it with proper dependencies.
     */
    public GraphQLInputObjectType createStopPlaceInputObjectType(
            List<GraphQLInputObjectField> commonInputFieldsList,
            GraphQLInputObjectType topographicPlaceInputObjectType,
            GraphQLInputObjectType quayObjectInputType,
            GraphQLInputObjectType validBetweenInputObjectType) {

        return newInputObject()
                .name(INPUT_TYPE_STOPPLACE)
                .fields(commonInputFieldsList)
                .fields(transportModeScalar.createTransportModeInputFieldsList())
                .field(newInputObjectField()
                        .name(STOP_PLACE_TYPE)
                        .type(stopPlaceTypeEnum))
                .field(newInputObjectField()
                        .name(TOPOGRAPHIC_PLACE)
                        .type(topographicPlaceInputObjectType))
                .field(newInputObjectField()
                        .name(WEIGHTING)
                        .type(interchangeWeightingEnum))
                .field(newInputObjectField()
                        .name(PARENT_SITE_REF)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(VERSION_COMMENT)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(QUAYS)
                        .type(new GraphQLList(quayObjectInputType)))
                .field(newInputObjectField()
                        .name(VALID_BETWEEN)
                        .type(validBetweenInputObjectType))
                .field(newInputObjectField()
                        .name(TARIFF_ZONES)
                        .description("List of tariff zone references without version")
                        .type(new GraphQLList(versionLessRefInputObjectType)))
                .field(newInputObjectField()
                        .name(ADJACENT_SITES)
                        .description(ADJACENT_SITES_DESCRIPTION)
                        .type(new GraphQLList(versionLessRefInputObjectType)))
                .field(newInputObjectField()
                        .name(URL)
                        .type(GraphQLString).build())
                .build();
    }
}