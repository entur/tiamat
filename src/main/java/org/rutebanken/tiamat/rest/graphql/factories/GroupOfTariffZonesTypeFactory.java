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

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.model.GroupOfTariffZones;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.embeddableMultilingualStringObjectType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.netexIdFieldDefinition;

/**
 * Factory for creating GroupOfTariffZones GraphQL output type.
 * GroupOfTariffZones represents a grouping of fare zones.
 */
@Component
public class GroupOfTariffZonesTypeFactory implements GraphQLTypeFactory {

    @Autowired
    private FareZoneRepository fareZoneRepository;

    @Override
    public List<GraphQLType> createTypes() {
        return List.of(createGroupOfTariffZonesObjectType());
    }

    @Override
    public String getFactoryName() {
        return "GroupOfTariffZonesTypeFactory";
    }

    /**
     * Creates the output object type for GroupOfTariffZones.
     */
    private GraphQLObjectType createGroupOfTariffZonesObjectType() {
        return newObject()
                .name(OUTPUT_TYPE_GROUP_OF_TARIFF_ZONES)
                .field(netexIdFieldDefinition)
                .field(newFieldDefinition()
                        .name(NAME)
                        .type(embeddableMultilingualStringObjectType))
                .field(newFieldDefinition()
                        .name(DESCRIPTION)
                        .type(embeddableMultilingualStringObjectType))
                .field(newFieldDefinition()
                        .name(VERSION)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(VERSION_COMMENT)
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name(GROUP_OF_TARIFF_ZONES_MEMBERS)
                        .type(new GraphQLList(GraphQLTypeReference.typeRef(OUTPUT_TYPE_FARE_ZONE))))
                .build();
    }

    /**
     * Data fetcher for group of tariff zone members.
     * Used in buildCodeRegistry to resolve the members field.
     */
    public List<FareZone> groupOfTariffZoneMembersType(DataFetchingEnvironment env) {
        final Set<TariffZoneRef> members = ((GroupOfTariffZones) env.getSource()).getMembers();
        if (!members.isEmpty()) {
            return members.stream()
                    .map(member -> fareZoneRepository.findFirstByNetexIdOrderByVersionDesc(member.getRef()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}