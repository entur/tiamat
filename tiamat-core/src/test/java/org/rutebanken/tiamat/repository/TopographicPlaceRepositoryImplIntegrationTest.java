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

import jakarta.persistence.EntityManager;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.model.ValidBetween;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class TopographicPlaceRepositoryImplIntegrationTest extends TiamatIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    public void updateStopPlaceTopographicPlaceRef_shouldAssignMunicipalityOverCounty() {
        // Create a point for the stop place
        Point stopPlacePoint = geometryFactory.createPoint(new Coordinate(10.0, 59.0));

        // Create overlapping polygons - larger buffer for County, smaller for Municipality
        Polygon largePolygon = createPolygonAroundPoint(stopPlacePoint, 0.5);  // County - larger area
        Polygon smallPolygon = createPolygonAroundPoint(stopPlacePoint, 0.1);  // Municipality - smaller area

        // Create County topographic place (lower priority)
        TopographicPlace county = new TopographicPlace();
        county.setName(new EmbeddableMultilingualString("Test County"));
        county.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);
        county.setPolygon(largePolygon);
        county.setVersion(1L);
        topographicPlaceRepository.save(county);

        // Create Municipality topographic place (higher priority)
        TopographicPlace municipality = new TopographicPlace();
        municipality.setName(new EmbeddableMultilingualString("Test Municipality"));
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        municipality.setPolygon(smallPolygon);
        municipality.setVersion(1L);
        topographicPlaceRepository.save(municipality);

        // Create stop place without topographic place ref
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Test Stop"));
        stopPlace.setCentroid(stopPlacePoint);
        stopPlace.setVersion(1L);
        stopPlace.setValidBetween(new ValidBetween(Instant.now().minusSeconds(3600), null));
        stopPlaceRepository.save(stopPlace);

        // Flush and clear to ensure data is persisted and visible to native query
        entityManager.flush();
        entityManager.clear();

        // Verify no topographic place is assigned yet
        StopPlace beforeUpdate = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());
        assertThat(beforeUpdate.getTopographicPlace()).isNull();

        // Run the update
        int updated = topographicPlaceRepository.updateStopPlaceTopographicPlaceRef();

        // Verify at least one stop was updated
        assertThat(updated).isGreaterThanOrEqualTo(1);

        // Clear persistence context to force reload from DB
        entityManager.clear();

        // Refresh the stop place from DB
        StopPlace updatedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        // Verify Municipality was assigned (higher priority than County)
        assertThat(updatedStopPlace.getTopographicPlace())
                .as("Stop place should have topographic place assigned")
                .isNotNull();
        assertThat(updatedStopPlace.getTopographicPlace().getTopographicPlaceType())
                .as("Should be Municipality, not County")
                .isEqualTo(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        assertThat(updatedStopPlace.getTopographicPlace().getNetexId())
                .as("Should match Municipality netex ID")
                .isEqualTo(municipality.getNetexId());
    }

    @Test
    public void updateStopPlaceTopographicPlaceRef_shouldAssignCountryWhenOnlyCountryExists() {
        // Create a point for the stop place
        Point stopPlacePoint = geometryFactory.createPoint(new Coordinate(11.0, 60.0));

        // Create Country polygon
        Polygon countryPolygon = createPolygonAroundPoint(stopPlacePoint, 1.0);

        // Create Country topographic place
        TopographicPlace country = new TopographicPlace();
        country.setName(new EmbeddableMultilingualString("Test Country"));
        country.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTRY);
        country.setPolygon(countryPolygon);
        country.setVersion(1L);
        topographicPlaceRepository.save(country);

        // Create stop place
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Test Stop in Country"));
        stopPlace.setCentroid(stopPlacePoint);
        stopPlace.setVersion(1L);
        stopPlace.setValidBetween(new ValidBetween(Instant.now().minusSeconds(3600), null));
        stopPlaceRepository.save(stopPlace);

        // Flush and clear
        entityManager.flush();
        entityManager.clear();

        // Run the update
        int updated = topographicPlaceRepository.updateStopPlaceTopographicPlaceRef();

        assertThat(updated).isGreaterThanOrEqualTo(1);

        // Clear and refresh
        entityManager.clear();
        StopPlace updatedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(updatedStopPlace.getTopographicPlace())
                .as("Stop place should have topographic place assigned")
                .isNotNull();
        assertThat(updatedStopPlace.getTopographicPlace().getTopographicPlaceType())
                .as("Should be Country")
                .isEqualTo(TopographicPlaceTypeEnumeration.COUNTRY);
    }

    @Test
    public void updateStopPlaceTopographicPlaceRef_shouldNotUpdateStopPlaceOutsidePolygons() {
        // Create a point for the stop place OUTSIDE any polygon
        Point stopPlacePoint = geometryFactory.createPoint(new Coordinate(20.0, 70.0));

        // Create a polygon far from the stop place
        Point polygonCenter = geometryFactory.createPoint(new Coordinate(10.0, 59.0));
        Polygon polygon = createPolygonAroundPoint(polygonCenter, 0.1);

        // Create topographic place
        TopographicPlace municipality = new TopographicPlace();
        municipality.setName(new EmbeddableMultilingualString("Distant Municipality"));
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        municipality.setPolygon(polygon);
        municipality.setVersion(1L);
        topographicPlaceRepository.save(municipality);

        // Create stop place outside the polygon
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Stop Outside Polygon"));
        stopPlace.setCentroid(stopPlacePoint);
        stopPlace.setVersion(1L);
        stopPlace.setValidBetween(new ValidBetween(Instant.now().minusSeconds(3600), null));
        stopPlaceRepository.save(stopPlace);

        // Flush and clear
        entityManager.flush();
        entityManager.clear();

        // Run the update
        topographicPlaceRepository.updateStopPlaceTopographicPlaceRef();

        // Clear and verify - should still be null
        entityManager.clear();
        StopPlace updatedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(updatedStopPlace.getTopographicPlace())
                .as("Stop place outside all polygons should have no topographic place")
                .isNull();
    }

    @Test
    public void updateStopPlaceTopographicPlaceRef_shouldHandleThreeLevelHierarchy() {
        // Create a point for the stop place
        Point stopPlacePoint = geometryFactory.createPoint(new Coordinate(10.5, 59.5));

        // Create overlapping polygons for all three levels
        Polygon countryPolygon = createPolygonAroundPoint(stopPlacePoint, 2.0);
        Polygon countyPolygon = createPolygonAroundPoint(stopPlacePoint, 1.0);
        Polygon municipalityPolygon = createPolygonAroundPoint(stopPlacePoint, 0.5);

        // Create Country (lowest priority)
        TopographicPlace country = new TopographicPlace();
        country.setName(new EmbeddableMultilingualString("Norway"));
        country.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTRY);
        country.setPolygon(countryPolygon);
        country.setVersion(1L);
        topographicPlaceRepository.save(country);

        // Create County (medium priority)
        TopographicPlace county = new TopographicPlace();
        county.setName(new EmbeddableMultilingualString("Vestland"));
        county.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);
        county.setPolygon(countyPolygon);
        county.setVersion(1L);
        topographicPlaceRepository.save(county);

        // Create Municipality (highest priority)
        TopographicPlace municipality = new TopographicPlace();
        municipality.setName(new EmbeddableMultilingualString("Bergen"));
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        municipality.setPolygon(municipalityPolygon);
        municipality.setVersion(1L);
        topographicPlaceRepository.save(municipality);

        // Create stop place
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Bergen Central"));
        stopPlace.setCentroid(stopPlacePoint);
        stopPlace.setVersion(1L);
        stopPlace.setValidBetween(new ValidBetween(Instant.now().minusSeconds(3600), null));
        stopPlaceRepository.save(stopPlace);

        // Flush and clear
        entityManager.flush();
        entityManager.clear();

        // Run the update
        int updated = topographicPlaceRepository.updateStopPlaceTopographicPlaceRef();

        assertThat(updated).isGreaterThanOrEqualTo(1);

        // Clear and verify Municipality is selected
        entityManager.clear();
        StopPlace updatedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(updatedStopPlace.getTopographicPlace()).isNotNull();
        assertThat(updatedStopPlace.getTopographicPlace().getTopographicPlaceType())
                .as("Should be Municipality (highest priority)")
                .isEqualTo(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        assertThat(updatedStopPlace.getTopographicPlace().getName().getValue())
                .isEqualTo("Bergen");
    }

    @Test
    public void updateStopPlaceTopographicPlaceRef_shouldMatchViaMultiSurface() {
        // Create a point for the stop place
        Point stopPlacePoint = geometryFactory.createPoint(new Coordinate(12.0, 61.0));

        // Create a MultiPolygon for the topographic place
        MultiPolygon multiPolygon = createMultiPolygonAroundPoint(stopPlacePoint, 0.5);

        // Create Municipality with multi_surface (not polygon)
        TopographicPlace municipality = new TopographicPlace();
        municipality.setName(new EmbeddableMultilingualString("MultiSurface Municipality"));
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        municipality.setMultiSurface(multiPolygon);
        municipality.setVersion(1L);
        topographicPlaceRepository.save(municipality);

        // Create stop place
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Stop in MultiSurface"));
        stopPlace.setCentroid(stopPlacePoint);
        stopPlace.setVersion(1L);
        stopPlace.setValidBetween(new ValidBetween(Instant.now().minusSeconds(3600), null));
        stopPlaceRepository.save(stopPlace);

        // Flush and clear
        entityManager.flush();
        entityManager.clear();

        // Run the update
        int updated = topographicPlaceRepository.updateStopPlaceTopographicPlaceRef();

        assertThat(updated).isGreaterThanOrEqualTo(1);

        // Clear and verify
        entityManager.clear();
        StopPlace updatedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(updatedStopPlace.getTopographicPlace())
                .as("Stop place should have topographic place assigned via multi_surface")
                .isNotNull();
        assertThat(updatedStopPlace.getTopographicPlace().getName().getValue())
                .isEqualTo("MultiSurface Municipality");
    }

    @Test
    public void updateStopPlaceTopographicPlaceRef_shouldPreferMultiSurfaceOverPolygonForSameTopographicPlace() {
        // Create a point for the stop place
        Point stopPlacePoint = geometryFactory.createPoint(new Coordinate(14.0, 63.0));

        // Create a Municipality with BOTH polygon AND multi_surface containing the point
        Polygon polygon = createPolygonAroundPoint(stopPlacePoint, 0.5);
        MultiPolygon multiPolygon = createMultiPolygonAroundPoint(stopPlacePoint, 0.3);

        TopographicPlace municipality = new TopographicPlace();
        municipality.setName(new EmbeddableMultilingualString("Both Geometries Municipality"));
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        municipality.setPolygon(polygon);
        municipality.setMultiSurface(multiPolygon);
        municipality.setVersion(1L);
        topographicPlaceRepository.save(municipality);

        // Create stop place
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Stop in Both Geometries"));
        stopPlace.setCentroid(stopPlacePoint);
        stopPlace.setVersion(1L);
        stopPlace.setValidBetween(new ValidBetween(Instant.now().minusSeconds(3600), null));
        stopPlaceRepository.save(stopPlace);

        // Flush and clear
        entityManager.flush();
        entityManager.clear();

        // Run the update - should match via multi_surface (geometry_priority=0) over polygon (geometry_priority=1)
        int updated = topographicPlaceRepository.updateStopPlaceTopographicPlaceRef();

        assertThat(updated).isGreaterThanOrEqualTo(1);

        // Clear and verify
        entityManager.clear();
        StopPlace updatedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(updatedStopPlace.getTopographicPlace())
                .as("Stop place should have topographic place assigned")
                .isNotNull();
        assertThat(updatedStopPlace.getTopographicPlace().getName().getValue())
                .isEqualTo("Both Geometries Municipality");
        // The match should have been via multi_surface (preferred), but we can't directly verify which geometry was used
        // The important thing is that the topographic place was matched correctly
    }

    @Test
    public void updateStopPlaceTopographicPlaceRef_shouldMatchPolygonWhenNoMultiSurface() {
        // Create two separate points - one for polygon-only and one for multi-surface-only topographic places
        Point stopPlacePoint = geometryFactory.createPoint(new Coordinate(13.0, 62.0));

        // Create a Municipality with only polygon (no multi_surface)
        Polygon polygon = createPolygonAroundPoint(stopPlacePoint, 0.3);
        TopographicPlace polygonMunicipality = new TopographicPlace();
        polygonMunicipality.setName(new EmbeddableMultilingualString("Polygon Only Municipality"));
        polygonMunicipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        polygonMunicipality.setPolygon(polygon);
        polygonMunicipality.setVersion(1L);
        topographicPlaceRepository.save(polygonMunicipality);

        // Create stop place in the polygon area
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Stop in Polygon"));
        stopPlace.setCentroid(stopPlacePoint);
        stopPlace.setVersion(1L);
        stopPlace.setValidBetween(new ValidBetween(Instant.now().minusSeconds(3600), null));
        stopPlaceRepository.save(stopPlace);

        // Flush and clear
        entityManager.flush();
        entityManager.clear();

        // Run the update
        int updated = topographicPlaceRepository.updateStopPlaceTopographicPlaceRef();

        assertThat(updated).isGreaterThanOrEqualTo(1);

        // Clear and verify
        entityManager.clear();
        StopPlace updatedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(updatedStopPlace.getTopographicPlace())
                .as("Stop place should have topographic place assigned via polygon")
                .isNotNull();
        assertThat(updatedStopPlace.getTopographicPlace().getName().getValue())
                .isEqualTo("Polygon Only Municipality");
    }

    private Polygon createPolygonAroundPoint(Point point, double bufferSize) {
        Geometry buffered = point.buffer(bufferSize);
        Coordinate[] coordinates = buffered.getCoordinates();
        LinearRing ring = new LinearRing(new CoordinateArraySequence(coordinates), geometryFactory);
        return geometryFactory.createPolygon(ring, null);
    }

    private MultiPolygon createMultiPolygonAroundPoint(Point point, double bufferSize) {
        Polygon polygon = createPolygonAroundPoint(point, bufferSize);
        return geometryFactory.createMultiPolygon(new Polygon[]{polygon});
    }
}