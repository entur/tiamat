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
import org.springframework.stereotype.Component;

import java.util.List;

import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_TARIFF_ZONE;

/**
 * Factory for creating TariffZone GraphQL output type.
 * TariffZone represents fare zones for public transport pricing.
 */
@Component
public class TariffZoneTypeFactory implements GraphQLTypeFactory {

    @Override
    public List<GraphQLType> createTypes() {
        // Cannot create type without zone common fields.
        // Use createTariffZoneType(zoneCommonFieldList) instead.
        throw new UnsupportedOperationException(
                "TariffZone requires zone common field list. " +
                "Use createTariffZoneType(List<GraphQLFieldDefinition>) instead.");
    }

    @Override
    public String getFactoryName() {
        return "TariffZoneTypeFactory";
    }

    /**
     * Creates the output object type for TariffZone with zone common fields.
     *
     * @param zoneCommonFieldList the common fields for zones (id, name, geometry, etc.)
     * @return the TariffZone GraphQL output type
     */
    public GraphQLObjectType createTariffZoneType(List<GraphQLFieldDefinition> zoneCommonFieldList) {
        return newObject()
                .name(OUTPUT_TYPE_TARIFF_ZONE)
                .fields(zoneCommonFieldList)
                .build();
    }
}