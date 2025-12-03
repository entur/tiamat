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


import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInterfaceType.newInterface;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ALTERNATIVE_NAMES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CHANGED_BY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FARE_ZONES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_GROUP_OF_STOPPLACES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_STOPPLACE_INTERFACE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PERMISSIONS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POSTAL_ADDRESS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STOP_PLACE_GROUPS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAGS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TARIFF_ZONES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TOPOGRAPHIC_PLACE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.URL;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALID_BETWEEN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VERSION_COMMENT;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.alternativeNameObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.postalAddressObjectType;

@Component
public class StopPlaceInterfaceCreator {



    @Autowired
    private TagObjectTypeCreator tagObjectTypeCreator;



    public List<GraphQLFieldDefinition> createCommonInterfaceFields(GraphQLObjectType tariffZoneObjectType,
                                                                    GraphQLObjectType fareZoneObjectType,
                                                                    GraphQLObjectType topographicPlaceObjectType,
                                                                    GraphQLObjectType validBetweenObjectType,
                                                                    GraphQLObjectType entityPermissionObjectType) {
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
                .build());

        stopPlaceInterfaceFields.add(newFieldDefinition()
                .name(FARE_ZONES)
                .type(new GraphQLList(fareZoneObjectType))
                .build());
        stopPlaceInterfaceFields.add(newFieldDefinition()
                .name(TAGS)
                .type(new GraphQLList(tagObjectTypeCreator.create()))
                .build());
        stopPlaceInterfaceFields.add(newFieldDefinition()
                .name(STOP_PLACE_GROUPS)
                .type(new GraphQLList(new GraphQLTypeReference(OUTPUT_TYPE_GROUP_OF_STOPPLACES)))
                .build());
        stopPlaceInterfaceFields.add(newFieldDefinition()
                .name(PERMISSIONS)
                .type(entityPermissionObjectType)
                .build());
        stopPlaceInterfaceFields.add(newFieldDefinition()
                .name(URL)
                .type(GraphQLString)
                .build());
        stopPlaceInterfaceFields.add(newFieldDefinition()
                .name(POSTAL_ADDRESS)
                .type(postalAddressObjectType)
                .build());
        return stopPlaceInterfaceFields;
    }


    public GraphQLInterfaceType createInterface(List<GraphQLFieldDefinition> stopPlaceInterfaceFields,
                                                List<GraphQLFieldDefinition> commonFieldsList) {
        return newInterface()
                .name(OUTPUT_TYPE_STOPPLACE_INTERFACE)
                .fields(commonFieldsList)
                .fields(stopPlaceInterfaceFields)
                .build();
    }

}
