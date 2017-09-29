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
import graphql.schema.GraphQLObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Component
public class EntityRefObjectTypeCreator {

    @Autowired
    private DataFetcher referenceFetcher;

    public GraphQLObjectType create(GraphQLObjectType addressablePlaceObjectType) {
        return newObject()
                .name(OUTPUT_TYPE_ENTITY_REF)
                .description(ENTITY_REF_DESCRIPTION)
                .field(newFieldDefinition()
                        .name(ENTITY_REF_REF)
                        .type(GraphQLString))
                        .description(ENTITY_REF_REF_DESCRIPTION)
                .field(newFieldDefinition()
                        .name(ENTITY_REF_VERSION)
                        .type(GraphQLString))
                        .description(ENTITY_REF_VERSION_DESCRIPTION)
                .field(newFieldDefinition()
                        .name(ADDRESSABLE_PLACE)
                        .type(addressablePlaceObjectType)
                        .description("")
                        .dataFetcher(referenceFetcher))
                .build();
    }
}
