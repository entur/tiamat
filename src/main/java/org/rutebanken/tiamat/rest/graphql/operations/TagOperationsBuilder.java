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

package org.rutebanken.tiamat.rest.graphql.operations;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNonNull;
import org.rutebanken.tiamat.rest.graphql.factories.TagTypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.CREATE_TAG;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.REMOVE_TAG;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAG_COMMENT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAG_COMMENT_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAG_ID_REFERENCE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAG_ID_REFERENCE_DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAG_NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TAG_NAME_DESCRIPTION;

@Component
public class TagOperationsBuilder {

    @Autowired
    private TagTypeFactory tagTypeFactory;

    public List<GraphQLFieldDefinition> getTagOperations() {

        List<GraphQLArgument> createAndRemoveArguments = Arrays.asList(

                newArgument()
                        .name(TAG_ID_REFERENCE)
                        .type(new GraphQLNonNull(GraphQLString))
                        .description(TAG_ID_REFERENCE_DESCRIPTION)
                        .build(),

                newArgument()
                        .name(TAG_NAME)
                        .type(new GraphQLNonNull(GraphQLString))
                        .description(TAG_NAME_DESCRIPTION)
                        .build(),
                newArgument()
                        .name(TAG_COMMENT)
                        .type(GraphQLString)
                        .description(TAG_COMMENT_DESCRIPTION)
                        .build());

        return Arrays.asList(
                newFieldDefinition()
                        .type((graphql.schema.GraphQLOutputType) tagTypeFactory.createTypes().getFirst())
                        .name(REMOVE_TAG)
                        .description("Remove tag from referenced entity")
                        .arguments(createAndRemoveArguments)
                        .build(),
                newFieldDefinition()
                        .type((graphql.schema.GraphQLOutputType) tagTypeFactory.createTypes().getFirst())
                        .name(CREATE_TAG)
                        .description("Create tag for referenced entity.")
                        .arguments(createAndRemoveArguments)
                        .build()
        );
    }

}
