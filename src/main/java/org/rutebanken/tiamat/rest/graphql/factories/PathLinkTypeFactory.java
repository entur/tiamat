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

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import org.rutebanken.tiamat.model.TransferDuration;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

/**
 * Factory for creating PathLink GraphQL output type.
 * PathLink represents a connection between two places with transfer duration information.
 */
@Component
public class PathLinkTypeFactory implements GraphQLTypeFactory {

    @Override
    public List<GraphQLType> createTypes() {
        // Cannot create type without dependencies.
        // Use createPathLinkType(GraphQLObjectType, GraphQLFieldDefinition, GraphQLFieldDefinition) instead.
        throw new UnsupportedOperationException(
                "PathLink requires PathLinkEnd object type, netexId field, and geometry field. " +
                "Use createPathLinkType(GraphQLObjectType, GraphQLFieldDefinition, GraphQLFieldDefinition) instead.");
    }

    @Override
    public String getFactoryName() {
        return "PathLinkTypeFactory";
    }

    /**
     * Creates the output object type for PathLink with required dependencies.
     *
     * @param pathLinkEndObjecttype the PathLinkEnd object type
     * @param netexIdFieldDefinition the netexId field definition
     * @param geometryFieldDefinition the geometry field definition
     * @return the PathLink GraphQL output type
     */
    public GraphQLObjectType createPathLinkType(
            GraphQLObjectType pathLinkEndObjecttype,
            GraphQLFieldDefinition netexIdFieldDefinition,
            GraphQLFieldDefinition geometryFieldDefinition) {

        return newObject()
                .name(OUTPUT_TYPE_PATH_LINK)
                .field(netexIdFieldDefinition)
                .field(newFieldDefinition()
                        .name(PATH_LINK_FROM)
                        .type(pathLinkEndObjecttype))
                .field(newFieldDefinition()
                        .name(PATH_LINK_TO)
                        .type(pathLinkEndObjecttype))
                .field(newFieldDefinition()
                        .name(VERSION)
                        .type(GraphQLString))
                .field(geometryFieldDefinition)
                .field(newFieldDefinition()
                        .name(TRANSFER_DURATION)
                        .type(newObject()
                                .name(OUTPUT_TYPE_TRANSFER_DURATION)
                                .description(TRANSFER_DURATION_DESCRIPTION)
                                .field(newFieldDefinition()
                                        .name(DEFAULT_DURATION)
                                        .type(GraphQLInt)
                                        .description(DEFAULT_DURATION_DESCRIPTION))
                                .field(newFieldDefinition()
                                        .name(FREQUENT_TRAVELLER_DURATION)
                                        .type(GraphQLInt)
                                        .description(FREQUENT_TRAVELLER_DURATION_DESCRIPTION))
                                .field(newFieldDefinition()
                                        .name(OCCASIONAL_TRAVELLER_DURATION)
                                        .type(GraphQLInt)
                                        .description(OCCASIONAL_TRAVELLER_DURATION_DESCRIPTION))
                                .field(newFieldDefinition()
                                        .name(MOBILITY_RESTRICTED_TRAVELLER_DURATION)
                                        .type(GraphQLInt)
                                        .description(MOBILITY_RESTRICTED_TRAVELLER_DURATION_DESCRIPTION))
                                .build()))
                .build();
    }

    /**
     * Data fetcher for converting Duration to seconds.
     * Used in buildCodeRegistry to resolve duration fields.
     */
    public Integer getSeconds(Duration duration) {
        if(duration != null) {
            return Math.toIntExact(duration.getSeconds());
        }
        return null;
    }

    /**
     * Data fetcher for transfer duration fields.
     * Used in buildCodeRegistry to resolve duration fields.
     */
    public DataFetcher<Integer> durationSecondsFetcher() {
        return (env) -> {
            if (env.getSource() != null) {
                TransferDuration transferDuration = (TransferDuration) env.getSource();

                return switch (env.getMergedField().getFields().getFirst().getName()) {
                    case DEFAULT_DURATION -> getSeconds(transferDuration.getDefaultDuration());
                    case OCCASIONAL_TRAVELLER_DURATION -> getSeconds(transferDuration.getOccasionalTravellerDuration());
                    case MOBILITY_RESTRICTED_TRAVELLER_DURATION -> getSeconds(transferDuration.getMobilityRestrictedTravellerDuration());
                    case FREQUENT_TRAVELLER_DURATION -> getSeconds(transferDuration.getFrequentTravellerDuration());
                    default -> null;
                };
            }
            return null;
        };
    }
}