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

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.tiamat.exception.HSLErrorCodeEnumeration;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.PurposeOfGrouping;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopPlaceReference;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.ValidBetween;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MUTATE_GROUP_OF_STOP_PLACES;

public class GraphQLResourceGroupOfStopPlacesIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    private final static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSZ")
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.of("Europe/Oslo"));

    @Test
    public void create_new_group_of_stop_places() {
        var purposeOfGrouping = new PurposeOfGrouping();
        purposeOfGrouping.setNetexId("NSR:PurposeOfGrouping:1");
        purposeOfGrouping.setVersion(1L);
        purposeOfGrouping.setName(new EmbeddableMultilingualString("Purpose of grouping"));
        purposeOfGroupingRepository.save(purposeOfGrouping);

        var stopPlace1 = new StopPlace();
        stopPlace1.setCentroid(geometryFactory.createPoint(new Coordinate(12, 53)));
        stopPlace1.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace1);

        var stopPlace2 = new StopPlace();
        stopPlace2.setCentroid(geometryFactory.createPoint(new Coordinate(13, 61)));
        stopPlace2.setStopPlaceType(StopTypeEnumeration.TRAM_STATION);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace2);

        var groupName = "Group name";
        var groupSweName = "Gruppens namn";
        var versionComment = "VersionComment";

        Double lon =  Double.valueOf("10.111");
        Double lat = Double.valueOf("59.111");

        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(20, ChronoUnit.DAYS);

        String graphQlJsonQuery = """
                                    mutation {
                                    group: %s(GroupOfStopPlaces: {
                                        name: {value: "%s"},
                                        alternativeNames: [{
                                            name: { lang: "swe", value: "%s" },
                                            nameType: translation
                                        }],
                                        purposeOfGrouping: {ref: "%s"},
                                        versionComment: "%s",
                                        validBetween: {
                                            fromDate: "%s",
                                            toDate: "%s",
                                        },
                                        geometry: {
                                            type: Point,
                                            coordinates: [ %f, %f ]
                                        },
                                        members: [
                                            {ref: "%s"},
                                            {ref: "%s"}
                                        ]
                                    }) {
                                    id
                                    version
                                    versionComment
                                    name {
                                      value
                                    }
                                    validBetween {
                                      fromDate
                                      toDate
                                    }
                                    geometry {
                                      type
                                      coordinates
                                    }
                                    members {
                                      id
                                      name {
                                        value
                                      }
                                      version
                                      ... on StopPlace {
                                        stopPlaceType
                                      }
                                    }
                                    alternativeNames {
                                      name { lang, value }
                                      nameType
                                    }
                                  }
                                }
                                """.formatted(
                                                MUTATE_GROUP_OF_STOP_PLACES,
                                                groupName,
                                                groupSweName,
                                                purposeOfGrouping.getNetexId(),
                                                versionComment,
                                                startDate.toString(),
                                                endDate.toString(),
                                                lon,
                                                lat,
                                                stopPlace1.getNetexId(),
                                                stopPlace2.getNetexId()
                                              );

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.group.name.value", equalTo(groupName))
                .body("data.group.versionComment", equalTo(versionComment))
                .rootPath("data.group.validBetween")
                    .body("fromDate", equalTo(timeFormatter.format(startDate)))
                    .body("toDate", equalTo(timeFormatter.format(endDate)))
                .rootPath("data.group.geometry")
                    .body("type", equalTo("Point"))
                    .body("coordinates", notNullValue())
                .rootPath("data.group.members.find { it.id == '" + stopPlace2.getNetexId() + "'}")
                    .body("version", equalTo(String.valueOf(stopPlace2.getVersion())))
                    .body("stopPlaceType", equalTo(StopTypeEnumeration.TRAM_STATION.value()))
                    .body("name", nullValue())
                .rootPath("data.group.members.find { it.id == '" + stopPlace1.getNetexId() + "'}")
                    .body("name", nullValue())
                    .body("stopPlaceType", equalTo(StopTypeEnumeration.BUS_STATION.value()))
                    .body("version", equalTo(String.valueOf(stopPlace1.getVersion())))
                .rootPath("data.group.alternativeNames.find { it.nameType == 'translation'}")
                    .body("name.lang", equalTo("swe"))
                    .body("name.value", equalTo(groupSweName));
    }

    @Test
    public void groupOfStopPlacesErrorCodeTest() {
        var stopPlace1 = new StopPlace();
        stopPlace1.setCentroid(geometryFactory.createPoint(new Coordinate(12, 53)));
        stopPlace1.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace1);

        var stopPlace2 = new StopPlace();
        stopPlace2.setCentroid(geometryFactory.createPoint(new Coordinate(13, 61)));
        stopPlace2.setStopPlaceType(StopTypeEnumeration.TRAM_STATION);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace2);

        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(20, ChronoUnit.DAYS);

        var groupName = "conflictingName";

        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces(new EmbeddableMultilingualString(groupName));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace1.getNetexId()));
        groupOfStopPlaces.setValidBetween(new ValidBetween(startDate, endDate));
        groupOfStopPlacesRepository.save(groupOfStopPlaces);

        String graphQlJsonQuery = """
                mutation {
                    group: %s(GroupOfStopPlaces: {
                        name: {value: "%s"},
                        validBetween: {
                            fromDate: "%s",
                            toDate: "%s",
                        },
                        members: [
                            {ref: "%s"}
                        ]
                    }) {
                    id
                    name {
                      value
                    }
                  }
                }
                """.formatted(
                MUTATE_GROUP_OF_STOP_PLACES,
                groupName,
                startDate.toString(),
                endDate.toString(),
                stopPlace2.getNetexId()
        );

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("errors[0].extensions.errorCode", equalTo(HSLErrorCodeEnumeration.GROUP_OF_STOP_PLACES_UNIQUE_NAME.name()));
    }
    @Test
    public void groupOfStopPlacesAllowSameNameAgainTest() {
        var stopPlace1 = new StopPlace();
        stopPlace1.setCentroid(geometryFactory.createPoint(new Coordinate(12, 53)));
        stopPlace1.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlace1 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace1);

        var stopPlace2 = new StopPlace();
        stopPlace2.setCentroid(geometryFactory.createPoint(new Coordinate(13, 61)));
        stopPlace2.setStopPlaceType(StopTypeEnumeration.TRAM_STATION);
        stopPlace2 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace2);

        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(20, ChronoUnit.DAYS);

        var groupName = "allowedName";
        var groupDescription = "allowedDescription";

        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces(new EmbeddableMultilingualString(groupName));
        groupOfStopPlaces.setDescription(new EmbeddableMultilingualString(groupDescription));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace1.getNetexId()));
        groupOfStopPlaces.setValidBetween(new ValidBetween(startDate, endDate));
        groupOfStopPlaces = groupOfStopPlacesRepository.save(groupOfStopPlaces);

        String graphQlJsonQuery = """
                mutation {
                    group: %s(GroupOfStopPlaces: {
                        id: "%s"
                        name: {value: "%s"},
                        description: {value: "%s"},
                        validBetween: {
                            fromDate: "%s",
                            toDate: "%s",
                        },
                        members: [
                            {ref: "%s"},
                            {ref: "%s"}
                        ]
                    }) {
                    id
                    name {
                      value
                    }
                    description {
                      value
                    }
                  }
                }
                """.formatted(
                MUTATE_GROUP_OF_STOP_PLACES,
                groupOfStopPlaces.getNetexId(),
                groupName,
                groupDescription,
                startDate.toString(),
                endDate.toString(),
                stopPlace1.getNetexId(),
                stopPlace2.getNetexId()
        );

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.group")
                .body("name.value", equalTo(groupName))
                .body("description.value", equalTo(groupDescription));
    }

    @Test
    public void update_group_of_stop_places() {
        var purposeOfGrouping = new PurposeOfGrouping();
        purposeOfGrouping.setNetexId("NSR:PurposeOfGrouping:1");
        purposeOfGrouping.setVersion(1L);
        purposeOfGrouping.setName(new EmbeddableMultilingualString("Purpose of grouping"));
        purposeOfGroupingRepository.save(purposeOfGrouping);

        var stopPlace1 = new StopPlace();
        stopPlace1.setCentroid(geometryFactory.createPoint(new Coordinate(12, 53)));
        stopPlace1.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace1);

        var stopPlace2 = new StopPlace();
        stopPlace2.setCentroid(geometryFactory.createPoint(new Coordinate(13, 61)));
        stopPlace2.setStopPlaceType(StopTypeEnumeration.TRAM_STATION);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace2);

        var groupName = "Group name";
        var groupSweName = "Gruppens namn";
        var aliasSweName = "Namnet för gruppen";
        var versionComment = "VersionComment";

        Double lon =  Double.valueOf("10.111");
        Double lat = Double.valueOf("59.111");

        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(20, ChronoUnit.DAYS);

        String insertQuery = """
                                mutation {
                                    group: %s(GroupOfStopPlaces: {
                                        name: {value: "%s"},
                                        alternativeNames: [{
                                            name: { lang: "swe", value: "%s" },
                                            nameType: translation
                                        }],
                                        purposeOfGrouping: {ref: "%s"},
                                        versionComment: "%s",
                                        validBetween: {
                                            fromDate: "%s",
                                            toDate: "%s",
                                        },
                                        geometry: {
                                            type: Point,
                                            coordinates: [ %f, %f ]
                                        },
                                        members: [{ref: "%s"}]
                                    }) {
                                    id
                                  }
                                }
                                """.formatted(
                MUTATE_GROUP_OF_STOP_PLACES,
                groupName,
                groupSweName,
                purposeOfGrouping.getNetexId(),
                versionComment,
                startDate.toString(),
                endDate.toString(),
                lon,
                lat,
                stopPlace1.getNetexId()
        );

        final String groupId = executeGraphqQLQueryOnly(insertQuery)
                .extract()
                .body()
                .jsonPath()
                .get("data.group.id");

        String updateQuery = """
                                    mutation {
                                    group: %s(GroupOfStopPlaces: {
                                        id: "%s",
                                        name: {value: "%s"},
                                        alternativeNames: [{
                                            name: { lang: "swe", value: "%s" },
                                            nameType: translation
                                        }, {
                                            name: { lang: "swe", value: "%s" },
                                            nameType: alias
                                        }],
                                        members: [
                                            {ref: "%s"},
                                            {ref: "%s"}
                                        ]
                                    }) {
                                    id
                                    version
                                    versionComment
                                    name {
                                      value
                                    }
                                    validBetween {
                                      fromDate
                                      toDate
                                    }
                                    geometry {
                                      type
                                      coordinates
                                    }
                                    members {
                                      id
                                      name {
                                        value
                                      }
                                      version
                                      ... on StopPlace {
                                        stopPlaceType
                                      }
                                    }
                                    alternativeNames {
                                      name { lang, value }
                                      nameType
                                    }
                                  }
                                }
                                """.formatted(
                MUTATE_GROUP_OF_STOP_PLACES,
                groupId,
                groupName,
                groupSweName,
                aliasSweName,
                stopPlace1.getNetexId(),
                stopPlace2.getNetexId()
        );

        executeGraphqQLQueryOnly(updateQuery)
                .body("data.group.name.value", equalTo(groupName))
                .body("data.group.versionComment", equalTo(versionComment))
                .rootPath("data.group.validBetween")
                .body("fromDate", equalTo(timeFormatter.format(startDate)))
                .body("toDate", equalTo(timeFormatter.format(endDate)))
                .rootPath("data.group.geometry")
                .body("type", equalTo("Point"))
                .body("coordinates", notNullValue())
                .rootPath("data.group.members.find { it.id == '" + stopPlace2.getNetexId() + "'}")
                .body("version", equalTo(String.valueOf(stopPlace2.getVersion())))
                .body("stopPlaceType", equalTo(StopTypeEnumeration.TRAM_STATION.value()))
                .body("name", nullValue())
                .rootPath("data.group.members.find { it.id == '" + stopPlace1.getNetexId() + "'}")
                .body("name", nullValue())
                .body("stopPlaceType", equalTo(StopTypeEnumeration.BUS_STATION.value()))
                .body("version", equalTo(String.valueOf(stopPlace1.getVersion())))
                .rootPath("data.group.alternativeNames.find { it.nameType == 'translation'}")
                .body("name.lang", equalTo("swe"))
                .body("name.value", equalTo(groupSweName))
                .rootPath("data.group.alternativeNames.find { it.nameType == 'alias'}")
                .body("name.lang", equalTo("swe"))
                .body("name.value", equalTo(aliasSweName));
    }
}
