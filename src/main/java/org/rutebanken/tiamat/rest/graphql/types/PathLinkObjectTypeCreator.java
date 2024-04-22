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

package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import org.rutebanken.tiamat.model.TransferDuration;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.DEFAULT_DURATION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.DEFAULT_DURATION_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FREQUENT_TRAVELLER_DURATION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FREQUENT_TRAVELLER_DURATION_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MOBILITY_RESTRICTED_TRAVELLER_DURATION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MOBILITY_RESTRICTED_TRAVELLER_DURATION_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OCCASIONAL_TRAVELLER_DURATION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OCCASIONAL_TRAVELLER_DURATION_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_PATH_LINK;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_TRANSFER_DURATION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PATH_LINK_FROM;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PATH_LINK_TO;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TRANSFER_DURATION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TRANSFER_DURATION_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VERSION;

@Component
public class PathLinkObjectTypeCreator {

    public Integer getSeconds(Duration duration) {
        if(duration != null) {
            return Math.toIntExact(duration.getSeconds());
        }
        return null;
    }

    public GraphQLObjectType create(GraphQLObjectType pathLinkEndObjecttype, GraphQLFieldDefinition netexIdFieldDefinition, GraphQLFieldDefinition geometryFieldDefinition) {

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

    public DataFetcher durationSecondsFetcher() {

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
