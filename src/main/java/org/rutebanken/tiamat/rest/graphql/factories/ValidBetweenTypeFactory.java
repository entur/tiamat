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
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import org.rutebanken.tiamat.rest.graphql.scalars.DateScalar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

/**
 * Factory for creating ValidBetween GraphQL types (both output and input types).
 * ValidBetween represents temporal validity periods for entities.
 */
@Component
public class ValidBetweenTypeFactory implements GraphQLTypeFactory {

    @Autowired
    private DateScalar dateScalar;

    @Override
    public List<GraphQLType> createTypes() {
        return List.of(
                createValidBetweenObjectType(),
                createValidBetweenInputObjectType()
        );
    }

    @Override
    public String getFactoryName() {
        return "ValidBetweenTypeFactory";
    }

    /**
     * Creates the output object type for ValidBetween.
     */
    private GraphQLObjectType createValidBetweenObjectType() {
        return newObject()
                .name(OUTPUT_TYPE_VALID_BETWEEN)
                .field(newFieldDefinition()
                        .name(VALID_BETWEEN_FROM_DATE)
                        .type(dateScalar.getGraphQLDateScalar())
                        .description(DATE_SCALAR_DESCRIPTION))
                .field(newFieldDefinition()
                        .name(VALID_BETWEEN_TO_DATE)
                        .type(dateScalar.getGraphQLDateScalar())
                        .description(DATE_SCALAR_DESCRIPTION))
                .build();
    }

    /**
     * Creates the input object type for ValidBetween.
     */
    private GraphQLInputObjectType createValidBetweenInputObjectType() {
        return newInputObject()
                .name(INPUT_TYPE_VALID_BETWEEN)
                .field(newInputObjectField()
                        .name(VALID_BETWEEN_FROM_DATE)
                        .type(new GraphQLNonNull(dateScalar.getGraphQLDateScalar()))
                        .description("When the new version is valid from"))
                .field(newInputObjectField()
                        .name(VALID_BETWEEN_TO_DATE)
                        .type(dateScalar.getGraphQLDateScalar())
                        .description("When the version is no longer valid"))
                .build();
    }
}