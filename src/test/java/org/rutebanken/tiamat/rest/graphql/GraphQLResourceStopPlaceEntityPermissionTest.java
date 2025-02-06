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
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.hasSize;

public class GraphQLResourceStopPlaceEntityPermissionTest extends AbstractGraphQLResourceIntegrationTest {

    @Test
    public void noAuthStopPlacesTest() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));


        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);


        String graphQlJsonQuery = """
                  {
                   stopPlace(id:"%s") {
                            id
                            name {value}
                            version
                            permissions {
                                canEdit
                                canDelete
                                allowedSubmodes
                                bannedSubmodes
                                allowedStopPlaceTypes
                                bannedStopPlaceTypes
                              }
                        }
                   }""".formatted(stopPlace.getNetexId());



        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].permissions.canEdit", comparesEqualTo(false))
                .body("data.stopPlace[0].permissions.canDelete", comparesEqualTo(false))
                .body("data.stopPlace[0].permissions.allowedSubmodes", hasSize(0))
                .body("data.stopPlace[0].permissions.bannedSubmodes", hasSize(1))
                .body("data.stopPlace[0].permissions.allowedStopPlaceTypes", hasSize(0))
                .body("data.stopPlace[0].permissions.bannedStopPlaceTypes", hasSize(1))
                .body("data.stopPlace[0].permissions.bannedStopPlaceTypes[0]", comparesEqualTo("*"))
                .body("data.stopPlace[0].permissions.bannedSubmodes[0]", comparesEqualTo("*"));
    }
}
