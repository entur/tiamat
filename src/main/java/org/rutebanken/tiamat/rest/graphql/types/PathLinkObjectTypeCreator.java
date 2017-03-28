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
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Component
public class PathLinkObjectTypeCreator {

    public Integer getSeconds(Duration duration) {
        if(duration != null) {
            return Math.toIntExact(duration.getSeconds());
        }
        return null;
    }

    public GraphQLObjectType create(GraphQLObjectType pathLinkEndObjecttype, GraphQLFieldDefinition netexIdFieldDefinition, GraphQLFieldDefinition geometryFieldDefinition) {

        DataFetcher durationSecondsFetcher = (env) -> {

            if (env.getSource() != null) {
                TransferDuration transferDuration = (TransferDuration) env.getSource();

                switch (env.getFields().get(0).getName()) {
                    case DEFAULT_DURATION:
                        return getSeconds(transferDuration.getDefaultDuration());
                    case OCCASIONAL_TRAVELLER_DURATION:
                        return getSeconds(transferDuration.getOccasionalTravellerDuration());
                    case MOBILITY_RESTRICTED_TRAVELLER_DURATION:
                        return getSeconds(transferDuration.getMobilityRestrictedTravellerDuration());
                    case FREQUENT_TRAVELLER_DURATION:
                        return getSeconds(transferDuration.getFrequentTravellerDuration());
                    default:
                        return null;
                }
            }
            return null;
        };

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
                                        .dataFetcher(durationSecondsFetcher)
                                        .description(DEFAULT_DURATION_DESCRIPTION))
                                .field(newFieldDefinition()
                                        .name(FREQUENT_TRAVELLER_DURATION)
                                        .type(GraphQLInt)
                                        .dataFetcher(durationSecondsFetcher)
                                        .description(FREQUENT_TRAVELLER_DURATION_DESCRIPTION))
                                .field(newFieldDefinition()
                                        .name(OCCASIONAL_TRAVELLER_DURATION)
                                        .type(GraphQLInt)
                                        .dataFetcher(durationSecondsFetcher)
                                        .description(OCCASIONAL_TRAVELLER_DURATION_DESCRIPTION))
                                .field(newFieldDefinition()
                                        .name(MOBILITY_RESTRICTED_TRAVELLER_DURATION)
                                        .type(GraphQLInt)
                                        .dataFetcher(durationSecondsFetcher)
                                        .description(MOBILITY_RESTRICTED_TRAVELLER_DURATION_DESCRIPTION))
                                .build()))
                .build();
    }
}
