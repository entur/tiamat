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
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MUTATE_GROUP_OF_STOP_PLACES;

public class GraphQLResourceGroupOfStopPlacesIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Test
    public void create_new_group_of_stop_places() {
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

        String graphQlJsonQuery = """
                                    mutation {
                                    group: %s(GroupOfStopPlaces: {
                                        name: {value: "%s"},
                                        versionComment: "%s",
                                        members: [
                                            {ref: "%s"},
                                            {ref: "%s"}],
                                        }) {
                                    id
                                    version
                                    versionComment
                                    name {
                                      value
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
                                                versionComment,
                                                stopPlace1.getNetexId(),
                                                stopPlace2.getNetexId()
                                              );

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.group.name.value", equalTo(groupName))
                .body("data.group.versionComment", equalTo(versionComment))
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
