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
import graphql.schema.GraphQLObjectType;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_STOPPLACE_CHANGELOG;

@Component
public class StopPlaceChangelogObjectTypeCreator {

    public GraphQLObjectType create(List<GraphQLFieldDefinition> stopPlaceChangelogFieldList) {
        return newObject()
                .name(OUTPUT_TYPE_STOPPLACE_CHANGELOG)
                .fields(stopPlaceChangelogFieldList)
                .build();
    }

}
