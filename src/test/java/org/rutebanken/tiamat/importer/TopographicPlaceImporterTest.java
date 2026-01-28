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

package org.rutebanken.tiamat.importer;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.model.ValidBetween;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class TopographicPlaceImporterTest extends TiamatIntegrationTest {

    @Autowired
    private TopographicPlaceImporter topographicPlaceImporter;

    @Test
    public void importPointOfInterest() {

        Instant testStarted = Instant.now();

        TopographicPlace poi = new TopographicPlace(new EmbeddableMultilingualString("name"));
        poi.setVersion(2L);
        poi.setTopographicPlaceType(TopographicPlaceTypeEnumeration.PLACE_OF_INTEREST);
        poi.setValidBetween(new ValidBetween(Instant.EPOCH));
        Geometry geometry =  geometryFactory.createPoint(new Coordinate(9.84, 59.26)).buffer(20);
        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(geometry.getCoordinates()), geometryFactory);
        poi.setPolygon(geometryFactory.createPolygon(linearRing, null));
        topographicPlaceRepository.save(poi);

        TopographicPlace poiNew = new TopographicPlace(new EmbeddableMultilingualString("name"));
        poiNew.setNetexId(poi.getNetexId());
        poiNew.setTopographicPlaceType(TopographicPlaceTypeEnumeration.PLACE_OF_INTEREST);
        poiNew.setVersion(5L);
        LinearRing linearRingNew = new LinearRing(new CoordinateArraySequence(new Coordinate[] {new Coordinate(12, 12), new Coordinate(12, 12), new Coordinate(12, 12), new Coordinate(12, 12)}), geometryFactory);
        poiNew.setPolygon(geometryFactory.createPolygon(linearRingNew, null));

        AtomicInteger counter = new AtomicInteger();
        List<org.rutebanken.netex.model.TopographicPlace> imported = topographicPlaceImporter.importTopographicPlaces(Arrays.asList(poiNew), counter);

        assertThat(imported).isNotNull().hasSize(1);

        List<TopographicPlace> actuals = topographicPlaceRepository.findByNetexId(poi.getNetexId());

        assertThat(actuals)
                .as("Imported topographic place of type poi should be updating existing version")
                .hasSize(1);

        assertThat(actuals.getFirst().getValidBetween()).isEqualTo(poi.getValidBetween());
        assertThat(actuals.getFirst().getVersion()).isEqualTo(poi.getVersion());
        assertThat(actuals.getFirst().getChanged()).isAfterOrEqualTo(testStarted);
        assertThat(actuals.getFirst().getPolygon()).isEqualTo(poiNew.getPolygon());

    }

    @Test
    public void importTopographicPlaceWithPolygon() {
        // Create a single polygon
        Coordinate[] polygonCoordinates = new Coordinate[] {
                new Coordinate(9.0, 59.0),
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(9.0, 60.0),
                new Coordinate(9.0, 59.0)  // Close the ring
        };
        LinearRing polygonRing = new LinearRing(new CoordinateArraySequence(polygonCoordinates), geometryFactory);
        Polygon polygon = geometryFactory.createPolygon(polygonRing, null);

        // Create TopographicPlace with ONLY polygon (no multiSurface) - traditional behavior
        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("Place with polygon only"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        topographicPlace.setPolygon(polygon);
        topographicPlace.setVersion(1L);


        AtomicInteger counter = new AtomicInteger();
        List<org.rutebanken.netex.model.TopographicPlace> imported = topographicPlaceImporter.importTopographicPlaces(List.of(topographicPlace), counter);

        assertThat(imported).isNotNull().hasSize(1);

        List<TopographicPlace> actuals = topographicPlaceRepository.findByNetexId(topographicPlace.getNetexId());

        assertThat(actuals)
                .as("Imported topographic place of type poi should be updating existing version")
                .hasSize(1);

        assertThat(actuals.getFirst().getValidBetween()).isEqualTo(topographicPlace.getValidBetween());
        assertThat(actuals.getFirst().getVersion()).isEqualTo(topographicPlace.getVersion());
        assertThat(actuals.getFirst().getPolygon()).isEqualTo(topographicPlace.getPolygon());

    }

    @Test
    public void importTopographicPlaceWithMultiSurface() {
        // Create two separate polygons for the multiSurface property (disconnected areas)
        Coordinate[] multiPolygon1Coordinates = new Coordinate[] {
                new Coordinate(11.0, 61.0),
                new Coordinate(12.0, 61.0),
                new Coordinate(12.0, 62.0),
                new Coordinate(11.0, 62.0),
                new Coordinate(11.0, 61.0)  // Close the ring
        };
        LinearRing multiPolygon1Ring = new LinearRing(new CoordinateArraySequence(multiPolygon1Coordinates), geometryFactory);
        Polygon multiPolygon1 = geometryFactory.createPolygon(multiPolygon1Ring, null);

        Coordinate[] multiPolygon2Coordinates = new Coordinate[] {
                new Coordinate(13.0, 63.0),
                new Coordinate(14.0, 63.0),
                new Coordinate(14.0, 64.0),
                new Coordinate(13.0, 64.0),
                new Coordinate(13.0, 63.0)  // Close the ring
        };
        LinearRing multiPolygon2Ring = new LinearRing(new CoordinateArraySequence(multiPolygon2Coordinates), geometryFactory);
        Polygon multiPolygon2 = geometryFactory.createPolygon(multiPolygon2Ring, null);

        MultiPolygon multiSurface = geometryFactory.createMultiPolygon(new Polygon[] { multiPolygon1, multiPolygon2 });

        // Create TopographicPlace with ONLY polygon (no multiSurface) - traditional behavior
        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("Place with muulti surface only"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        topographicPlace.setMultiSurface(multiSurface);
        topographicPlace.setVersion(1L);


        AtomicInteger counter = new AtomicInteger();
        List<org.rutebanken.netex.model.TopographicPlace> imported = topographicPlaceImporter.importTopographicPlaces(List.of(topographicPlace), counter);

        assertThat(imported).isNotNull().hasSize(1);

        List<TopographicPlace> actuals = topographicPlaceRepository.findByNetexId(topographicPlace.getNetexId());

        assertThat(actuals)
                .as("Imported topographic place of type poi should be updating existing version")
                .hasSize(1);

        assertThat(actuals.getFirst().getValidBetween()).isEqualTo(topographicPlace.getValidBetween());
        assertThat(actuals.getFirst().getVersion()).isEqualTo(topographicPlace.getVersion());
        final MultiPolygon multiSurface1 = actuals.getFirst().getMultiSurface();
        assertThat(multiSurface1).isEqualTo(topographicPlace.getMultiSurface());
        assertThat(multiSurface1.getNumGeometries()).isEqualTo(2);

    }


}