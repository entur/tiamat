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

package org.rutebanken.tiamat.rest.graphql;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.model.ScopingMethodEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopPlaceReference;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GraphQLResourceFareZoneIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Autowired
    private FareZoneRepository fareZoneRepository;

    @Test
    public void searchForFareZone() throws Exception {

        var fareZone = new FareZone();
        fareZone.setNetexId("BRA:FareZone:112");
        fareZone.setName( new EmbeddableMultilingualString("Somewhere"));
        fareZone.setVersion(1L);
        Coordinate[] coordinates = Arrays.asList(new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(1, 1), new Coordinate(0, 0)).toArray(new Coordinate[4]);
        fareZone.setPolygon(new GeometryFactory().createPolygon(coordinates));
        fareZoneRepository.save(fareZone);

        String graphQlJsonQuery = """
                        {
                            fareZones(query:"112") {
                                id
                                name {value}
                                version
                                    geometry {
                                        type
                                        coordinates
                                    }
                                    polygon {
                                        type
                                        coordinates
                                    }
                          }
                        }""";

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.fareZones[0]")
                .body("name.value", equalTo(fareZone.getName().getValue()))
                .body("id", equalTo(fareZone.getNetexId()))
                .body("version", equalTo(Long.toString(fareZone.getVersion())))
                .body("polygon",notNullValue());
    }

    @Test
    public void testFareZoneWithScopingMethodExplicitStops() {
        StopPlace stopPlace1 = new StopPlace(new EmbeddableMultilingualString("StopPlace1"));
        stopPlaceRepository.save(stopPlace1);
        StopPlace stopPlace2 = new StopPlace(new EmbeddableMultilingualString("StopPlace2"));
        stopPlaceRepository.save(stopPlace2);
        var fareZone = new FareZone();
        fareZone.setNetexId("BRA:FareZone:112");
        fareZone.setName( new EmbeddableMultilingualString("Somewhere"));
        fareZone.setVersion(1L);
        fareZone.setTransportOrganisationRef("BRA:TransportOrganisation:1");
        fareZone.setScopingMethod(ScopingMethodEnumeration.EXPLICIT_STOPS);

        var neighbourFareZone1 = new FareZone();
        neighbourFareZone1.setNetexId("BRA:FareZone:113");
        fareZoneRepository.save(neighbourFareZone1);

        var neighbourFareZone2 = new FareZone();
        neighbourFareZone2.setNetexId("BRA:FareZone:114");
        fareZoneRepository.save(neighbourFareZone2);

        Set<TariffZoneRef> neighbours = new HashSet<>();
        var neighbour1 = new TariffZoneRef();
        neighbour1.setRef(neighbourFareZone1.getNetexId());
        var neighbour2 = new TariffZoneRef();
        neighbour2.setRef(neighbourFareZone2.getNetexId());
        neighbours.add(neighbour1);
        neighbours.add(neighbour2);
        fareZone.setNeighbours(neighbours);

        Set< StopPlaceReference> members= new HashSet<>();
        var member1 =new StopPlaceReference(stopPlace1.getNetexId());
        members.add(member1);
        var member2 =new StopPlaceReference(stopPlace2.getNetexId());
        members.add(member2);
        fareZone.setFareZoneMembers(members);
        fareZoneRepository.save(fareZone);

        String graphQlJsonQuery = """
                        {
                            fareZones(query:"112") {
                                id
                                name {value}
                                version
                                authorityRef
                                scopingMethod
                                members {
                                    id
                                }
                                neighbours {
                                    id
                                }
                          }
                        }""";

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.fareZones[0]")
                .body("name.value", equalTo(fareZone.getName().getValue()))
                .body("id", equalTo(fareZone.getNetexId()))
                .body("version", equalTo(Long.toString(fareZone.getVersion())))
                .body("scopingMethod", equalTo(fareZone.getScopingMethod().value()))
                .body("authorityRef", equalTo(fareZone.getTransportOrganisationRef()))
                .body("members.id", containsInAnyOrder(stopPlace1.getNetexId(), stopPlace2.getNetexId()))
                .body("neighbours.id", containsInAnyOrder(neighbourFareZone1.getNetexId(), neighbourFareZone2.getNetexId()));

    }

}
