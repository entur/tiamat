package org.rutebanken.tiamat.rest.graphql;

import com.google.common.collect.Sets;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.rutebanken.tiamat.auth.MockedRoleAssignmentExtractor;
import org.rutebanken.tiamat.auth.RoleAssignmentListBuilder;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.DEFAULT_DURATION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FREQUENT_TRAVELLER_DURATION;
import static org.rutebanken.tiamat.versioning.VersionIncrementor.INITIAL_VERSION;

public class GraphQLResourcePathLinkIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLResourcePathLinkIntegrationTest.class);

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private MockedRoleAssignmentExtractor mockedRoleAssignmentExtractor;

    @Test
    void retrievePathLinkReferencingTwoQuays() throws Exception {
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
                { ${GraphQLNames.FIND_PATH_LINK} (id:"${pathLink.getNetexId()}")
                    {
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
                """;

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .root("data.pathLink[0]")
                .body("id", comparesEqualTo(pathLink.getNetexId()))
                .root("data.pathLink[0].from")
                .body("id", comparesEqualTo(pathLink.getFrom().getNetexId()))
                .body("placeRef.ref", equalTo(firstQuay.getNetexId()))
                .body("placeRef.version", equalTo(String.valueOf(firstQuay.getVersion())))
                .root("data.pathLink[0].to")
                .body("id", comparesEqualTo(pathLink.getTo().getNetexId()))
                .body("placeRef.ref", equalTo(secondQuay.getNetexId()))
                .body("placeRef.version", equalTo(String.valueOf(secondQuay.getVersion())));
    }

    @Test
    void findPathLinkFromStopPlaceId() throws Exception {

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
                { ${GraphQLNames.FIND_PATH_LINK} (stopPlaceId: "${stopPlace.getNetexId()}") {
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
                """;

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .root("data.pathLink[0]")
                .body("id", comparesEqualTo(pathLink.getNetexId()))
                .root("data.pathLink[0].from")
                .body("id", comparesEqualTo(pathLink.getFrom().getNetexId()))
                .body("placeRef.ref", equalTo(firstQuay.getNetexId()))
                .body("placeRef.version", equalTo(String.valueOf(firstQuay.getVersion())))
                .root("data.pathLink[0].to")
                .body("id", comparesEqualTo(pathLink.getTo().getNetexId()))
                .body("placeRef.ref", equalTo(secondQuay.getNetexId()))
                .body("placeRef.version", equalTo(String.valueOf(secondQuay.getVersion())));
    }

    @Test
    void createNewPathLinkByUserWithoutAuthorizationForStopPlaceType() throws Exception {
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(
                RoleAssignmentListBuilder.builder().withStopPlaceOfType(StopTypeEnumeration.BUS_STATION).build());
        PathLinkQuery query = createNewPathLinkQuery(StopTypeEnumeration.FERRY_STOP);
        executeGraphQLQueryOnly(query.query, HttpStatus.FORBIDDEN.value());
    }

    @Test
    void createNewPathLinkByUserWithCorrectAuthorizationForStopPlaceType() throws Exception {
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(
                RoleAssignmentListBuilder.builder().withStopPlaceOfType(StopTypeEnumeration.BUS_STATION).build());
        PathLinkQuery query = createNewPathLinkQuery(StopTypeEnumeration.BUS_STATION);

        ValidatableResponse rsp = executeGraphQLQueryOnly(query.query);

        rsp
                .root("data.pathLink[0]")
                .body("id", notNullValue())
                .body("geometry", notNullValue())
                .root("data.pathLink[0].from")
                .body("id", notNullValue())
                .body("placeRef.ref", equalTo(query.from.getNetexId()))
                .body("placeRef.version", equalTo(String.valueOf(query.from.getVersion())))
                .root("data.pathLink[0].to")
                .body("id", notNullValue())
                .body("placeRef.ref", equalTo(query.to.getNetexId()))
                .body("placeRef.version", isEmptyOrNullString());
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
                           pathLink: ${MUTATE_PATH_LINK} (PathLink: [{
                                from: {placeRef: {ref: "${firstQuay.getNetexId()}", version:"${firstQuay.getVersion()}"}},
                                    to: {placeRef: {ref: "${secondQuay.getNetexId()}"}}
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
                       }
                       """;
        return new PathLinkQuery(firstQuay, secondQuay, query);
    }

    @Test
    void updatePathLinkWithTransferDurationWithoutClearingLineString() throws Exception {
        var firstQuay = new Quay();
        firstQuay.setCentroid(geometryFactory.createPoint(new Coordinate(5, 60)));
        firstQuay.setVersion(INITIAL_VERSION);

        var secondQuay = new Quay();
        secondQuay.setVersion(INITIAL_VERSION + 1);
        secondQuay.setCentroid(geometryFactory.createPoint(new Coordinate(5.1, 60.1)));

        var stop = new StopPlace();
        stop.setQuays(Set.of(firstQuay, secondQuay));
        stopPlaceRepository.save(stop);

        String graphQlJsonQuery = """
            mutation {
                pathLink: ${MUTATE_PATH_LINK} (PathLink: [{
                    from: {placeRef: {ref: "${firstQuay.getNetexId()}", version:"${firstQuay.getVersion()}"}},
                    to: {placeRef: {ref: "${secondQuay.getNetexId()}"}},
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
                }
                """;

        String pathLinkId = executeGraphQLQueryOnly(graphQlJsonQuery)
                .root("data.pathLink[0]")
                .body("id", notNullValue())
                .body("geometry", notNullValue())
                .extract().path("data.pathLink[0].id");

        LOGGER.debug("Got path link ID: " + pathLinkId + ". Will send another mutation were only the transfer duration will be changed");

        String secondGraphQlJsonQuery = """
                    mutation {
                    pathLink: ${MUTATE_PATH_LINK} (PathLink: {
                        id: "${pathLinkId}",
                        ${TRANSFER_DURATION}: {
                            ${varAULT_DURATION}: 1,
                            ${FREQUENT_TRAVELLER_DURATION}: 2,
                        }
                    }) {
                        id
                        geometry {
                            type
                            coordinates
                        }
                        ${TRANSFER_DURATION} {
                            ${varAULT_DURATION}
                            ${FREQUENT_TRAVELLER_DURATION}
                        }
                    }
                }
                """;

        executeGraphQLQueryOnly(secondGraphQlJsonQuery)
                .body("errors", nullValue())
                .root("data.pathLink[0]")
                .body("id", notNullValue())
                .body("geometry", notNullValue())
                .body("transferDuration", notNullValue())
                .body("transferDuration." + DEFAULT_DURATION, notNullValue())
                .body("transferDuration." + FREQUENT_TRAVELLER_DURATION, notNullValue())
                .extract().path("id");

    }

    // For reuse of test setup
    private class PathLinkQuery {
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
