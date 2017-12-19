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

package org.rutebanken.tiamat.repository;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.exporter.params.TariffZoneSearch;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TariffZone;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TariffZoneRepositoryImplTest extends TiamatIntegrationTest {


    @Test
    public void findTariffZonesByName() throws Exception {

        TariffZone tariffZone2V = new TariffZone();
        tariffZone2V.setName(new EmbeddableMultilingualString("2V"));
        tariffZone2V.setNetexId("RUT:TariffZone:2V");

        TariffZone tariffZone412 = new TariffZone();
        tariffZone412.setNetexId("BRA:TariffZone:412");
        tariffZone412.setName(new EmbeddableMultilingualString("Kongsberg"));


        tariffZoneRepository.save(tariffZone2V);
        tariffZoneRepository.save(tariffZone412);

        TariffZoneSearch search = TariffZoneSearch.newTariffZoneSearchBuilder()
                .query("Kongsberg")
                .build();

        List<TariffZone> tariffZoneList = tariffZoneRepository.findTariffZones(search);

        assertThat(tariffZoneList)
                .hasSize(1)
                .extracting(TariffZone::getNetexId)
                .containsOnly(tariffZone412.getNetexId());
    }

    @Test
    public void findTariffZonesById() throws Exception {

        TariffZone tariffZone2V = new TariffZone();
        tariffZone2V.setName(new EmbeddableMultilingualString("2V"));
        tariffZone2V.setNetexId("RUT:TariffZone:2V");

        TariffZone tariffZone412 = new TariffZone();
        tariffZone412.setNetexId("BRA:TariffZone:412");
        tariffZone412.setName(new EmbeddableMultilingualString("Kongsberg"));


        tariffZoneRepository.save(tariffZone2V);
        tariffZoneRepository.save(tariffZone412);

        TariffZoneSearch search = TariffZoneSearch.newTariffZoneSearchBuilder()
                .query(tariffZone412.getNetexId())
                .build();

        List<TariffZone> tariffZoneList = tariffZoneRepository.findTariffZones(search);

        assertThat(tariffZoneList)
                .hasSize(1)
                .extracting(TariffZone::getNetexId)
                .containsOnly(tariffZone412.getNetexId());
    }

    @Test
    public void findTariffZonesByIdSuffix() throws Exception {

        TariffZone tariffZone2V = new TariffZone();
        tariffZone2V.setName(new EmbeddableMultilingualString("2V"));
        tariffZone2V.setNetexId("RUT:TariffZone:2V");

        TariffZone tariffZone412 = new TariffZone();
        tariffZone412.setNetexId("BRA:TariffZone:412");
        tariffZone412.setName(new EmbeddableMultilingualString("Kongsberg"));

        tariffZoneRepository.save(tariffZone2V);
        tariffZoneRepository.save(tariffZone412);

        TariffZoneSearch search = TariffZoneSearch.newTariffZoneSearchBuilder()
                .query(tariffZone2V.getName().getValue())
                .build();

        List<TariffZone> tariffZoneList = tariffZoneRepository.findTariffZones(search);

        assertThat(tariffZoneList)
                .hasSize(1)
                .extracting(TariffZone::getNetexId)
                .containsOnly(tariffZone2V.getNetexId());
    }

    @Test
    public void findTariffZonesByIdPrefix() throws Exception {

        TariffZone tariffZone2V = new TariffZone();
        tariffZone2V.setName(new EmbeddableMultilingualString("2V"));
        tariffZone2V.setNetexId("RUT:TariffZone:2V");

        TariffZone tariffZone412 = new TariffZone();
        tariffZone412.setNetexId("BRA:TariffZone:412");
        tariffZone412.setName(new EmbeddableMultilingualString("Kongsberg"));

        tariffZoneRepository.save(tariffZone2V);
        tariffZoneRepository.save(tariffZone412);

        TariffZoneSearch search = TariffZoneSearch.newTariffZoneSearchBuilder()
                .query("RUT")
                .build();

        List<TariffZone> tariffZoneList = tariffZoneRepository.findTariffZones(search);

        assertThat(tariffZoneList)
                .hasSize(1)
                .extracting(TariffZone::getNetexId)
                .containsOnly(tariffZone2V.getNetexId());
    }

}