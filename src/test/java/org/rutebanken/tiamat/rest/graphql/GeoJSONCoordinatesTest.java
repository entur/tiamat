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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for GeoJSON coordinate conversion.
 * Verifies that JTS geometries are correctly converted to GeoJSON-compliant coordinates.
 */
public class GeoJSONCoordinatesTest {

    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Test
    public void testPointToGeoJSONCoordinates() {
        Point point = geometryFactory.createPoint(new Coordinate(10.5, 59.9));

        List<Double> coords = pointToGeoJSON(point);

        assertEquals(2, coords.size());
        assertEquals(10.5, coords.get(0), 0.001);
        assertEquals(59.9, coords.get(1), 0.001);
    }

    @Test
    public void testSimplePolygonToGeoJSONCoordinates() {
        Coordinate[] shell = new Coordinate[] {
            new Coordinate(0, 0),
            new Coordinate(10, 0),
            new Coordinate(10, 10),
            new Coordinate(0, 10),
            new Coordinate(0, 0)
        };
        Polygon polygon = geometryFactory.createPolygon(shell);

        List<List<List<Double>>> coords = polygonToGeoJSON(polygon);

        // Should have 1 ring (exterior only)
        assertEquals(1, coords.size());
        // Exterior ring should have 5 coordinates
        assertEquals(5, coords.get(0).size());
        // First coordinate
        assertEquals(0.0, coords.get(0).get(0).get(0), 0.001);
        assertEquals(0.0, coords.get(0).get(0).get(1), 0.001);
    }

    @Test
    public void testPolygonWithHoleToGeoJSONCoordinates() {
        // Exterior ring
        Coordinate[] shell = new Coordinate[] {
            new Coordinate(0, 0),
            new Coordinate(10, 0),
            new Coordinate(10, 10),
            new Coordinate(0, 10),
            new Coordinate(0, 0)
        };
        LinearRing exteriorRing = geometryFactory.createLinearRing(shell);

        // Hole (interior ring)
        Coordinate[] hole = new Coordinate[] {
            new Coordinate(2, 2),
            new Coordinate(8, 2),
            new Coordinate(8, 8),
            new Coordinate(2, 8),
            new Coordinate(2, 2)
        };
        LinearRing holeRing = geometryFactory.createLinearRing(hole);

        Polygon polygon = geometryFactory.createPolygon(exteriorRing, new LinearRing[] { holeRing });

        List<List<List<Double>>> coords = polygonToGeoJSON(polygon);

        // Should have 2 rings (exterior + 1 hole)
        assertEquals("Polygon with hole should have 2 rings", 2, coords.size());

        // Exterior ring should have 5 coordinates
        assertEquals(5, coords.get(0).size());
        // First coordinate of exterior
        assertEquals(0.0, coords.get(0).get(0).get(0), 0.001);
        assertEquals(0.0, coords.get(0).get(0).get(1), 0.001);

        // Hole ring should have 5 coordinates
        assertEquals(5, coords.get(1).size());
        // First coordinate of hole
        assertEquals(2.0, coords.get(1).get(0).get(0), 0.001);
        assertEquals(2.0, coords.get(1).get(0).get(1), 0.001);
    }

    @Test
    public void testMultiPolygonToGeoJSONCoordinates() {
        // First polygon
        Coordinate[] shell1 = new Coordinate[] {
            new Coordinate(0, 0),
            new Coordinate(5, 0),
            new Coordinate(5, 5),
            new Coordinate(0, 5),
            new Coordinate(0, 0)
        };
        Polygon polygon1 = geometryFactory.createPolygon(shell1);

        // Second polygon (disconnected)
        Coordinate[] shell2 = new Coordinate[] {
            new Coordinate(10, 10),
            new Coordinate(15, 10),
            new Coordinate(15, 15),
            new Coordinate(10, 15),
            new Coordinate(10, 10)
        };
        Polygon polygon2 = geometryFactory.createPolygon(shell2);

        MultiPolygon multiPolygon = geometryFactory.createMultiPolygon(new Polygon[] { polygon1, polygon2 });

        List<List<List<List<Double>>>> coords = multiPolygonToGeoJSON(multiPolygon);

        // Should have 2 polygons
        assertEquals("MultiPolygon should have 2 polygons", 2, coords.size());

        // First polygon has 1 ring with 5 coordinates
        assertEquals(1, coords.get(0).size());
        assertEquals(5, coords.get(0).get(0).size());
        assertEquals(0.0, coords.get(0).get(0).get(0).get(0), 0.001);

        // Second polygon has 1 ring with 5 coordinates
        assertEquals(1, coords.get(1).size());
        assertEquals(5, coords.get(1).get(0).size());
        assertEquals(10.0, coords.get(1).get(0).get(0).get(0), 0.001);
    }

    @Test
    public void testMultiPolygonWithHolesCoordinates() {
        // First polygon with a hole
        LinearRing shell1 = geometryFactory.createLinearRing(new Coordinate[] {
            new Coordinate(0, 0),
            new Coordinate(10, 0),
            new Coordinate(10, 10),
            new Coordinate(0, 10),
            new Coordinate(0, 0)
        });
        LinearRing hole1 = geometryFactory.createLinearRing(new Coordinate[] {
            new Coordinate(2, 2),
            new Coordinate(8, 2),
            new Coordinate(8, 8),
            new Coordinate(2, 8),
            new Coordinate(2, 2)
        });
        Polygon polygon1 = geometryFactory.createPolygon(shell1, new LinearRing[] { hole1 });

        // Second polygon (simple, no holes)
        Coordinate[] shell2 = new Coordinate[] {
            new Coordinate(20, 20),
            new Coordinate(25, 20),
            new Coordinate(25, 25),
            new Coordinate(20, 25),
            new Coordinate(20, 20)
        };
        Polygon polygon2 = geometryFactory.createPolygon(shell2);

        MultiPolygon multiPolygon = geometryFactory.createMultiPolygon(new Polygon[] { polygon1, polygon2 });

        List<List<List<List<Double>>>> coords = multiPolygonToGeoJSON(multiPolygon);

        // Should have 2 polygons
        assertEquals(2, coords.size());

        // First polygon has 2 rings (exterior + hole)
        assertEquals("First polygon should have 2 rings", 2, coords.get(0).size());

        // Second polygon has 1 ring
        assertEquals("Second polygon should have 1 ring", 1, coords.get(1).size());
    }

    // Helper methods that mirror the implementation in StopPlaceRegisterGraphQLSchema

    private List<Double> pointToGeoJSON(Point point) {
        Coordinate coord = point.getCoordinate();
        return List.of(coord.x, coord.y);
    }

    private List<List<List<Double>>> polygonToGeoJSON(Polygon polygon) {
        java.util.ArrayList<List<List<Double>>> rings = new java.util.ArrayList<>();

        // Add exterior ring
        rings.add(coordinatesToList(polygon.getExteriorRing().getCoordinates()));

        // Add interior rings (holes)
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            rings.add(coordinatesToList(polygon.getInteriorRingN(i).getCoordinates()));
        }

        return rings;
    }

    private List<List<List<List<Double>>>> multiPolygonToGeoJSON(MultiPolygon multiPolygon) {
        java.util.ArrayList<List<List<List<Double>>>> polygons = new java.util.ArrayList<>();
        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
            Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
            polygons.add(polygonToGeoJSON(polygon));
        }
        return polygons;
    }

    private List<List<Double>> coordinatesToList(Coordinate[] coordinates) {
        java.util.ArrayList<List<Double>> coordList = new java.util.ArrayList<>();
        for (Coordinate coord : coordinates) {
            coordList.add(List.of(coord.x, coord.y));
        }
        return coordList;
    }
}