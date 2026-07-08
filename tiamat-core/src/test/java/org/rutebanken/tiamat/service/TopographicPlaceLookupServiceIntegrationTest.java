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
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class TopographicPlaceLookupServiceIntegrationTest extends TiamatIntegrationTest {

    @Autowired
    private TopographicPlaceLookupService topographicPlaceLookupService;

    // ========== findTopographicPlace() Tests ==========

    @Test
    public void shouldFindTopographicPlaceContainingPoint() {
        TopographicPlace municipality = createTopographicPlace("NSR:TopographicPlace:1", "Oslo",
                TopographicPlaceTypeEnumeration.MUNICIPALITY,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        topographicPlaceRepository.save(municipality);
        topographicPlaceLookupService.reset();

        Optional<TopographicPlace> result = topographicPlaceLookupService.findTopographicPlace(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        assertThat(result).isPresent();
        assertThat(result.get().getNetexId()).isEqualTo("NSR:TopographicPlace:1");
        assertThat(result.get().getName().getValue()).isEqualTo("Oslo");
    }

    @Test
    public void shouldReturnEmptyWhenPointOutsideAllTopographicPlaces() {
        TopographicPlace municipality = createTopographicPlace("NSR:TopographicPlace:1", "Oslo",
                TopographicPlaceTypeEnumeration.MUNICIPALITY,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        topographicPlaceRepository.save(municipality);
        topographicPlaceLookupService.reset();

        Optional<TopographicPlace> result = topographicPlaceLookupService.findTopographicPlace(
                geometryFactory.createPoint(new Coordinate(5.0, 55.0)));

        assertThat(result).isEmpty();
    }

    @Test
    public void shouldPreferMunicipalityOverCounty() {
        // Create overlapping municipality and county - municipality should be preferred
        TopographicPlace county = createTopographicPlace("NSR:TopographicPlace:1", "Viken",
                TopographicPlaceTypeEnumeration.COUNTY,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        topographicPlaceRepository.save(county);

        TopographicPlace municipality = createTopographicPlace("NSR:TopographicPlace:2", "Oslo",
                TopographicPlaceTypeEnumeration.MUNICIPALITY,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        topographicPlaceRepository.save(municipality);
        topographicPlaceLookupService.reset();

        Optional<TopographicPlace> result = topographicPlaceLookupService.findTopographicPlace(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        assertThat(result).isPresent();
        assertThat(result.get().getTopographicPlaceType()).isEqualTo(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        assertThat(result.get().getName().getValue()).isEqualTo("Oslo");
    }

    @Test
    public void shouldPreferCountyOverCountry() {
        TopographicPlace country = createTopographicPlace("NSR:TopographicPlace:1", "Norway",
                TopographicPlaceTypeEnumeration.COUNTRY,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        topographicPlaceRepository.save(country);

        TopographicPlace county = createTopographicPlace("NSR:TopographicPlace:2", "Viken",
                TopographicPlaceTypeEnumeration.COUNTY,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        topographicPlaceRepository.save(county);
        topographicPlaceLookupService.reset();

        Optional<TopographicPlace> result = topographicPlaceLookupService.findTopographicPlace(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        assertThat(result).isPresent();
        assertThat(result.get().getTopographicPlaceType()).isEqualTo(TopographicPlaceTypeEnumeration.COUNTY);
        assertThat(result.get().getName().getValue()).isEqualTo("Viken");
    }

    @Test
    public void shouldIgnoreTopographicPlaceWithoutGeometry() {
        TopographicPlace withPolygon = createTopographicPlace("NSR:TopographicPlace:1", "Oslo",
                TopographicPlaceTypeEnumeration.MUNICIPALITY,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        topographicPlaceRepository.save(withPolygon);

        TopographicPlace withoutGeometry = new TopographicPlace(new EmbeddableMultilingualString("No Geometry"));
        withoutGeometry.setNetexId("NSR:TopographicPlace:2");
        withoutGeometry.setVersion(1L);
        withoutGeometry.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        topographicPlaceRepository.save(withoutGeometry);

        topographicPlaceLookupService.reset();

        Optional<TopographicPlace> result = topographicPlaceLookupService.findTopographicPlace(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        assertThat(result).isPresent();
        assertThat(result.get().getNetexId()).isEqualTo("NSR:TopographicPlace:1");
    }

    // ========== populateTopographicPlaceRelation() Tests ==========

    @Test
    public void shouldPopulateTopographicPlaceForStopPlace() {
        TopographicPlace municipality = createTopographicPlace("NSR:TopographicPlace:1", "Oslo",
                TopographicPlaceTypeEnumeration.MUNICIPALITY,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        topographicPlaceRepository.save(municipality);
        topographicPlaceLookupService.reset();

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test Stop"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        boolean changed = topographicPlaceLookupService.populateTopographicPlaceRelation(stopPlace);

        assertThat(changed).isTrue();
        assertThat(stopPlace.getTopographicPlace()).isNotNull();
        assertThat(stopPlace.getTopographicPlace().getNetexId()).isEqualTo("NSR:TopographicPlace:1");
    }

    @Test
    public void shouldReturnFalseWhenStopPlaceHasNoCoordinates() {
        TopographicPlace municipality = createTopographicPlace("NSR:TopographicPlace:1", "Oslo",
                TopographicPlaceTypeEnumeration.MUNICIPALITY,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        topographicPlaceRepository.save(municipality);
        topographicPlaceLookupService.reset();

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stop without coordinates"));
        // No centroid set

        boolean changed = topographicPlaceLookupService.populateTopographicPlaceRelation(stopPlace);

        assertThat(changed).isFalse();
        assertThat(stopPlace.getTopographicPlace()).isNull();
    }

    @Test
    public void shouldReturnFalseWhenNoMatchingTopographicPlaceFound() {
        TopographicPlace municipality = createTopographicPlace("NSR:TopographicPlace:1", "Oslo",
                TopographicPlaceTypeEnumeration.MUNICIPALITY,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        topographicPlaceRepository.save(municipality);
        topographicPlaceLookupService.reset();

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stop outside"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(5.0, 55.0)));

        boolean changed = topographicPlaceLookupService.populateTopographicPlaceRelation(stopPlace);

        assertThat(changed).isFalse();
        assertThat(stopPlace.getTopographicPlace()).isNull();
    }

    @Test
    public void shouldReturnFalseWhenExistingRefMatchesFound() {
        TopographicPlace municipality = createTopographicPlace("NSR:TopographicPlace:1", "Oslo",
                TopographicPlaceTypeEnumeration.MUNICIPALITY,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        municipality = topographicPlaceRepository.save(municipality);
        topographicPlaceLookupService.reset();

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stop with existing ref"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.5, 59.5)));
        stopPlace.setTopographicPlace(municipality); // Already has correct ref

        boolean changed = topographicPlaceLookupService.populateTopographicPlaceRelation(stopPlace);

        assertThat(changed).isFalse();
        assertThat(stopPlace.getTopographicPlace().getNetexId()).isEqualTo("NSR:TopographicPlace:1");
    }

    @Test
    public void shouldReturnTrueWhenRefChangesToDifferentTopographicPlace() {
        TopographicPlace oldMunicipality = createTopographicPlace("NSR:TopographicPlace:1", "Old Municipality",
                TopographicPlaceTypeEnumeration.MUNICIPALITY,
                new Coordinate(5.0, 55.0),
                new Coordinate(5.0, 56.0),
                new Coordinate(6.0, 56.0),
                new Coordinate(6.0, 55.0),
                new Coordinate(5.0, 55.0));
        oldMunicipality = topographicPlaceRepository.save(oldMunicipality);

        TopographicPlace newMunicipality = createTopographicPlace("NSR:TopographicPlace:2", "New Municipality",
                TopographicPlaceTypeEnumeration.MUNICIPALITY,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        topographicPlaceRepository.save(newMunicipality);
        topographicPlaceLookupService.reset();

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stop moved to new municipality"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.5, 59.5))); // In new municipality
        stopPlace.setTopographicPlace(oldMunicipality); // Still references old

        boolean changed = topographicPlaceLookupService.populateTopographicPlaceRelation(stopPlace);

        assertThat(changed).isTrue();
        assertThat(stopPlace.getTopographicPlace().getNetexId()).isEqualTo("NSR:TopographicPlace:2");
    }

    // ========== findTopographicPlaceByReference() Tests ==========

    @Test
    public void shouldFindTopographicPlaceByReference() {
        TopographicPlace oslo = createTopographicPlace("NSR:TopographicPlace:1", "Oslo",
                TopographicPlaceTypeEnumeration.MUNICIPALITY,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        topographicPlaceRepository.save(oslo);

        TopographicPlace bergen = createTopographicPlace("NSR:TopographicPlace:2", "Bergen",
                TopographicPlaceTypeEnumeration.MUNICIPALITY,
                new Coordinate(5.0, 60.0),
                new Coordinate(5.0, 61.0),
                new Coordinate(6.0, 61.0),
                new Coordinate(6.0, 60.0),
                new Coordinate(5.0, 60.0));
        topographicPlaceRepository.save(bergen);
        topographicPlaceLookupService.reset();

        List<String> references = Arrays.asList("NSR:TopographicPlace:1", "NSR:TopographicPlace:2");

        Optional<TopographicPlace> result = topographicPlaceLookupService.findTopographicPlaceByReference(
                references,
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        assertThat(result).isPresent();
        assertThat(result.get().getNetexId()).isEqualTo("NSR:TopographicPlace:1");
    }

    @Test
    public void shouldReturnEmptyWhenReferenceNotInList() {
        TopographicPlace oslo = createTopographicPlace("NSR:TopographicPlace:1", "Oslo",
                TopographicPlaceTypeEnumeration.MUNICIPALITY,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        topographicPlaceRepository.save(oslo);
        topographicPlaceLookupService.reset();

        // Reference list doesn't include Oslo
        List<String> references = Arrays.asList("NSR:TopographicPlace:999");

        Optional<TopographicPlace> result = topographicPlaceLookupService.findTopographicPlaceByReference(
                references,
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnEmptyWhenPointOutsideReferencedPolygon() {
        TopographicPlace oslo = createTopographicPlace("NSR:TopographicPlace:1", "Oslo",
                TopographicPlaceTypeEnumeration.MUNICIPALITY,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        topographicPlaceRepository.save(oslo);
        topographicPlaceLookupService.reset();

        List<String> references = List.of("NSR:TopographicPlace:1");

        // Point is outside Oslo
        Optional<TopographicPlace> result = topographicPlaceLookupService.findTopographicPlaceByReference(
                references,
                geometryFactory.createPoint(new Coordinate(5.0, 55.0)));

        assertThat(result).isEmpty();
    }

    // ========== Polygon with Holes Tests ==========

    @Test
    public void shouldFindTopographicPlaceWhenPointInsidePolygonWithHole() {
        Polygon polygonWithHole = createPolygonWithHole();
        TopographicPlace municipality = new TopographicPlace(new EmbeddableMultilingualString("Municipality with hole"));
        municipality.setNetexId("NSR:TopographicPlace:1");
        municipality.setVersion(1L);
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        municipality.setPolygon(polygonWithHole);
        topographicPlaceRepository.save(municipality);
        topographicPlaceLookupService.reset();

        // Point inside outer ring but outside hole
        Optional<TopographicPlace> result = topographicPlaceLookupService.findTopographicPlace(
                geometryFactory.createPoint(new Coordinate(10.2, 59.5)));

        assertThat(result).isPresent();
        assertThat(result.get().getNetexId()).isEqualTo("NSR:TopographicPlace:1");
    }

    @Test
    public void shouldNotFindTopographicPlaceWhenPointInsideHole() {
        Polygon polygonWithHole = createPolygonWithHole();
        TopographicPlace municipality = new TopographicPlace(new EmbeddableMultilingualString("Municipality with hole"));
        municipality.setNetexId("NSR:TopographicPlace:1");
        municipality.setVersion(1L);
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        municipality.setPolygon(polygonWithHole);
        topographicPlaceRepository.save(municipality);
        topographicPlaceLookupService.reset();

        // Point inside the hole
        Optional<TopographicPlace> result = topographicPlaceLookupService.findTopographicPlace(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        assertThat(result).isEmpty();
    }

    @Test
    public void shouldNotPopulateTopographicPlaceWhenStopInsideHole() {
        Polygon polygonWithHole = createPolygonWithHole();
        TopographicPlace municipality = new TopographicPlace(new EmbeddableMultilingualString("Municipality with hole"));
        municipality.setNetexId("NSR:TopographicPlace:1");
        municipality.setVersion(1L);
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        municipality.setPolygon(polygonWithHole);
        topographicPlaceRepository.save(municipality);
        topographicPlaceLookupService.reset();

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stop inside hole"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        boolean changed = topographicPlaceLookupService.populateTopographicPlaceRelation(stopPlace);

        assertThat(changed).isFalse();
        assertThat(stopPlace.getTopographicPlace()).isNull();
    }

    // ========== MultiSurface Tests ==========

    @Test
    public void shouldFindTopographicPlaceWithOnlyMultiSurface() {
        TopographicPlace municipality = new TopographicPlace(new EmbeddableMultilingualString("Municipality with MultiSurface"));
        municipality.setNetexId("NSR:TopographicPlace:1");
        municipality.setVersion(1L);
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        municipality.setMultiSurface(createMultiPolygon());
        topographicPlaceRepository.save(municipality);
        topographicPlaceLookupService.reset();

        Optional<TopographicPlace> result = topographicPlaceLookupService.findTopographicPlace(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        assertThat(result).isPresent();
        assertThat(result.get().getNetexId()).isEqualTo("NSR:TopographicPlace:1");
    }

    @Test
    public void shouldPreferMultiSurfaceOverPolygonWhenBothExist() {
        // MultiSurface covers areas A (10-11) and B (12-13)
        // Polygon only covers area A (10-11)
        TopographicPlace municipality = createTopographicPlace("NSR:TopographicPlace:1", "Municipality",
                TopographicPlaceTypeEnumeration.MUNICIPALITY,
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(11.0, 60.0),
                new Coordinate(11.0, 59.0),
                new Coordinate(10.0, 59.0));
        municipality.setMultiSurface(createMultiPolygonWithTwoAreas());
        topographicPlaceRepository.save(municipality);
        topographicPlaceLookupService.reset();

        // Point in area A (covered by both)
        Optional<TopographicPlace> resultA = topographicPlaceLookupService.findTopographicPlace(
                geometryFactory.createPoint(new Coordinate(10.5, 59.5)));
        assertThat(resultA).isPresent();

        // Point in area B (only covered by multiSurface)
        Optional<TopographicPlace> resultB = topographicPlaceLookupService.findTopographicPlace(
                geometryFactory.createPoint(new Coordinate(12.5, 59.5)));
        assertThat(resultB).isPresent();
        assertThat(resultB.get().getNetexId()).isEqualTo("NSR:TopographicPlace:1");
    }

    @Test
    public void shouldPopulateTopographicPlaceFromMultiSurface() {
        TopographicPlace municipality = new TopographicPlace(new EmbeddableMultilingualString("Municipality with MultiSurface"));
        municipality.setNetexId("NSR:TopographicPlace:1");
        municipality.setVersion(1L);
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        municipality.setMultiSurface(createMultiPolygon());
        topographicPlaceRepository.save(municipality);
        topographicPlaceLookupService.reset();

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stop in multiSurface"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.5, 59.5)));

        boolean changed = topographicPlaceLookupService.populateTopographicPlaceRelation(stopPlace);

        assertThat(changed).isTrue();
        assertThat(stopPlace.getTopographicPlace()).isNotNull();
        assertThat(stopPlace.getTopographicPlace().getNetexId()).isEqualTo("NSR:TopographicPlace:1");
    }

    @Test
    public void shouldFindTopographicPlaceInSecondPolygonOfMultiSurface() {
        // Municipality with islands - point in disconnected area B
        TopographicPlace municipality = new TopographicPlace(new EmbeddableMultilingualString("Municipality with islands"));
        municipality.setNetexId("NSR:TopographicPlace:1");
        municipality.setVersion(1L);
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        municipality.setMultiSurface(createMultiPolygonWithTwoAreas());
        topographicPlaceRepository.save(municipality);
        topographicPlaceLookupService.reset();

        // Point in area B (12-13, disconnected from A)
        Optional<TopographicPlace> result = topographicPlaceLookupService.findTopographicPlace(
                geometryFactory.createPoint(new Coordinate(12.5, 59.5)));

        assertThat(result).isPresent();
        assertThat(result.get().getNetexId()).isEqualTo("NSR:TopographicPlace:1");
    }

    @Test
    public void shouldReturnEmptyWhenPointOutsideMultiSurface() {
        TopographicPlace municipality = new TopographicPlace(new EmbeddableMultilingualString("Municipality"));
        municipality.setNetexId("NSR:TopographicPlace:1");
        municipality.setVersion(1L);
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        municipality.setMultiSurface(createMultiPolygon());
        topographicPlaceRepository.save(municipality);
        topographicPlaceLookupService.reset();

        Optional<TopographicPlace> result = topographicPlaceLookupService.findTopographicPlace(
                geometryFactory.createPoint(new Coordinate(5.0, 55.0)));

        assertThat(result).isEmpty();
    }

    // ========== Helper Methods ==========

    private TopographicPlace createTopographicPlace(String netexId, String name,
                                                     TopographicPlaceTypeEnumeration type,
                                                     Coordinate... coordinates) {
        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString(name));
        topographicPlace.setNetexId(netexId);
        topographicPlace.setVersion(1L);
        topographicPlace.setTopographicPlaceType(type);
        topographicPlace.setPolygon(geometryFactory.createPolygon(coordinates));
        return topographicPlace;
    }

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

    private MultiPolygon createMultiPolygon() {
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
        // Area B: 12-13, 59-60 (disconnected from A - like an island)
        Polygon polygonB = geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(12.0, 59.0),
                new Coordinate(12.0, 60.0),
                new Coordinate(13.0, 60.0),
                new Coordinate(13.0, 59.0),
                new Coordinate(12.0, 59.0)
        });
        return geometryFactory.createMultiPolygon(new Polygon[]{polygonA, polygonB});
    }
}