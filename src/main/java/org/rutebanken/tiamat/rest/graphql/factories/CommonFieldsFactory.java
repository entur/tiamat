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

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.*;

/**
 * Factory for creating common field lists shared across multiple GraphQL types.
 * These field lists are used by Quays, StopPlaces, and AddressablePlaces.
 */
@Component
public class CommonFieldsFactory {

    /**
     * Creates common input field list used by Quay, StopPlace, and AddressablePlace input types.
     *
     * @param embeddableMultiLingualStringInputObjectType the multilingual string input type
     * @return list of common input fields
     */
    public List<GraphQLInputObjectField> createCommonInputFieldList(
            GraphQLInputObjectType embeddableMultiLingualStringInputObjectType) {

        List<GraphQLInputObjectField> commonInputFieldsList = new ArrayList<>();
        commonInputFieldsList.add(newInputObjectField()
                .name(ID)
                .type(GraphQLString)
                .description("Ignore when creating new")
                .build());
        commonInputFieldsList.add(newInputObjectField()
                .name(NAME)
                .type(embeddableMultiLingualStringInputObjectType)
                .build());
        commonInputFieldsList.add(newInputObjectField()
                .name(SHORT_NAME)
                .type(embeddableMultiLingualStringInputObjectType)
                .build());
        commonInputFieldsList.add(newInputObjectField()
                .name(PUBLIC_CODE)
                .type(GraphQLString)
                .build());
        commonInputFieldsList.add(newInputObjectField()
                .name(PRIVATE_CODE)
                .type(privateCodeInputType)
                .build());
        commonInputFieldsList.add(newInputObjectField()
                .name(DESCRIPTION)
                .type(embeddableMultiLingualStringInputObjectType)
                .build());
        commonInputFieldsList.add(newInputObjectField()
                .name(GEOMETRY)
                .type(geoJsonInputType)
                .build());
        commonInputFieldsList.add(newInputObjectField()
                .name(ALTERNATIVE_NAMES)
                .type(new GraphQLList(alternativeNameInputObjectType))
                .build());
        commonInputFieldsList.add(newInputObjectField()
                .name(PLACE_EQUIPMENTS)
                .type(equipmentInputType)
                .build());
        commonInputFieldsList.add(newInputObjectField()
                .name(KEY_VALUES)
                .type(new GraphQLList(keyValuesObjectInputType))
                .build());
        commonInputFieldsList.add(
                newInputObjectField()
                        .name(ACCESSIBILITY_ASSESSMENT)
                        .description("This field is set either on StopPlace (i.e. all Quays are equal), or on every Quay.")
                        .type(accessibilityAssessmentInputObjectType)
                        .build()
        );
        return commonInputFieldsList;
    }

    /**
     * Creates common output field list used by Quay, StopPlace, and AddressablePlace object types.
     *
     * @return list of common output fields
     */
    public List<GraphQLFieldDefinition> createCommonOutputFieldList() {
        List<GraphQLFieldDefinition> commonFieldsList = new ArrayList<>();
        commonFieldsList.add(newFieldDefinition()
                .name(PLACE_EQUIPMENTS)
                .type(equipmentType)
                .build());
        commonFieldsList.add(newFieldDefinition()
                .name(ACCESSIBILITY_ASSESSMENT)
                .description("This field is set either on StopPlace (i.e. all Quays are equal), or on every Quay.")
                .type(accessibilityAssessmentObjectType)
                .build()
        );
        commonFieldsList.add(newFieldDefinition()
                .name(PUBLIC_CODE)
                .type(GraphQLString)
                .build());
        commonFieldsList.add(privateCodeFieldDefinition);
        commonFieldsList.add(
                newFieldDefinition()
                        .name(MODIFICATION_ENUMERATION)
                        .type(modificationEnumerationType)
                        .build()
        );
        return commonFieldsList;
    }
}