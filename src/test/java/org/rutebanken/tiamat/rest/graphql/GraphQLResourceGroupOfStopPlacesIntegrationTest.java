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
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.PurposeOfGrouping;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MUTATE_GROUP_OF_STOP_PLACES;

public class GraphQLResourceGroupOfStopPlacesIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

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
        var versionComment = "VersionComment";

        Double lon =  Double.valueOf("10.111");
        Double lat = Double.valueOf("59.111");

        Instant startDate = Instant.now();
        Instant endDate = startDate.plus(20, ChronoUnit.DAYS);

        String graphQlJsonQuery = """
                                    mutation {
                                    group: %s(GroupOfStopPlaces: {
                                        name: {value: "%s"},
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
                                  }
                                }
                                """.formatted(
                                                MUTATE_GROUP_OF_STOP_PLACES,
                                                groupName,
                                                purposeOfGrouping.getNetexId(),
                                                versionComment,
                                                startDate.toString(),
                                                endDate.toString(),
                                                lon,
                                                lat,
                                                stopPlace1.getNetexId(),
                                                stopPlace2.getNetexId()
                                              );

        final var timeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSZ")
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.of("Europe/Oslo"));

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
                    .body("version", equalTo(String.valueOf(stopPlace1.getVersion())));
    }
}
