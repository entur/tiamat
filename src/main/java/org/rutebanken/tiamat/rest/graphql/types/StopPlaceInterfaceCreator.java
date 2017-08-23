package org.rutebanken.tiamat.rest.graphql.types;

import graphql.schema.*;
import org.rutebanken.tiamat.rest.graphql.fetchers.StopPlaceTariffZoneFetcher;
import org.rutebanken.tiamat.rest.graphql.fetchers.TagFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInterfaceType.newInterface;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ALTERNATIVE_NAMES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TARIFF_ZONES;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.alternativeNameObjectType;

@Component
public class StopPlaceInterfaceCreator {

    @Autowired
    private StopPlaceTariffZoneFetcher stopPlaceTariffZoneFetcher;

    @Autowired
    private TagObjectTypeCreator tagObjectTypeCreator;

    @Autowired
    private TagFetcher tagFetcher;

    public List<GraphQLFieldDefinition> createCommonInterfaceFields(GraphQLObjectType tariffZoneObjectType,
                                                              GraphQLObjectType topographicPlaceObjectType,
                                                              GraphQLObjectType validBetweenObjectType) {
        List<GraphQLFieldDefinition> stopPlaceInterfaceFields = new ArrayList<>();
        stopPlaceInterfaceFields.add(newFieldDefinition()
                .name(VERSION_COMMENT)
                .type(GraphQLString)
                .build());
        stopPlaceInterfaceFields.add(newFieldDefinition()
                .name(CHANGED_BY)
                .type(GraphQLString).build());
        stopPlaceInterfaceFields.add(newFieldDefinition()
                .name(TOPOGRAPHIC_PLACE)
                .type(topographicPlaceObjectType).build());
        stopPlaceInterfaceFields.add(newFieldDefinition()
                .name(VALID_BETWEEN)
                .type(validBetweenObjectType).build());

        stopPlaceInterfaceFields.add(newFieldDefinition()
                .name(ALTERNATIVE_NAMES)
                .type(new GraphQLList(alternativeNameObjectType))
                .build());
        stopPlaceInterfaceFields.add(newFieldDefinition()
                .name(TARIFF_ZONES)
                .type(new GraphQLList(tariffZoneObjectType))
                .dataFetcher(stopPlaceTariffZoneFetcher)
                .build());
        stopPlaceInterfaceFields.add(newFieldDefinition()
                .name(TAGS)
                .type(new GraphQLList(tagObjectTypeCreator.create()))
                .dataFetcher(tagFetcher).build());
        return stopPlaceInterfaceFields;
    }


    public GraphQLInterfaceType createInterface(List<GraphQLFieldDefinition> stopPlaceInterfaceFields,
                                                List<GraphQLFieldDefinition> commonFieldsList,
                                                TypeResolver stopPlaceTypeResolver) {
        return newInterface()
                .name(OUTPUT_TYPE_STOPPLACE_INTERFACE)
                .fields(commonFieldsList)
                .fields(stopPlaceInterfaceFields)
                .typeResolver(stopPlaceTypeResolver)
                .build();
    }

}
