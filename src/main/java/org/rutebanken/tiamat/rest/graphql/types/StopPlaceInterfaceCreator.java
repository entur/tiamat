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

import graphql.schema.*;
import org.rutebanken.tiamat.model.StopPlace;
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
    private DataFetcher<List<StopPlace>> stopPlaceGroupsFetcher;

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
        stopPlaceInterfaceFields.add(newFieldDefinition()
                .name(STOP_PLACE_GROUPS)
                .type(new GraphQLList(new GraphQLTypeReference(GROUP_OF_STOP_PLACES)))
                .dataFetcher(stopPlaceGroupsFetcher)
                .build());
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
