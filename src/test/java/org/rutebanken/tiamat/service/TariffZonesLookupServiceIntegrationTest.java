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

package org.rutebanken.tiamat.service;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.model.ScopingMethodEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopPlaceReference;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TariffZonesLookupServiceIntegrationTest extends TiamatIntegrationTest {

    @Autowired
    private TariffZonesLookupService tariffZonesLookupService;

    @Test
    public void shouldPopulateTariffZoneForStopPlaceWithinZone() {
        TariffZone tariffZone = createTariffZoneWithPolygon("NSR:TariffZone:1", "Zone A",
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        tariffZoneRepository.save(tariffZone);
        tariffZonesLookupService.resetTariffZone();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Test Stop"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        boolean changed = tariffZonesLookupService.populateTariffZone(stopPlace);

        assertThat(changed).isTrue();
        assertThat(stopPlace.getTariffZones()).hasSize(1);
        assertThat(stopPlace.getTariffZones().iterator().next().getRef()).isEqualTo("NSR:TariffZone:1");
    }

    @Test
    public void shouldNotPopulateTariffZoneWhenStopPlaceOutsideZone() {
        TariffZone tariffZone = createTariffZoneWithPolygon("NSR:TariffZone:1", "Zone A",
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        tariffZoneRepository.save(tariffZone);
        tariffZonesLookupService.resetTariffZone();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Test Stop Outside"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(5.0, 55.0)));

        boolean changed = tariffZonesLookupService.populateTariffZone(stopPlace);

        assertThat(changed).isFalse();
        assertThat(stopPlace.getTariffZones()).isEmpty();
    }

    @Test
    public void shouldReturnFalseWhenStopPlaceHasNoCentroid() {
        TariffZone tariffZone = createTariffZoneWithPolygon("NSR:TariffZone:1", "Zone A",
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        tariffZoneRepository.save(tariffZone);
        tariffZonesLookupService.resetTariffZone();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Stop without centroid"));

        boolean changed = tariffZonesLookupService.populateTariffZone(stopPlace);

        assertThat(changed).isFalse();
        // Note: the service still initializes tariffZones set, but doesn't populate it
    }

    @Test
    public void shouldNotDuplicateExistingTariffZoneRefs() {
        TariffZone tariffZone = createTariffZoneWithPolygon("NSR:TariffZone:1", "Zone A",
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        tariffZone = tariffZoneRepository.save(tariffZone);
        tariffZonesLookupService.resetTariffZone();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Test Stop"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.5, 59.5)));
        Set<TariffZoneRef> existingRefs = new HashSet<>();
        existingRefs.add(new TariffZoneRef(tariffZone));
        stopPlace.setTariffZones(existingRefs);

        boolean changed = tariffZonesLookupService.populateTariffZone(stopPlace);

        assertThat(changed).isFalse();
        assertThat(stopPlace.getTariffZones()).hasSize(1);
    }

    @Test
    public void shouldPopulateMultipleTariffZonesWhenStopPlaceInOverlappingZones() {
        TariffZone zoneA = createTariffZoneWithPolygon("NSR:TariffZone:1", "Zone A",
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        tariffZoneRepository.save(zoneA);

        TariffZone zoneB = createTariffZoneWithPolygon("NSR:TariffZone:2", "Zone B",
                new Coordinate(10.3, 59.3),
                new Coordinate(10.3, 59.7),
                new Coordinate(10.7, 59.7),
                new Coordinate(10.7, 59.3),
                new Coordinate(10.3, 59.3));
        tariffZoneRepository.save(zoneB);
        tariffZonesLookupService.resetTariffZone();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Stop in overlapping zones"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        boolean changed = tariffZonesLookupService.populateTariffZone(stopPlace);

        assertThat(changed).isTrue();
        assertThat(stopPlace.getTariffZones()).hasSize(2);
        Set<String> zoneRefs = new HashSet<>();
        stopPlace.getTariffZones().forEach(ref -> zoneRefs.add(ref.getRef()));
        assertThat(zoneRefs).containsExactlyInAnyOrder("NSR:TariffZone:1", "NSR:TariffZone:2");
    }

    @Test
    public void shouldFindTariffZonesContainingPoint() {
        TariffZone tariffZone = createTariffZoneWithPolygon("NSR:TariffZone:1", "Zone A",
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        tariffZoneRepository.save(tariffZone);
        tariffZonesLookupService.resetTariffZone();

        List<TariffZone> result = tariffZonesLookupService.findTariffZones(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getNetexId()).isEqualTo("NSR:TariffZone:1");
    }

    @Test
    public void shouldReturnEmptyListWhenPointOutsideAllTariffZones() {
        TariffZone tariffZone = createTariffZoneWithPolygon("NSR:TariffZone:1", "Zone A",
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        tariffZoneRepository.save(tariffZone);
        tariffZonesLookupService.resetTariffZone();

        List<TariffZone> result = tariffZonesLookupService.findTariffZones(
                geometryFactory.createPoint(new Coordinate(5.0, 55.0)));

        assertThat(result).isEmpty();
    }

    @Test
    public void shouldFindLatestVersionOfTariffZone() {
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0)
        };
        Polygon polygon = geometryFactory.createPolygon(coordinates);

        TariffZone v1 = new TariffZone();
        v1.setNetexId("NSR:TariffZone:1");
        v1.setName(new EmbeddableMultilingualString("Zone A v1"));
        v1.setVersion(1L);
        v1.setPolygon(polygon);
        tariffZoneRepository.save(v1);

        TariffZone v2 = new TariffZone();
        v2.setNetexId("NSR:TariffZone:1");
        v2.setName(new EmbeddableMultilingualString("Zone A v2"));
        v2.setVersion(2L);
        v2.setPolygon(polygon);
        tariffZoneRepository.save(v2);

        tariffZonesLookupService.resetTariffZone();

        List<TariffZone> result = tariffZonesLookupService.findTariffZones(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVersion()).isEqualTo(2L);
        assertThat(result.get(0).getName().getValue()).isEqualTo("Zone A v2");
    }

    @Test
    public void shouldFindFareZonesContainingPointWithImplicitSpatialProjection() {
        FareZone fareZone = createFareZoneWithPolygon("NSR:FareZone:1", "Fare Zone A",
                ScopingMethodEnumeration.IMPLICIT_SPATIAL_PROJECTION,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        fareZoneRepository.save(fareZone);
        tariffZonesLookupService.resetFareZone();

        List<FareZone> result = tariffZonesLookupService.findFareZones(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getNetexId()).isEqualTo("NSR:FareZone:1");
    }

    @Test
    public void shouldReturnEmptyListWhenPointOutsideAllFareZones() {
        FareZone fareZone = createFareZoneWithPolygon("NSR:FareZone:1", "Fare Zone A",
                ScopingMethodEnumeration.IMPLICIT_SPATIAL_PROJECTION,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        fareZoneRepository.save(fareZone);
        tariffZonesLookupService.resetFareZone();

        List<FareZone> result = tariffZonesLookupService.findFareZones(
                geometryFactory.createPoint(new Coordinate(5.0, 55.0)));

        assertThat(result).isEmpty();
    }

    @Test
    public void shouldPopulateBothTariffZoneAndFareZone() {
        TariffZone tariffZone = createTariffZoneWithPolygon("NSR:TariffZone:1", "Tariff Zone A",
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        tariffZoneRepository.save(tariffZone);

        FareZone fareZone = createFareZoneWithPolygon("NSR:FareZone:1", "Fare Zone A",
                ScopingMethodEnumeration.IMPLICIT_SPATIAL_PROJECTION,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        fareZoneRepository.save(fareZone);

        tariffZonesLookupService.resetTariffZone();
        tariffZonesLookupService.resetFareZone();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Test Stop"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        boolean changed = tariffZonesLookupService.populateTariffZone(stopPlace);

        assertThat(changed).isTrue();
        assertThat(stopPlace.getTariffZones()).hasSize(2);
        Set<String> zoneRefs = new HashSet<>();
        stopPlace.getTariffZones().forEach(ref -> zoneRefs.add(ref.getRef()));
        assertThat(zoneRefs).containsExactlyInAnyOrder("NSR:TariffZone:1", "NSR:FareZone:1");
    }

    @Test
    public void shouldNotPopulateFareZoneWithExplicitStopsWhenStopNotMemberAndHasExistingRefs() {
        // Create a tariff zone so the stop place gets an existing ref
        TariffZone tariffZone = createTariffZoneWithPolygon("NSR:TariffZone:1", "Tariff Zone A",
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        tariffZone = tariffZoneRepository.save(tariffZone);

        FareZone fareZone = createFareZoneWithPolygon("NSR:FareZone:1", "Fare Zone Explicit",
                ScopingMethodEnumeration.EXPLICIT_STOPS,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        fareZone.getFareZoneMembers().add(new StopPlaceReference("NSR:StopPlace:999"));
        fareZoneRepository.save(fareZone);

        tariffZonesLookupService.resetTariffZone();
        tariffZonesLookupService.resetFareZone();

        // Create stop place with existing tariff zone ref - this is required for
        // EXPLICIT_STOPS logic to be evaluated (when tariffZones is empty, the filter short-circuits)
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:1");
        stopPlace.setName(new EmbeddableMultilingualString("Test Stop"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.5, 59.5)));
        Set<TariffZoneRef> existingRefs = new HashSet<>();
        existingRefs.add(new TariffZoneRef(tariffZone));
        stopPlace.setTariffZones(existingRefs);

        boolean changed = tariffZonesLookupService.populateTariffZone(stopPlace);

        // No change because the tariff zone was already there, and the fare zone with
        // EXPLICIT_STOPS doesn't include this stop in its members
        assertThat(changed).isFalse();
        assertThat(stopPlace.getTariffZones()).hasSize(1);
        assertThat(stopPlace.getTariffZones().iterator().next().getRef()).isEqualTo("NSR:TariffZone:1");
    }

    @Test
    public void shouldPopulateFareZoneWithExplicitStopsWhenStopIsMember() {
        FareZone fareZone = createFareZoneWithPolygon("NSR:FareZone:1", "Fare Zone Explicit",
                ScopingMethodEnumeration.EXPLICIT_STOPS,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        fareZone.getFareZoneMembers().add(new StopPlaceReference("NSR:StopPlace:1"));
        fareZoneRepository.save(fareZone);

        tariffZonesLookupService.resetFareZone();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:1");
        stopPlace.setName(new EmbeddableMultilingualString("Test Stop"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        boolean changed = tariffZonesLookupService.populateTariffZone(stopPlace);

        assertThat(changed).isTrue();
        assertThat(stopPlace.getTariffZones()).hasSize(1);
        assertThat(stopPlace.getTariffZones().iterator().next().getRef()).isEqualTo("NSR:FareZone:1");
    }

    @Test
    public void shouldIgnoreTariffZoneWithoutPolygon() {
        TariffZone zoneWithPolygon = createTariffZoneWithPolygon("NSR:TariffZone:1", "Zone A",
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        tariffZoneRepository.save(zoneWithPolygon);

        TariffZone zoneWithoutPolygon = new TariffZone();
        zoneWithoutPolygon.setNetexId("NSR:TariffZone:2");
        zoneWithoutPolygon.setName(new EmbeddableMultilingualString("Zone B - No Polygon"));
        zoneWithoutPolygon.setVersion(1L);
        tariffZoneRepository.save(zoneWithoutPolygon);

        tariffZonesLookupService.resetTariffZone();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Test Stop"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        boolean changed = tariffZonesLookupService.populateTariffZone(stopPlace);

        assertThat(changed).isTrue();
        assertThat(stopPlace.getTariffZones()).hasSize(1);
        assertThat(stopPlace.getTariffZones().iterator().next().getRef()).isEqualTo("NSR:TariffZone:1");
    }

    @Test
    public void shouldIgnoreFareZoneWithoutPolygon() {
        FareZone zoneWithPolygon = createFareZoneWithPolygon("NSR:FareZone:1", "Fare Zone A",
                ScopingMethodEnumeration.IMPLICIT_SPATIAL_PROJECTION,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        fareZoneRepository.save(zoneWithPolygon);

        FareZone zoneWithoutPolygon = new FareZone();
        zoneWithoutPolygon.setNetexId("NSR:FareZone:2");
        zoneWithoutPolygon.setName(new EmbeddableMultilingualString("Fare Zone B - No Polygon"));
        zoneWithoutPolygon.setVersion(1L);
        zoneWithoutPolygon.setScopingMethod(ScopingMethodEnumeration.IMPLICIT_SPATIAL_PROJECTION);
        fareZoneRepository.save(zoneWithoutPolygon);

        tariffZonesLookupService.resetFareZone();

        List<FareZone> result = tariffZonesLookupService.findFareZones(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNetexId()).isEqualTo("NSR:FareZone:1");
    }

    // ========== Polygon with Holes Tests ==========

    @Test
    public void shouldFindTariffZoneWhenPointInsidePolygonWithHole() {
        // Create polygon with hole: outer ring 10-11, inner hole 10.4-10.6
        Polygon polygonWithHole = createPolygonWithHole();
        TariffZone tariffZone = new TariffZone();
        tariffZone.setNetexId("NSR:TariffZone:1");
        tariffZone.setName(new EmbeddableMultilingualString("Zone with hole"));
        tariffZone.setVersion(1L);
        tariffZone.setPolygon(polygonWithHole);
        tariffZoneRepository.save(tariffZone);
        tariffZonesLookupService.resetTariffZone();

        // Point inside outer ring but outside hole (should match)
        List<TariffZone> result = tariffZonesLookupService.findTariffZones(
                geometryFactory.createPoint(new Coordinate(10.2, 59.5)));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getNetexId()).isEqualTo("NSR:TariffZone:1");
    }

    @Test
    public void shouldNotFindTariffZoneWhenPointInsideHole() {
        // Create polygon with hole
        Polygon polygonWithHole = createPolygonWithHole();
        TariffZone tariffZone = new TariffZone();
        tariffZone.setNetexId("NSR:TariffZone:1");
        tariffZone.setName(new EmbeddableMultilingualString("Zone with hole"));
        tariffZone.setVersion(1L);
        tariffZone.setPolygon(polygonWithHole);
        tariffZoneRepository.save(tariffZone);
        tariffZonesLookupService.resetTariffZone();

        // Point inside the hole (should NOT match)
        List<TariffZone> result = tariffZonesLookupService.findTariffZones(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        assertThat(result).isEmpty();
    }

    @Test
    public void shouldPopulateTariffZoneWhenStopOutsideHole() {
        Polygon polygonWithHole = createPolygonWithHole();
        TariffZone tariffZone = new TariffZone();
        tariffZone.setNetexId("NSR:TariffZone:1");
        tariffZone.setName(new EmbeddableMultilingualString("Zone with hole"));
        tariffZone.setVersion(1L);
        tariffZone.setPolygon(polygonWithHole);
        tariffZoneRepository.save(tariffZone);
        tariffZonesLookupService.resetTariffZone();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Stop outside hole"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.2, 59.5)));

        boolean changed = tariffZonesLookupService.populateTariffZone(stopPlace);

        assertThat(changed).isTrue();
        assertThat(stopPlace.getTariffZones()).hasSize(1);
    }

    @Test
    public void shouldNotPopulateTariffZoneWhenStopInsideHole() {
        Polygon polygonWithHole = createPolygonWithHole();
        TariffZone tariffZone = new TariffZone();
        tariffZone.setNetexId("NSR:TariffZone:1");
        tariffZone.setName(new EmbeddableMultilingualString("Zone with hole"));
        tariffZone.setVersion(1L);
        tariffZone.setPolygon(polygonWithHole);
        tariffZoneRepository.save(tariffZone);
        tariffZonesLookupService.resetTariffZone();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Stop inside hole"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        boolean changed = tariffZonesLookupService.populateTariffZone(stopPlace);

        assertThat(changed).isFalse();
        assertThat(stopPlace.getTariffZones()).isEmpty();
    }

    @Test
    public void shouldFindFareZoneWhenPointInsidePolygonWithHole() {
        Polygon polygonWithHole = createPolygonWithHole();
        FareZone fareZone = new FareZone();
        fareZone.setNetexId("NSR:FareZone:1");
        fareZone.setName(new EmbeddableMultilingualString("Fare Zone with hole"));
        fareZone.setVersion(1L);
        fareZone.setScopingMethod(ScopingMethodEnumeration.IMPLICIT_SPATIAL_PROJECTION);
        fareZone.setPolygon(polygonWithHole);
        fareZoneRepository.save(fareZone);
        tariffZonesLookupService.resetFareZone();

        // Point inside outer ring but outside hole
        List<FareZone> result = tariffZonesLookupService.findFareZones(
                geometryFactory.createPoint(new Coordinate(10.2, 59.5)));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getNetexId()).isEqualTo("NSR:FareZone:1");
    }

    @Test
    public void shouldNotFindFareZoneWhenPointInsideHole() {
        Polygon polygonWithHole = createPolygonWithHole();
        FareZone fareZone = new FareZone();
        fareZone.setNetexId("NSR:FareZone:1");
        fareZone.setName(new EmbeddableMultilingualString("Fare Zone with hole"));
        fareZone.setVersion(1L);
        fareZone.setScopingMethod(ScopingMethodEnumeration.IMPLICIT_SPATIAL_PROJECTION);
        fareZone.setPolygon(polygonWithHole);
        fareZoneRepository.save(fareZone);
        tariffZonesLookupService.resetFareZone();

        // Point inside the hole
        List<FareZone> result = tariffZonesLookupService.findFareZones(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        assertThat(result).isEmpty();
    }

    // ========== MultiSurface Tests ==========

    @Test
    public void shouldFindTariffZoneWithOnlyMultiSurface() {
        // Create tariff zone with only multiSurface (no polygon)
        TariffZone zoneWithMultiSurface = new TariffZone();
        zoneWithMultiSurface.setNetexId("NSR:TariffZone:1");
        zoneWithMultiSurface.setName(new EmbeddableMultilingualString("Zone with MultiSurface only"));
        zoneWithMultiSurface.setVersion(1L);
        zoneWithMultiSurface.setMultiSurface(createPersistableMultiPolygon());
        tariffZoneRepository.save(zoneWithMultiSurface);
        tariffZonesLookupService.resetTariffZone();

        List<TariffZone> result = tariffZonesLookupService.findTariffZones(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getNetexId()).isEqualTo("NSR:TariffZone:1");
    }

    @Test
    public void shouldFindTariffZoneWithBothPolygonAndMultiSurface() {
        // Create tariff zone with both polygon and multiSurface
        TariffZone zone = createTariffZoneWithPolygon("NSR:TariffZone:1", "Zone with both",
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        zone.setMultiSurface(createPersistableMultiPolygon());
        tariffZoneRepository.save(zone);
        tariffZonesLookupService.resetTariffZone();

        List<TariffZone> result = tariffZonesLookupService.findTariffZones(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        // Should find via polygon (multiSurface is ignored but zone is still found)
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getNetexId()).isEqualTo("NSR:TariffZone:1");
    }

    @Test
    public void shouldFindFareZoneWithOnlyMultiSurface() {
        // Create fare zone with only multiSurface (no polygon)
        FareZone zoneWithMultiSurface = new FareZone();
        zoneWithMultiSurface.setNetexId("NSR:FareZone:1");
        zoneWithMultiSurface.setName(new EmbeddableMultilingualString("Fare Zone with MultiSurface only"));
        zoneWithMultiSurface.setVersion(1L);
        zoneWithMultiSurface.setScopingMethod(ScopingMethodEnumeration.IMPLICIT_SPATIAL_PROJECTION);
        zoneWithMultiSurface.setMultiSurface(createPersistableMultiPolygon());
        fareZoneRepository.save(zoneWithMultiSurface);
        tariffZonesLookupService.resetFareZone();

        List<FareZone> result = tariffZonesLookupService.findFareZones(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getNetexId()).isEqualTo("NSR:FareZone:1");
    }

    @Test
    public void shouldNotPopulateTariffZoneWhenOnlyMultiSurfaceExists() {
        // Documents that stop places don't get tariff zone refs when zone has only multiSurface
        TariffZone zoneWithMultiSurface = new TariffZone();
        zoneWithMultiSurface.setNetexId("NSR:TariffZone:1");
        zoneWithMultiSurface.setName(new EmbeddableMultilingualString("Zone with MultiSurface only"));
        zoneWithMultiSurface.setVersion(1L);
        zoneWithMultiSurface.setMultiSurface(createPersistableMultiPolygon());
        tariffZoneRepository.save(zoneWithMultiSurface);
        tariffZonesLookupService.resetTariffZone();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Test Stop"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        boolean changed = tariffZonesLookupService.populateTariffZone(stopPlace);

        assertThat(changed).isTrue();
        assertThat(stopPlace.getTariffZones().iterator().next().getRef()).isEqualTo("NSR:TariffZone:1");
    }

    @Test
    public void shouldFindTariffZoneInSecondPolygonOfMultiSurface() {
        // When both polygon and multiSurface exist, multiSurface is preferred
        // This allows matching points in disconnected areas of a multiSurface

        // Zone has polygon covering area A, and multiSurface covering areas A and B
        TariffZone zone = new TariffZone();
        zone.setNetexId("NSR:TariffZone:1");
        zone.setName(new EmbeddableMultilingualString("Zone with multi areas"));
        zone.setVersion(1L);
        // Polygon only covers area A (10-11, 59-60)
        zone.setPolygon(geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0)
        }));
        // MultiSurface covers both area A and area B (12-13, 59-60)
        zone.setMultiSurface(createMultiPolygonWithTwoAreas());
        tariffZoneRepository.save(zone);
        tariffZonesLookupService.resetTariffZone();

        // Point in area A (covered by both polygon and multiSurface) - should match
        List<TariffZone> resultA = tariffZonesLookupService.findTariffZones(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));
        assertThat(resultA).hasSize(1);

        // Point in area B (covered by multiSurface only) - should match because multiSurface is preferred
        List<TariffZone> resultB = tariffZonesLookupService.findTariffZones(
                geometryFactory.createPoint(new Coordinate(12.5, 59.5)));
        assertThat(resultB).hasSize(1);
        assertThat(resultB.getFirst().getNetexId()).isEqualTo("NSR:TariffZone:1");
    }

    // ========== Helper Methods ==========

    private Polygon createPolygonWithHole() {
        // Outer ring: 10-11 longitude, 59-60 latitude
        LinearRing outerRing = geometryFactory.createLinearRing(new Coordinate[]{
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0)
        });
        // Inner hole: 10.4-10.6 longitude, 59.4-59.6 latitude
        LinearRing hole = geometryFactory.createLinearRing(new Coordinate[]{
                new Coordinate(10.4, 59.4),
                new Coordinate(10.4, 59.6),
                new Coordinate(10.6, 59.6),
                new Coordinate(10.6, 59.4),
                new Coordinate(10.4, 59.4)
        });
        return geometryFactory.createPolygon(outerRing, new LinearRing[]{hole});
    }

    private MultiPolygon createPersistableMultiPolygon() {
        Polygon polygon = geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0)
        });
        return geometryFactory.createMultiPolygon(new Polygon[]{polygon});

    }

    private MultiPolygon createMultiPolygonWithTwoAreas() {
        // Area A: 10-11, 59-60
        Polygon polygonA = geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0)
        });
        // Area B: 12-13, 59-60 (disconnected from A)
        Polygon polygonB = geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(12.0, 59.0),
                new Coordinate(12.0, 60.0),
                new Coordinate(13.0, 60.0),
                new Coordinate(13.0, 59.0),
                new Coordinate(12.0, 59.0)
        });
        return geometryFactory.createMultiPolygon(new Polygon[]{polygonA, polygonB});
    }

    private TariffZone createTariffZoneWithPolygon(String netexId, String name, Coordinate... coordinates) {
        TariffZone tariffZone = new TariffZone();
        tariffZone.setNetexId(netexId);
        tariffZone.setName(new EmbeddableMultilingualString(name));
        tariffZone.setVersion(1L);
        tariffZone.setPolygon(geometryFactory.createPolygon(coordinates));
        return tariffZone;
    }

    private FareZone createFareZoneWithPolygon(String netexId, String name, ScopingMethodEnumeration scopingMethod, Coordinate... coordinates) {
        FareZone fareZone = new FareZone();
        fareZone.setNetexId(netexId);
        fareZone.setName(new EmbeddableMultilingualString(name));
        fareZone.setVersion(1L);
        fareZone.setScopingMethod(scopingMethod);
        fareZone.setPolygon(geometryFactory.createPolygon(coordinates));
        return fareZone;
    }
}