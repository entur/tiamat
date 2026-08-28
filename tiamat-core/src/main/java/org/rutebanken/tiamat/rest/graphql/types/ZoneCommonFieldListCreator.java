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
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.IMPORTED_ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.KEY_VALUES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POLYGON;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SHORT_NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALID_BETWEEN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VERSION;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.embeddableMultilingualStringObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.geoJsonObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.geometryFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.keyValuesObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.netexIdFieldDefinition;

@Component
public class ZoneCommonFieldListCreator {
    public List<GraphQLFieldDefinition> create(GraphQLObjectType validBetweenObjectType) {

        List<GraphQLFieldDefinition> zoneFieldList = new ArrayList<>();
        zoneFieldList.add(netexIdFieldDefinition);
        zoneFieldList.add(newFieldDefinition().name(NAME).type(embeddableMultilingualStringObjectType).build());
        zoneFieldList.add(newFieldDefinition().name(SHORT_NAME).type(embeddableMultilingualStringObjectType).build());
        zoneFieldList.add(newFieldDefinition().name(DESCRIPTION).type(embeddableMultilingualStringObjectType).build());
        zoneFieldList.add(newFieldDefinition().name(VERSION).type(GraphQLString).build());
        zoneFieldList.add(newFieldDefinition().name(VALID_BETWEEN).type(validBetweenObjectType).build());
        zoneFieldList.add(geometryFieldDefinition);

        zoneFieldList.add(newFieldDefinition()
                .name(IMPORTED_ID)
                .deprecate("Moved to keyValues")
                .type(new GraphQLList(GraphQLString))
                .build());

        zoneFieldList.add(newFieldDefinition()
                .name(KEY_VALUES)
                .type(new GraphQLList(keyValuesObjectType))
                .build());

        zoneFieldList.add(newFieldDefinition()
                .name(POLYGON)
                .type(geoJsonObjectType)
                .build());

        return zoneFieldList;
    }
}
