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
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import org.rutebanken.tiamat.rest.graphql.scalars.TransportModeScalar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ADJACENT_SITES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ADJACENT_SITES_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_STOPPLACE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARENT_SITE_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.QUAYS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STOP_PLACE_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.WEIGHTING;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.interchangeWeightingEnum;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.stopPlaceTypeEnum;
import static org.rutebanken.tiamat.rest.graphql.types.VersionLessEntityRef.versionLessEntityRef;

/**
 * Factory for creating StopPlace GraphQL output type.
 * StopPlace represents a physical stop location with quays, transport modes, and related metadata.
 */
@Component
public class StopPlaceTypeFactory implements GraphQLTypeFactory {

    @Autowired
    private TransportModeScalar transportModeScalar;

    @Override
    public List<GraphQLType> createTypes() {
        // Cannot create type without dependencies.
        // Use createStopPlaceType(...) instead.
        throw new UnsupportedOperationException(
                "StopPlace requires StopPlaceInterface, field lists, and QuayObjectType. " +
                "Use createStopPlaceType(GraphQLInterfaceType, List, List, GraphQLObjectType) instead.");
    }

    @Override
    public String getFactoryName() {
        return "StopPlaceTypeFactory";
    }

    /**
     * Creates the output object type for StopPlace with required dependencies.
     *
     * @param stopPlaceInterface the StopPlace interface type
     * @param stopPlaceInterfaceFields the fields from the StopPlace interface
     * @param commonFieldsList the common fields shared across types
     * @param quayObjectType the Quay object type
     * @return the StopPlace GraphQL output type
     */
    public GraphQLObjectType createStopPlaceType(
            GraphQLInterfaceType stopPlaceInterface,
            List<GraphQLFieldDefinition> stopPlaceInterfaceFields,
            List<GraphQLFieldDefinition> commonFieldsList,
            GraphQLObjectType quayObjectType) {

        return newObject()
                .name(OUTPUT_TYPE_STOPPLACE)
                .withInterface(stopPlaceInterface)
                .fields(stopPlaceInterfaceFields)
                .fields(commonFieldsList)
                .fields(transportModeScalar.getTransportModeFieldsList())
                .field(newFieldDefinition()
                        .name(STOP_PLACE_TYPE)
                        .type(stopPlaceTypeEnum))
                .field(newFieldDefinition()
                        .name(WEIGHTING)
                        .type(interchangeWeightingEnum))
                .field(newFieldDefinition()
                        .name(PARENT_SITE_REF)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(ADJACENT_SITES)
                        .type(new GraphQLList(versionLessEntityRef))
                        .description(ADJACENT_SITES_DESCRIPTION))
                .field(newFieldDefinition()
                        .name(QUAYS)
                        .type(new GraphQLList(quayObjectType)))
                .build();
    }
}
