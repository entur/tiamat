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

package org.rutebanken.tiamat.rest.graphql.factories;

import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLType;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.*;

/**
 * Factory for creating GroupOfStopPlaces input GraphQL type.
 * This is used for mutations (create/update) of group of stop places.
 */
@Component
public class GroupOfStopPlacesInputTypeFactory implements GraphQLTypeFactory {

    @Override
    public List<GraphQLType> createTypes() {
        return List.of(createGroupOfStopPlacesInputObjectType());
    }

    @Override
    public String getFactoryName() {
        return "GroupOfStopPlacesInputTypeFactory";
    }

    /**
     * Creates the input object type for GroupOfStopPlaces.
     */
    private GraphQLInputObjectType createGroupOfStopPlacesInputObjectType() {
        return newInputObject()
                .name(INPUT_TYPE_GROUP_OF_STOPPLACES)
                .field(newInputObjectField()
                        .name(ID)
                        .type(GraphQLString)
                        .description("Ignore ID when creating new"))
                .field(newInputObjectField()
                        .name(NAME)
                        .type(new GraphQLNonNull(embeddableMultiLingualStringInputObjectType)))
                .field(newInputObjectField()
                        .name(SHORT_NAME)
                        .type(embeddableMultiLingualStringInputObjectType))
                .field(newInputObjectField()
                        .name(DESCRIPTION)
                        .type(embeddableMultiLingualStringInputObjectType))
                .field(newInputObjectField()
                        .name(ALTERNATIVE_NAMES)
                        .type(new GraphQLList(alternativeNameInputObjectType)))
                .field(newInputObjectField()
                        .name(VERSION_COMMENT)
                        .type(GraphQLString))
                .field(newInputObjectField()
                        .name(PURPOSE_OF_GROUPING)
                        .type(versionLessRefInputObjectType)
                        .description("References to purpose of grouping"))
                .field(newInputObjectField()
                        .name(GROUP_OF_STOP_PLACES_MEMBERS)
                        .description("References to group of stop places members. Stop place IDs.")
                        .type(new GraphQLList(versionLessRefInputObjectType)))
                .build();
    }
}