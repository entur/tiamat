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
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLType;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.embeddableMultiLingualStringInputObjectType;

/**
 * Factory for creating PurposeOfGrouping input GraphQL type.
 * This is used for mutations (create/update) of purpose of grouping.
 */
@Component
public class PurposeOfGroupingInputTypeFactory implements GraphQLTypeFactory {

    @Override
    public List<GraphQLType> createTypes() {
        return List.of(createPurposeOfGroupingInputObjectType());
    }

    @Override
    public String getFactoryName() {
        return "PurposeOfGroupingInputTypeFactory";
    }

    /**
     * Creates the input object type for PurposeOfGrouping.
     */
    private GraphQLInputObjectType createPurposeOfGroupingInputObjectType() {
        return newInputObject()
                .name(INPUT_TYPE_PURPOSE_OF_GROUPING)
                .field(newInputObjectField()
                        .name(ID)
                        .type(GraphQLString)
                        .description("Ignore ID when creating new"))
                .field(newInputObjectField()
                        .name(NAME)
                        .type(new GraphQLNonNull(embeddableMultiLingualStringInputObjectType)))
                .field(newInputObjectField()
                        .name(DESCRIPTION)
                        .type(embeddableMultiLingualStringInputObjectType))
                .field(newInputObjectField()
                        .name(VERSION_COMMENT)
                        .type(GraphQLString))
                .build();
    }
}