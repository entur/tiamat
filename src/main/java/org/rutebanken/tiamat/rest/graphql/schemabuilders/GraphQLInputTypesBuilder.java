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

package org.rutebanken.tiamat.rest.graphql.schemabuilders;

import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import org.rutebanken.tiamat.rest.graphql.registry.GraphQLServicesRegistry;
import org.rutebanken.tiamat.rest.graphql.registry.GraphQLTypeRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.*;

/**
 * Builds all GraphQL input types for mutations.
 * Consolidates input type creation logic to improve maintainability.
 */
@Component
public class GraphQLInputTypesBuilder {

    @Autowired
    private GraphQLTypeRegistry typeRegistry;

    @Autowired
    private GraphQLServicesRegistry servicesRegistry;

    /**
     * Builds all input types needed for GraphQL mutations.
     * Returns a map of input type names to their corresponding GraphQL input types.
     *
     * @param commonInputFieldsList Common input fields shared across entity types
     * @param topographicPlaceInputObjectType TopographicPlace input type
     * @param quayInputObjectType Quay input type
     * @param validBetweenInputObjectType ValidBetween input type
     * @return Map of input type names to GraphQL input object types
     */
    public Map<String, GraphQLInputObjectType> buildInputTypes(
            List<GraphQLInputObjectField> commonInputFieldsList,
            GraphQLInputObjectType topographicPlaceInputObjectType,
            GraphQLInputObjectType quayInputObjectType,
            GraphQLInputObjectType validBetweenInputObjectType) {

        Map<String, GraphQLInputObjectType> inputTypes = new HashMap<>();

        // StopPlace input type
        GraphQLInputObjectType stopPlaceInputObjectType = createStopPlaceInputObjectType(
                commonInputFieldsList,
                topographicPlaceInputObjectType,
                quayInputObjectType,
                validBetweenInputObjectType
        );
        inputTypes.put(INPUT_TYPE_STOPPLACE, stopPlaceInputObjectType);

        // ParentStopPlace input type
        GraphQLInputObjectType parentStopPlaceInputObjectType = typeRegistry
                .getParentStopPlaceInputTypeFactory()
                .createParentStopPlaceInputType(commonInputFieldsList, validBetweenInputObjectType, stopPlaceInputObjectType);
        inputTypes.put(INPUT_TYPE_PARENT_STOPPLACE, parentStopPlaceInputObjectType);

        // Parking input type
        GraphQLInputObjectType parkingInputObjectType = createParkingInputObjectType(validBetweenInputObjectType);
        inputTypes.put(INPUT_TYPE_PARKING, parkingInputObjectType);

        // GroupOfStopPlaces input type
        GraphQLInputObjectType groupOfStopPlacesInputObjectType = (GraphQLInputObjectType)
                typeRegistry.getGroupOfStopPlacesInputTypeFactory().createTypes().getFirst();
        inputTypes.put(INPUT_TYPE_GROUP_OF_STOPPLACES, groupOfStopPlacesInputObjectType);

        // PurposeOfGrouping input type
        GraphQLInputObjectType purposeOfGroupingInputObjectType = (GraphQLInputObjectType)
                typeRegistry.getPurposeOfGroupingInputTypeFactory().createTypes().getFirst();
        inputTypes.put(INPUT_TYPE_PURPOSE_OF_GROUPING, purposeOfGroupingInputObjectType);

        return inputTypes;
    }

    /**
     * Creates the StopPlace input type with all required fields.
     *
     * @param commonInputFieldsList Common input fields for entities
     * @param topographicPlaceInputObjectType TopographicPlace input type
     * @param quayObjectInputType Quay input type
     * @param validBetweenInputObjectType ValidBetween input type
     * @return Configured StopPlace input type
     */
    private GraphQLInputObjectType createStopPlaceInputObjectType(
            List<GraphQLInputObjectField> commonInputFieldsList,
            GraphQLInputObjectType topographicPlaceInputObjectType,
            GraphQLInputObjectType quayObjectInputType,
            GraphQLInputObjectType validBetweenInputObjectType) {

        return newInputObject()
                .name(INPUT_TYPE_STOPPLACE)
                .fields(commonInputFieldsList)
                .fields(servicesRegistry.getTransportModeScalar().createTransportModeInputFieldsList())
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
