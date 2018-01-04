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

import org.junit.Test
import org.rutebanken.tiamat.model.EmbeddableMultilingualString
import org.rutebanken.tiamat.model.TariffZone
import org.rutebanken.tiamat.repository.TariffZoneRepository
import org.springframework.beans.factory.annotation.Autowired

import static org.hamcrest.Matchers.equalTo

class GraphQLResourceTariffZoneIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Autowired
    private TariffZoneRepository tariffZoneRepository

    @Test
    void searchForTariffZone() throws Exception {

        def tariffZone = new TariffZone()
        tariffZone.netexId = "BRA:TariffZone:112"
        tariffZone.name = new EmbeddableMultilingualString("Somewhere")
        tariffZone.version = 1L
        tariffZoneRepository.save(tariffZone)

        String graphQlJsonQuery = """
                        {
                            tariffZones(query:"112") {
                                id
                                name {value}
                                version
                          }
                        }"""

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .root("data.tariffZones[0]")
                .body("name.value", equalTo(tariffZone.name.value))
                .body("id", equalTo(tariffZone.netexId))
                .body("version", equalTo(Long.toString(tariffZone.version)))
    }

}
