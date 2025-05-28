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

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopPlaceReference;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FARE_ZONES_AUTHORITY_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FARE_ZONES_MEMBERS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FARE_ZONES_NEIGHBOURS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FARE_ZONES_SCOPING_METHOD;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FARE_ZONES_ZONE_TOPOLOGY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_FARE_ZONE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_STOPPLACE;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.privateCodeFieldDefinition;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.scopingMethodEnumType;
import static org.rutebanken.tiamat.rest.graphql.types.CustomGraphQLTypes.zoneTopologyEnumType;


@Component
public class FareZoneObjectTypeCreator {


    private final StopPlaceRepository stopPlaceRepository;
    private final FareZoneRepository fareZoneRepository;


    public FareZoneObjectTypeCreator(StopPlaceRepository stopPlaceRepository,
                                     FareZoneRepository fareZoneRepository) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.fareZoneRepository = fareZoneRepository;
    }

    public GraphQLObjectType create(List<GraphQLFieldDefinition> zoneCommonFieldList) {

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

    public List<FareZone> fareZoneNeighboursType(DataFetchingEnvironment env) {
        final Set<TariffZoneRef> neighbours = ((FareZone) env.getSource()).getNeighbours();
        if (!neighbours.isEmpty()) {
            return neighbours.stream()
                    .map(neighbour -> fareZoneRepository.findFirstByNetexIdOrderByVersionDesc(neighbour.getRef()))
                    .toList();
        }
        return Collections.emptyList();

    }

    public List<StopPlace> fareZoneMemberType(DataFetchingEnvironment env) {
        final Set<StopPlaceReference> fareZoneMembers = ((FareZone) env.getSource()).getFareZoneMembers();
        if (!fareZoneMembers.isEmpty()) {
            return fareZoneMembers.stream()
                    .map(member -> stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(member.getRef()))
                    .toList();
        }
        return Collections.emptyList();

    }
}
