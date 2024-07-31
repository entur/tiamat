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
import org.rutebanken.tiamat.exporter.params.FareZoneSearch;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.model.ValidBetween;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FareZoneRepositoryImplTest extends TiamatIntegrationTest {


    @Test
    public void findFareZonesByName() throws Exception {

        FareZone fareZone2V = new FareZone();
        fareZone2V.setName(new EmbeddableMultilingualString("2V"));
        fareZone2V.setNetexId("RUT:FareZone:2V");

        FareZone fareZone412 = new FareZone();
        fareZone412.setNetexId("BRA:FareZone:412");
        fareZone412.setName(new EmbeddableMultilingualString("Kongsberg"));


        fareZoneRepository.save(fareZone2V);
        fareZoneRepository.save(fareZone412);

        FareZoneSearch search = FareZoneSearch.newFareZoneSearchBuilder()
                .query("Kongsberg")
                .build();

        List<FareZone> fareZoneList = fareZoneRepository.findFareZones(search);

        assertThat(fareZoneList)
                .hasSize(1)
                .extracting(FareZone::getNetexId)
                .containsOnly(fareZone412.getNetexId());
    }

    @Test
    public void findFareZonesById() throws Exception {

        FareZone fareZone2V = new FareZone();
        fareZone2V.setName(new EmbeddableMultilingualString("2V"));
        fareZone2V.setNetexId("RUT:FareZone:2V");

        FareZone fareZone412 = new FareZone();
        fareZone412.setNetexId("BRA:FareZone:412");
        fareZone412.setName(new EmbeddableMultilingualString("Kongsberg"));


        fareZoneRepository.save(fareZone2V);
        fareZoneRepository.save(fareZone412);

        FareZoneSearch search = FareZoneSearch.newFareZoneSearchBuilder()
                .query(fareZone412.getNetexId())
                .build();

        List<FareZone> fareZoneList = fareZoneRepository.findFareZones(search);

        assertThat(fareZoneList)
                .hasSize(1)
                .extracting(FareZone::getNetexId)
                .containsOnly(fareZone412.getNetexId());
    }

    @Test
    public void findFareZonesByIdSuffix() throws Exception {

        FareZone fareZone2V = new FareZone();
        fareZone2V.setName(new EmbeddableMultilingualString("2V"));
        fareZone2V.setNetexId("RUT:FareZone:2V");

        FareZone fareZone412 = new FareZone();
        fareZone412.setNetexId("BRA:FareZone:412");
        fareZone412.setName(new EmbeddableMultilingualString("Kongsberg"));

        fareZoneRepository.save(fareZone2V);
        fareZoneRepository.save(fareZone412);

        FareZoneSearch search = FareZoneSearch.newFareZoneSearchBuilder()
                .query(fareZone2V.getName().getValue())
                .build();

        List<FareZone> fareZoneList = fareZoneRepository.findFareZones(search);

        assertThat(fareZoneList)
                .hasSize(1)
                .extracting(FareZone::getNetexId)
                .containsOnly(fareZone2V.getNetexId());
    }

    @Test
    public void findFareZonesByIdPrefix() throws Exception {

        FareZone fareZone2V = new FareZone();
        fareZone2V.setName(new EmbeddableMultilingualString("2V"));
        fareZone2V.setNetexId("RUT:FareZone:2V");

        FareZone fareZone412 = new FareZone();
        fareZone412.setNetexId("BRA:FareZone:412");
        fareZone412.setName(new EmbeddableMultilingualString("Kongsberg"));

        fareZoneRepository.save(fareZone2V);
        fareZoneRepository.save(fareZone412);

        FareZoneSearch search = FareZoneSearch.newFareZoneSearchBuilder()
                .query("RUT")
                .build();

        List<FareZone> fareZoneList = fareZoneRepository.findFareZones(search);

        assertThat(fareZoneList)
                .hasSize(1)
                .extracting(FareZone::getNetexId)
                .containsOnly(fareZone2V.getNetexId());
    }

    @Test
    public void getFareZonesFromStopPlaceIds() throws Exception {

        String fareZoneNetexId = "CRI:FareZone:1";

        FareZone v1 = new FareZone();
        v1.setVersion(1L);
        v1.setNetexId(fareZoneNetexId);
        var zonedDateTime = ZonedDateTime.of(2020, 12, 01, 00, 00, 00, 000, ZoneId.systemDefault());
        Instant fromDate = zonedDateTime.toInstant();
        v1.setValidBetween(new ValidBetween(fromDate,null));

        fareZoneRepository.save(v1);

        FareZone v2 = new FareZone();
        v2.setVersion(2L);
        v2.setNetexId(fareZoneNetexId);
        var zonedDateTime2 = zonedDateTime.plusDays(10L);
        var fromDate2 = zonedDateTime2.toInstant();
        v2.setValidBetween(new ValidBetween(fromDate2,null));
        fareZoneRepository.save(v2);

        StopPlace stopPlace = new StopPlace();

        stopPlace.getTariffZones().add(new TariffZoneRef(fareZoneNetexId));
        stopPlaceRepository.save(stopPlace);

        List<FareZone> fareZones = fareZoneRepository.getFareZonesFromStopPlaceIds(Sets.newHashSet(stopPlace.getId()));

        assertThat(fareZones).hasSize(1);
        assertThat(fareZones.getFirst().getVersion()).isEqualTo(v2.getVersion());
    }

}