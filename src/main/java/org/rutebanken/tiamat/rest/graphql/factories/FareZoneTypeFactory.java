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
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopPlaceReference;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.privateCodeFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.scopingMethodEnumType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.zoneTopologyEnumType;

/**
 * Factory for creating FareZone GraphQL output type.
 * FareZone represents fare zones for public transport pricing with neighbors and members.
 */
@Component
public class FareZoneTypeFactory implements GraphQLTypeFactory {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private FareZoneRepository fareZoneRepository;

    @Override
    public List<GraphQLType> createTypes() {
        // Cannot create type without zone common fields.
        // Use createFareZoneType(zoneCommonFieldList) instead.
        throw new UnsupportedOperationException(
                "FareZone requires zone common field list. " +
                "Use createFareZoneType(List<GraphQLFieldDefinition>) instead.");
    }

    @Override
    public String getFactoryName() {
        return "FareZoneTypeFactory";
    }

    /**
     * Creates the output object type for FareZone with zone common fields.
     *
     * @param zoneCommonFieldList the common fields for zones (id, name, geometry, etc.)
     * @return the FareZone GraphQL output type
     */
    public GraphQLObjectType createFareZoneType(List<GraphQLFieldDefinition> zoneCommonFieldList) {
        List<GraphQLFieldDefinition> fareZoneFieldList = new ArrayList<>(zoneCommonFieldList);

        fareZoneFieldList.add(newFieldDefinition()
                .name(FARE_ZONES_AUTHORITY_REF)
                .type(GraphQLString)
                .build());
        fareZoneFieldList.add(privateCodeFieldDefinition);
        fareZoneFieldList.add(newFieldDefinition().name(FARE_ZONES_ZONE_TOPOLOGY).type(zoneTopologyEnumType).build());
        fareZoneFieldList.add(newFieldDefinition().name(FARE_ZONES_SCOPING_METHOD).type(scopingMethodEnumType).build());

        fareZoneFieldList.add(newFieldDefinition()
                .name(FARE_ZONES_NEIGHBOURS)
                .type(new GraphQLList(GraphQLTypeReference.typeRef(OUTPUT_TYPE_FARE_ZONE)))
                .build());

        fareZoneFieldList.add(newFieldDefinition()
                .name(FARE_ZONES_MEMBERS)
                .type(new GraphQLList(GraphQLTypeReference.typeRef(OUTPUT_TYPE_STOPPLACE)))
                .build());

        return newObject()
                .name(OUTPUT_TYPE_FARE_ZONE)
                .fields(fareZoneFieldList)
                .build();
    }

    /**
     * Data fetcher for fare zone neighbours.
     * Used in buildCodeRegistry to resolve the neighbours field.
     */
    public List<FareZone> fareZoneNeighboursType(DataFetchingEnvironment env) {
        final Set<TariffZoneRef> neighbours = ((FareZone) env.getSource()).getNeighbours();
        if (!neighbours.isEmpty()) {
            return neighbours.stream()
                    .map(neighbour -> fareZoneRepository.findFirstByNetexIdOrderByVersionDesc(neighbour.getRef()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Data fetcher for fare zone members (stop places).
     * Used in buildCodeRegistry to resolve the members field.
     */
    public List<StopPlace> fareZoneMemberType(DataFetchingEnvironment env) {
        final Set<StopPlaceReference> fareZoneMembers = ((FareZone) env.getSource()).getFareZoneMembers();
        if (!fareZoneMembers.isEmpty()) {
            return fareZoneMembers.stream()
                    .map(member -> stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(member.getRef()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}