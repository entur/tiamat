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

package org.rutebanken.tiamat.rest.graphql

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Point
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.rutebanken.tiamat.changelog.EntityChangedEvent
import org.rutebanken.tiamat.changelog.EntityChangedJMSListener
import org.rutebanken.tiamat.model.*
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper
import org.rutebanken.tiamat.service.stopplace.MultiModalStopPlaceEditor
import org.rutebanken.tiamat.time.ExportTimeZone
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import java.time.Instant
import java.time.format.DateTimeFormatter

import static org.assertj.core.api.Assertions.assertThat
import static org.hamcrest.Matchers.*
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*
import static org.rutebanken.tiamat.rest.graphql.operations.MultiModalityOperationsBuilder.INPUT
import static org.rutebanken.tiamat.rest.graphql.scalars.DateScalar.DATE_TIME_PATTERN

def class GraphQLResourceGroupOfStopPlacesIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Autowired
    private ExportTimeZone exportTimeZone

    @Test
    void "Create new group of stop places"() {
        def stopPlace1 = new StopPlace()
        stopPlace1.setCentroid(geometryFactory.createPoint(new Coordinate(12, 53)))
        stopPlace1.setStopPlaceType(StopTypeEnumeration.BUS_STATION)
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace1)

        def stopPlace2 = new StopPlace()
        stopPlace2.setCentroid(geometryFactory.createPoint(new Coordinate(13, 61)))
        stopPlace2.setStopPlaceType(StopTypeEnumeration.TRAM_STATION)
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace2)

        def groupName = "Group name"
        def versionComment = "VersionComment"

        def graphQlJsonQuery = """mutation {
                                    group: ${MUTATE_GROUP_OF_STOP_PLACES}(GroupOfStopPlaces: {
                                        name: {value: "${groupName}"},
                                        versionComment: "${versionComment}",
                                        members: [
                                            {ref: "${stopPlace1.getNetexId()}"},
                                            {ref: "${stopPlace2.getNetexId()}"}],
                                        }) {
                                    id
                                    version
                                    name {
                                      value
                                    }
                                    members {
                                      id
                                      name {
                                        value
                                      }
                                      version
                                    }
                                  }
                                }
                                """

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.group.name.value", equalTo(groupName))
                .body("data.group.versionComment", equalTo(versionComment))
                .root("data.group.members.find { it.id == '" + stopPlace2.getNetexId() + "'}")
                    .body("version", equalTo(String.valueOf(stopPlace2.getVersion()+1)))
                    .body("stopPlaceType", equalTo(StopTypeEnumeration.TRAM_STATION.value()))
                    .body("name", nullValue())
                .root("data.group.members.find { it.id == '" + stopPlace1.getNetexId() + "'}")
                    .body("name", nullValue())
                    .body("stopPlaceType", equalTo(StopTypeEnumeration.BUS_STATION.value()))
                    .body("version", equalTo(String.valueOf(stopPlace1.getVersion()+1)))
    }
}
