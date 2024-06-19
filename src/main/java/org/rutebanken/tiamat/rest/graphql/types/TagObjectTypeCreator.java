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
import org.rutebanken.tiamat.rest.graphql.scalars.DateScalar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_TAG;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAG_COMMENT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAG_COMMENT_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAG_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAG_ID_REFERENCE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAG_ID_REFERENCE_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAG_NAME_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAG_REMOVED_BY_USER_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAG_REMOVED_DESCRIPTION;

@Component
public class TagObjectTypeCreator {

    @Autowired
    private DateScalar dateScalar;

    private GraphQLObjectType graphQLObjectType = null;

    public GraphQLObjectType create() {
        if (graphQLObjectType == null) {
            graphQLObjectType = newObject()
                    .name(OUTPUT_TYPE_TAG)
                    .description(TAG_DESCRIPTION)
                    .field(newFieldDefinition()
                            .name(NAME)
                            .type(GraphQLString)
                            .description(TAG_NAME_DESCRIPTION))
                    .field(newFieldDefinition()
                            .name(TAG_ID_REFERENCE)
                            .type(GraphQLString)
                            .description(TAG_ID_REFERENCE_DESCRIPTION))
                    .field(newFieldDefinition()
                            .name("created")
                            .description("When this tag was added to the referenced entity")
                            .type(dateScalar.getGraphQLDateScalar()))
                    .field(newFieldDefinition()
                            .name("createdBy")
                            .description("Who created this tag for the referenced entity")
                            .type(GraphQLString))
                    .field(newFieldDefinition()
                            .name(TAG_COMMENT)
                            .type(GraphQLString)
                            .description(TAG_COMMENT_DESCRIPTION))
                    .field(newFieldDefinition()
                            .name("removed")
                            .description(TAG_REMOVED_DESCRIPTION)
                            .type(dateScalar.getGraphQLDateScalar()))
                    .field(newFieldDefinition()
                            .name("removedBy")
                            .description(TAG_REMOVED_BY_USER_DESCRIPTION)
                            .type(GraphQLString))
                    .build();
        }
        return graphQLObjectType;
    }
}
