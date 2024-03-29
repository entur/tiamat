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
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GraphQLResourceTariffZoneIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Autowired
    private TariffZoneRepository tariffZoneRepository;

    @Test
    public void searchForTariffZone() throws Exception {

        var tariffZone = new TariffZone();
        tariffZone.setNetexId("BRA:TariffZone:112");
        tariffZone.setName( new EmbeddableMultilingualString("Somewhere"));
        tariffZone.setVersion(1L);
        Coordinate[] coordinates = Arrays.asList(new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(1, 1), new Coordinate(0, 0)).toArray(new Coordinate[4]);
        tariffZone.setPolygon(new GeometryFactory().createPolygon(coordinates));
        tariffZoneRepository.save(tariffZone);

        String graphQlJsonQuery = """
                        {
                            tariffZones(query:"112") {
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
                .rootPath("data.tariffZones[0]")
                .body("name.value", equalTo(tariffZone.getName().getValue()))
                .body("id", equalTo(tariffZone.getNetexId()))
                .body("version", equalTo(Long.toString(tariffZone.getVersion())))
                .body("polygon",notNullValue());
    }

}
