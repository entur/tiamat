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
import org.rutebanken.tiamat.rest.graphql.fetchers.KeyValuesDataFetcher;
import org.rutebanken.tiamat.rest.graphql.fetchers.OriginalIdsDataFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.*;

@Component
public class ZoneCommonFieldListCreator {

    private static final Logger logger = LoggerFactory.getLogger(ZoneCommonFieldListCreator.class);

    @Autowired
    private OriginalIdsDataFetcher originalIdsDataFetcher;

    @Autowired
    private KeyValuesDataFetcher keyValuesDataFetcher;

    public List<GraphQLFieldDefinition> create() {

        List<GraphQLFieldDefinition> zoneFieldList = new ArrayList<>();
        zoneFieldList.add(netexIdFieldDefinition);
        zoneFieldList.add(newFieldDefinition().name(NAME).type(embeddableMultilingualStringObjectType).build());
        zoneFieldList.add(newFieldDefinition().name(SHORT_NAME).type(embeddableMultilingualStringObjectType).build());
        zoneFieldList.add(newFieldDefinition().name(DESCRIPTION).type(embeddableMultilingualStringObjectType).build());
        zoneFieldList.add(newFieldDefinition().name(VERSION).type(GraphQLString).build());
        zoneFieldList.add(geometryFieldDefinition);

        zoneFieldList.add(newFieldDefinition()
                .name(IMPORTED_ID)
                .deprecate("Moved to keyValues")
                .type(new GraphQLList(GraphQLString))
                .dataFetcher(originalIdsDataFetcher)
                .build());

        zoneFieldList.add(newFieldDefinition()
                .name(KEY_VALUES)
                .type(new GraphQLList(keyValuesObjectType))
                .dataFetcher(keyValuesDataFetcher)
                .build());

        return zoneFieldList;
    }
}
