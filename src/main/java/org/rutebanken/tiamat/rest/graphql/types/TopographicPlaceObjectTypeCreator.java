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

import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_TOPOGRAPHIC_PLACE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARENT_TOPOGRAPHIC_PLACE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POLYGON;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TOPOGRAPHIC_PLACE_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VERSION;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.embeddableMultilingualStringObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.geoJsonObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.geometryFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.topographicPlaceTypeEnum;

@Component
public class TopographicPlaceObjectTypeCreator {

    public GraphQLObjectType create() {
        return newObject()
                .name(OUTPUT_TYPE_TOPOGRAPHIC_PLACE)
                .field(newFieldDefinition()
                        .name(ID)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(TOPOGRAPHIC_PLACE_TYPE)
                        .type(topographicPlaceTypeEnum))
                .field(newFieldDefinition()
                        .name(NAME)
                        .type(embeddableMultilingualStringObjectType))
                .field(newFieldDefinition()
                        .name(VERSION)
                        .type(GraphQLInt))

                .field(newFieldDefinition()
                        .name(PARENT_TOPOGRAPHIC_PLACE)
                        .type(new GraphQLTypeReference(OUTPUT_TYPE_TOPOGRAPHIC_PLACE)))
                .field(newFieldDefinition()
                        .name(POLYGON)
                        .type(geoJsonObjectType))
                .field(geometryFieldDefinition)
                .build();
    }

}
