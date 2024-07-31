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
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.model.GroupOfTariffZones;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

public class GraphQLResourceGroupOfTariffZonesIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Autowired
    private FareZoneRepository fareZoneRepository;

    @Test
    public void testGroupOfTariffZones() throws Exception {

        var groupOfTariffZones = new GroupOfTariffZones();
        groupOfTariffZones.setNetexId("BRA:GroupOfTariffZones:112");
        groupOfTariffZones.setName(new EmbeddableMultilingualString("GroupOfTariffZones"));
        groupOfTariffZones.setVersion(1L);
        groupOfTariffZones.setResponsibilitySetRef("BRA:ResponsibilitySet:1");

        var members1 = new FareZone();
        members1.setNetexId("BRA:FareZone:113");
        fareZoneRepository.save(members1);

        var members2 = new FareZone();
        members2.setNetexId("BRA:FareZone:114");
        fareZoneRepository.save(members2);

        Set<TariffZoneRef> tariffZoneRefs = new HashSet<>();
        tariffZoneRefs.add(new TariffZoneRef(members1.getNetexId()));
        tariffZoneRefs.add(new TariffZoneRef(members2.getNetexId()));

        groupOfTariffZones.getMembers().addAll(tariffZoneRefs);

        groupOfTariffZonesRepository.save(groupOfTariffZones);

        String graphQlJsonQuery = """
                        {
                            groupOfTariffZones(id:"BRA:GroupOfTariffZones:112") {
                                id
                                name {value}
                                version
                                members {
                                    id
                                }
                          }
                        }""";

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .rootPath("data.groupOfTariffZones[0]")
                .body("name.value", equalTo(groupOfTariffZones.getName().getValue()))
                .body("id", equalTo(groupOfTariffZones.getNetexId()))
                .body("version", equalTo(Long.toString(groupOfTariffZones.getVersion())))
                .body("members.id", containsInAnyOrder(members1.getNetexId(), members2.getNetexId()));
    }

}
