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

import com.google.common.collect.Sets;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.rutebanken.tiamat.auth.MockedRoleAssignmentExtractor;
import org.rutebanken.tiamat.auth.RoleAssignmentListBuilder;
import org.rutebanken.tiamat.model.AddressablePlaceRefStructure;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.DEFAULT_DURATION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FIND_PATH_LINK;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FREQUENT_TRAVELLER_DURATION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MUTATE_PATH_LINK;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TRANSFER_DURATION;
import static org.rutebanken.tiamat.versioning.VersionIncrementor.INITIAL_VERSION;


public class GraphQLResourcePathLinkIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private MockedRoleAssignmentExtractor mockedRoleAssignmentExtractor;

    @Test
    public void retrievePathLinkReferencingTwoQuays() {
        Quay firstQuay = new Quay();
        firstQuay.setCentroid(geometryFactory.createPoint(new Coordinate(5, 60)));
        quayRepository.save(firstQuay);

        Quay secondQuay = new Quay();
        secondQuay.setCentroid(geometryFactory.createPoint(new Coordinate(5.1, 60.1)));
        quayRepository.save(secondQuay);

        PathLink pathLink = new PathLink(new PathLinkEnd(new AddressablePlaceRefStructure((firstQuay))), new PathLinkEnd(new AddressablePlaceRefStructure(secondQuay)));
        Coordinate[] coordinates = new Coordinate[2];
        coordinates[0] = new Coordinate(11, 60);
        coordinates[1] = new Coordinate(11.1, 60.1);

        CoordinateSequence points = new CoordinateArraySequence(coordinates);

        LineString lineString = new LineString(points, geometryFactory);
        pathLink.setLineString(lineString);

        pathLinkRepository.save(pathLink);

        String graphQlJsonQuery = """
                    { %s (id:"%s") {
                    id 
                    from {
                        id
                        placeRef {
                            ref
                            version
                        }
                    }
                    to {
                        id
                        placeRef {
                            ref
                            version
                        }
                    }
                }
              }""".formatted(FIND_PATH_LINK,pathLink.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.pathLink[0]")
                    .body("id", comparesEqualTo(pathLink.getNetexId()))
                .rootPath("data.pathLink[0].from")
                    .body("id", comparesEqualTo(pathLink.getFrom().getNetexId()))
                    .body("placeRef.ref", equalTo(firstQuay.getNetexId()))
                    .body("placeRef.version", equalTo(String.valueOf(firstQuay.getVersion())))
                .rootPath("data.pathLink[0].to")
                    .body("id", comparesEqualTo(pathLink.getTo().getNetexId()))
                    .body("placeRef.ref", equalTo(secondQuay.getNetexId()))
                    .body("placeRef.version", equalTo(String.valueOf(secondQuay.getVersion())));
    }

    @Test
    public void findPathLinkFromStopPlaceId() {

        Quay firstQuay = new Quay();
        firstQuay.setVersion(1L);

        Quay secondQuay = new Quay();
        secondQuay.setVersion(2L);
        secondQuay.setPublicCode("X");

        StopPlace stopPlace = new StopPlace();
        stopPlace.getQuays().add(firstQuay);
        stopPlace.getQuays().add(secondQuay);

        stopPlaceRepository.save(stopPlace);

        PathLink pathLink = new PathLink(new PathLinkEnd(new AddressablePlaceRefStructure(firstQuay)), new PathLinkEnd(new AddressablePlaceRefStructure(secondQuay)));
        pathLinkRepository.save(pathLink);

        String graphQlJsonQuery = """
                    { %s (stopPlaceId: "%s") {
                    id
                    from {
                        id
                        placeRef {
                            ref
                            version
                        }
                    }
                    to {
                      id
                      placeRef {
                        ref
                        version
                      }
                    }
                   }
                  }
                """.formatted(FIND_PATH_LINK,stopPlace.getNetexId());


        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.pathLink[0]")
                   .body("id", comparesEqualTo(pathLink.getNetexId()))
                .rootPath("data.pathLink[0].from")
                    .body("id", comparesEqualTo(pathLink.getFrom().getNetexId()))
                    .body("placeRef.ref", equalTo(firstQuay.getNetexId()))
                    .body("placeRef.version", equalTo(String.valueOf(firstQuay.getVersion())))
                .rootPath("data.pathLink[0].to")
                    .body("id", comparesEqualTo(pathLink.getTo().getNetexId()))
                    .body("placeRef.ref", equalTo(secondQuay.getNetexId()))
                    .body("placeRef.version", equalTo(String.valueOf(secondQuay.getVersion())));

    }


    @Test
    public void createNewPathLinkByUserWithoutAuthorizationForStopPlaceType() {
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(
                RoleAssignmentListBuilder.builder().withStopPlaceOfType(StopTypeEnumeration.BUS_STATION).build());
        PathLinkQuery query = createNewPathLinkQuery(StopTypeEnumeration.FERRY_STOP);
        final ValidatableResponse validatableResponse = executeGraphqQLQueryOnly(query.query, HttpStatus.FORBIDDEN.value());
        Assert.assertNotNull(validatableResponse);
    }

    @Test
    public void createNewPathLinkByUserWithCorrectAuthorizationForStopPlaceType() {
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(
                RoleAssignmentListBuilder.builder().withStopPlaceOfType(StopTypeEnumeration.BUS_STATION).build());
        PathLinkQuery query = createNewPathLinkQuery(StopTypeEnumeration.BUS_STATION);

        ValidatableResponse rsp = executeGraphqQLQueryOnly(query.query);

        rsp
                .rootPath("data.pathLink[0]")
                    .body("id", notNullValue())
                    .body("geometry", notNullValue())
                .rootPath("data.pathLink[0].from")
                    .body("id", notNullValue())
                    .body("placeRef.ref", equalTo(query.from.getNetexId()))
                    .body("placeRef.version", equalTo(String.valueOf(query.from.getVersion())))
                .rootPath("data.pathLink[0].to")
                    .body("id", notNullValue())
                    .body("placeRef.ref", equalTo(query.to.getNetexId()))
                    .body("placeRef.version", is(emptyOrNullString()));
    }


    private PathLinkQuery createNewPathLinkQuery(StopTypeEnumeration stopType) {
        Quay firstQuay = new Quay();
        firstQuay.setCentroid(geometryFactory.createPoint(new Coordinate(5, 60)));
        firstQuay.setVersion(INITIAL_VERSION);

        Quay secondQuay = new Quay();
        secondQuay.setVersion(INITIAL_VERSION + 1);
        secondQuay.setCentroid(geometryFactory.createPoint(new Coordinate(5.1, 60.1)));

        StopPlace stop = new StopPlace();
        stop.setStopPlaceType(stopType);
        stop.setQuays(Sets.newHashSet(firstQuay, secondQuay));
        stopPlaceRepository.save(stop);

        String query = """
                           mutation {
                           pathLink: %s (PathLink: [{
                                from: {placeRef: {ref: "%s", version:"%s"}},
                                    to: {placeRef: {ref: "%s"}}
                                    geometry: {
                                        type: LineString, coordinates: [[10.3, 59.9], [10.3, 59.9], [10.3, 59.9], [10.3, 59.9], [10.3, 59.9]]
                                    }
                                }]) {
                                id
                                geometry {
                                    type
                                    coordinates
                                }
                                from {
                                    id
                                    placeRef {
                                        ref
                                        version
                                    }
                                }
                                to {
                                    id
                                    placeRef {
                                        ref
                                        version
                                    }
                                }
                            }
                           }""".formatted(MUTATE_PATH_LINK,firstQuay.getNetexId(),firstQuay.getVersion(),secondQuay.getNetexId());
        return new PathLinkQuery(firstQuay, secondQuay, query);
    }

    @Test
    public void updatePathLinkWithTransferDurationWithoutClearingLineString() {
        var firstQuay = new Quay();
        firstQuay.setCentroid(geometryFactory.createPoint(new Coordinate(5, 60)));
        firstQuay.setVersion(INITIAL_VERSION);

        var secondQuay = new Quay();
        secondQuay.setVersion(INITIAL_VERSION + 1);
        secondQuay.setCentroid(geometryFactory.createPoint(new Coordinate(5.1, 60.1)));

        var stop = new StopPlace();
        Set<Quay> quaySet = new HashSet<>();
        quaySet.add(firstQuay);
        quaySet.add(secondQuay);
        stop.setQuays(quaySet);
        stopPlaceRepository.save(stop);

        String graphQlJsonQuery = """
                mutation {
                pathLink: %s (PathLink: [{
                    from: {placeRef: {ref: "%s", version:"%s"}},
                    to: {placeRef: {ref: "%s"}},
                    geometry: {
                        type: LineString,
                        coordinates: [[10.3, 59.9], [10.3, 59.9], [10.3, 59.9], [10.3, 59.9], [10.3, 59.9]]
                    }
                    }]) {
                    id
                    geometry {
                        type
                        coordinates
                       }
                    }
                }""".formatted(MUTATE_PATH_LINK,firstQuay.getNetexId(),firstQuay.getVersion(),secondQuay.getNetexId());

        String pathLinkId = executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.pathLink[0]")
                    .body("id", notNullValue())
                    .body("geometry", notNullValue())
                .extract().path("data.pathLink[0].id");

        System.out.println("Got path link ID: " + pathLinkId + ". Will send another mutation were only the transfer duration will be changed");

        String secondGraphQlJsonQuery = """
                    mutation {
                    pathLink: %s (PathLink: {
                        id: "%s",
                        %s: {
                            %s: 1,
                            %s: 2,
                        }
                    }) {
                        id
                        geometry {
                            type
                            coordinates
                        }
                        %s {
                            %s
                            %s
                        }
                    }
                }""".formatted(MUTATE_PATH_LINK,
                pathLinkId,
                TRANSFER_DURATION,
                DEFAULT_DURATION,
                FREQUENT_TRAVELLER_DURATION,
                TRANSFER_DURATION,
                DEFAULT_DURATION,
                FREQUENT_TRAVELLER_DURATION
        );

        executeGraphqQLQueryOnly(secondGraphQlJsonQuery)
                .body("errors", nullValue())
                .rootPath("data.pathLink[0]")
                    .body("id", notNullValue())
                    .body("geometry", notNullValue())
                    .body("transferDuration", notNullValue())
                    .body("transferDuration." + DEFAULT_DURATION, notNullValue())
                    .body("transferDuration." + FREQUENT_TRAVELLER_DURATION, notNullValue())
                .extract().path("id");

    }

    // For reuse of test setup
    private static class PathLinkQuery {
        Quay from;
        Quay to;
        String query;

        PathLinkQuery(Quay from, Quay to, String query) {
            this.from = from;
            this.to = to;
            this.query = query;
        }
    }
}
