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

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ENTITY_REF_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ENTITY_REF_REF_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_VERSION_LESS_ENTITY_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VERSION_LESS_ENTITY_REF_DESCRIPTION;

public class VersionLessEntityRef {

    public static GraphQLObjectType versionLessEntityRef = newObject()
            .name(OUTPUT_TYPE_VERSION_LESS_ENTITY_REF)
            .description(VERSION_LESS_ENTITY_REF_DESCRIPTION)
            .field(newFieldDefinition()
                    .name(ENTITY_REF_REF)
                    .type(GraphQLString)
                    .description(ENTITY_REF_REF_DESCRIPTION))
            .build();
}
