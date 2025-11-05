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
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_ADDRESSABLE_PLACE;

/**
 * Factory for creating AddressablePlace GraphQL output type.
 * AddressablePlace represents a place that can be addressed/referenced.
 */
@Component
public class AddressablePlaceTypeFactory implements GraphQLTypeFactory {

    @Autowired
    private CommonFieldsFactory commonFieldsFactory;

    @Override
    public List<GraphQLType> createTypes() {
        // This method creates types without the full common field list.
        // Use createAddressablePlaceType(commonFieldsList) instead when you need the merged common fields.
        List<GraphQLFieldDefinition> commonOutputFields = commonFieldsFactory.createCommonOutputFieldList();
        return List.of(createAddressablePlaceObjectType(commonOutputFields));
    }

    /**
     * Creates AddressablePlace type with a fully merged common field list.
     * This is the preferred method when calling from the main schema.
     *
     * @param commonFieldsList the merged common field list including zone fields
     * @return the AddressablePlace GraphQL output type
     */
    public GraphQLObjectType createAddressablePlaceType(List<GraphQLFieldDefinition> commonFieldsList) {
        return createAddressablePlaceObjectType(commonFieldsList);
    }

    @Override
    public String getFactoryName() {
        return "AddressablePlaceTypeFactory";
    }

    /**
     * Creates the output object type for AddressablePlace.
     */
    private GraphQLObjectType createAddressablePlaceObjectType(List<GraphQLFieldDefinition> commonFieldsList) {
        return newObject()
                .name(OUTPUT_TYPE_ADDRESSABLE_PLACE)
                .fields(commonFieldsList)
                .build();
    }
}