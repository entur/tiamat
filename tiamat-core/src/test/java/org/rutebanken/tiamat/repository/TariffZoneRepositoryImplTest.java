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

import com.google.common.collect.Sets;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.exporter.params.TariffZoneSearch;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.model.ValidBetween;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    public void indTariffZonesByNetexIds() {
        TariffZone tariffZone2V = new TariffZone();
        tariffZone2V.setName(new EmbeddableMultilingualString("2V"));
        tariffZone2V.setNetexId("RUT:TariffZone:2V");

        TariffZone tariffZone2S = new TariffZone();
        tariffZone2S.setName(new EmbeddableMultilingualString("2S"));
        tariffZone2S.setNetexId("RUT:TariffZone:2S");

        TariffZone tariffZone412 = new TariffZone();
        tariffZone412.setNetexId("BRA:TariffZone:412");
        tariffZone412.setName(new EmbeddableMultilingualString("Kongsberg"));

        tariffZoneRepository.save(tariffZone2V);
        tariffZoneRepository.save(tariffZone2S);
        tariffZoneRepository.save(tariffZone412);

        final List<String> netexIds = List.of("RUT:TariffZone:2V", "RUT:TariffZone:2S");
        final List<TariffZone> validTariffZones = tariffZoneRepository.findValidTariffZones(netexIds);

        assertThat(validTariffZones)
                .hasSize(2)
                .extracting(TariffZone::getNetexId)
                .containsAll(netexIds);
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

    @Test
    public void getTariffZonesFromStopPlaceIds() throws Exception {

        String tariffZoneNetexId = "CRI:TariffZone:1";

        TariffZone v1 = new TariffZone();
        v1.setVersion(1L);
        v1.setNetexId(tariffZoneNetexId);
        var zonedDateTime = ZonedDateTime.of(2020, 12, 01, 00, 00, 00, 000, ZoneId.systemDefault());
        Instant fromDate = zonedDateTime.toInstant();
        v1.setValidBetween(new ValidBetween(fromDate,null));

        tariffZoneRepository.save(v1);

        TariffZone v2 = new TariffZone();
        v2.setVersion(2L);
        v2.setNetexId(tariffZoneNetexId);
        var zonedDateTime2 = zonedDateTime.plusDays(10L);
        var fromDate2 = zonedDateTime2.toInstant();
        v2.setValidBetween(new ValidBetween(fromDate2,null));
        tariffZoneRepository.save(v2);

        StopPlace stopPlace = new StopPlace();

        stopPlace.getTariffZones().add(new TariffZoneRef(tariffZoneNetexId));
        stopPlaceRepository.save(stopPlace);

        List<TariffZone> tariffZones = tariffZoneRepository.getTariffZonesFromStopPlaceIds(Sets.newHashSet(stopPlace.getId()));

        assertThat(tariffZones).hasSize(1);
        assertThat(tariffZones.getFirst().getVersion()).isEqualTo(v2.getVersion());
    }

}