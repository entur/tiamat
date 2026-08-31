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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.exporter.params.FareZoneSearch;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.model.ScopingMethodEnumeration;
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
    public void findFareZonesReturnsOnlyCurrentVersionWhenMultipleVersionsExist() {
        String netexId = "RUT:FareZone:MultiVersion";
        Instant past = Instant.now().minusSeconds(3600);

        FareZone v1 = new FareZone();
        v1.setNetexId(netexId);
        v1.setVersion(1L);
        v1.setName(new EmbeddableMultilingualString("Old"));
        v1.setValidBetween(new ValidBetween(past, past.plusSeconds(60)));
        fareZoneRepository.save(v1);

        FareZone v2 = new FareZone();
        v2.setNetexId(netexId);
        v2.setVersion(2L);
        v2.setName(new EmbeddableMultilingualString("Current"));
        v2.setValidBetween(new ValidBetween(past, null));
        fareZoneRepository.save(v2);

        FareZoneSearch search = FareZoneSearch.newFareZoneSearchBuilder().build();
        List<FareZone> result = fareZoneRepository.findFareZones(search);

        assertThat(result)
                .filteredOn(fz -> netexId.equals(fz.getNetexId()))
                .hasSize(1)
                .extracting(FareZone::getVersion)
                .containsOnly(2L);
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

    @Test
    public void updateStopPlaceTariffZoneRef_stopPlaceInsideMultiSurface_shouldAssignFareZone() {
        MultiPolygon multiPolygon = createTwoPolygonMultiSurface();

        FareZone fareZone = new FareZone();
        fareZone.setNetexId("TST:FareZone:MultiSurface2");
        fareZone.setName(new EmbeddableMultilingualString("Implicit Spatial Zone"));
        fareZone.setScopingMethod(ScopingMethodEnumeration.IMPLICIT_SPATIAL_PROJECTION);
        fareZone.setMultiSurface(multiPolygon);
        fareZone.setValidBetween(new ValidBetween(Instant.now().minusSeconds(3600), null));
        fareZoneRepository.saveAndFlush(fareZone);

        // Centroid at (0.5, 0.5) is inside Polygon 1: (0,0)-(0,1)-(1,1)-(1,0)-(0,0)
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Stop Inside Zone"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(0.5, 0.5)));
        stopPlace.setValidBetween(new ValidBetween(Instant.now().minusSeconds(3600), null));
        stopPlaceRepository.saveAndFlush(stopPlace);

        fareZoneRepository.updateStopPlaceTariffZoneRef();

        StopPlace updated = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());
        assertThat(updated.getTariffZones())
                .extracting(TariffZoneRef::getRef)
                .contains(fareZone.getNetexId());
    }

    @Test
    public void updateStopPlaceTariffZoneRef_stopPlaceOutsideMultiSurface_shouldNotAssignFareZone() {
        MultiPolygon multiPolygon = createTwoPolygonMultiSurface();

        FareZone fareZone = new FareZone();
        fareZone.setNetexId("TST:FareZone:MultiSurface3");
        fareZone.setName(new EmbeddableMultilingualString("Implicit Spatial Zone Outside"));
        fareZone.setScopingMethod(ScopingMethodEnumeration.IMPLICIT_SPATIAL_PROJECTION);
        fareZone.setMultiSurface(multiPolygon);
        fareZone.setValidBetween(new ValidBetween(Instant.now().minusSeconds(3600), null));
        fareZoneRepository.saveAndFlush(fareZone);

        // Centroid at (10.0, 10.0) is outside both polygons
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Stop Outside Zone"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.0, 10.0)));
        stopPlace.setValidBetween(new ValidBetween(Instant.now().minusSeconds(3600), null));
        stopPlaceRepository.saveAndFlush(stopPlace);

        fareZoneRepository.updateStopPlaceTariffZoneRef();

        StopPlace updated = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());
        assertThat(updated.getTariffZones())
                .extracting(TariffZoneRef::getRef)
                .doesNotContain(fareZone.getNetexId());
    }

    /**
     * Creates a MultiPolygon with two disconnected unit squares:
     * - Polygon 1: (0,0), (0,1), (1,1), (1,0), (0,0)
     * - Polygon 2: (3,0), (3,1), (4,1), (4,0), (3,0)
     */
    private MultiPolygon createTwoPolygonMultiSurface() {
        Polygon polygon1 = createPolygon(
                new Coordinate(0, 0), new Coordinate(0, 1),
                new Coordinate(1, 1), new Coordinate(1, 0),
                new Coordinate(0, 0));
        Polygon polygon2 = createPolygon(
                new Coordinate(3, 0), new Coordinate(3, 1),
                new Coordinate(4, 1), new Coordinate(4, 0),
                new Coordinate(3, 0));
        return geometryFactory.createMultiPolygon(new Polygon[]{polygon1, polygon2});
    }

    private Polygon createPolygon(Coordinate... coordinates) {
        LinearRing ring = new LinearRing(new CoordinateArraySequence(coordinates), geometryFactory);
        return geometryFactory.createPolygon(ring, null);
    }

}