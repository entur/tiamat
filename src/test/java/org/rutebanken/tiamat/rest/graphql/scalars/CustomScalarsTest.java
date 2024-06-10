package org.rutebanken.tiamat.rest.graphql.scalars;

import graphql.GraphQLContext;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CustomScalarsTest {


    @Test
    public void testNonStandardSerializePolygon() {
        CustomScalars customScalars = new CustomScalars();
        Coordinate[] coordinates = Arrays.asList(new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(1, 1), new Coordinate(0, 0)).toArray(new Coordinate[4]);

        Object result = customScalars.GraphQLLegacyGeoJSONCoordinates.getCoercing().serialize(coordinates, GraphQLContext.getDefault(), Locale.getDefault());
        assertNotNull(result);
        assertTrue(result instanceof Iterable);
        ((Iterable) result).forEach(o -> {
            assertTrue(o instanceof Iterable);
            assertEquals(2, ((Iterable) o).spliterator().getExactSizeIfKnown());
        });
    }

    @Test
    public void testStandardSerializePolygon() {
        CustomScalars customScalars = new CustomScalars();
        Coordinate[] coordinates = Arrays.asList(new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(1, 1), new Coordinate(0, 0)).toArray(new Coordinate[4]);

        Object result = customScalars.GraphQLGeoJSONCoordinates.getCoercing().serialize(coordinates, GraphQLContext.getDefault(), Locale.getDefault());
        assertNotNull(result);
        assertTrue(result instanceof Iterable);
        ((Iterable) result).forEach(o -> {
            assertTrue(o instanceof Iterable);
            assertEquals(4, ((Iterable) o).spliterator().getExactSizeIfKnown());
        });
    }

    @Test
    public void testNonStandardSerializePoint() {
        CustomScalars customScalars = new CustomScalars();
        Coordinate[] coordinates = Arrays.asList(new Coordinate(10, 20)).toArray(new Coordinate[1]);

        Object result = customScalars.GraphQLLegacyGeoJSONCoordinates.getCoercing().serialize(coordinates, GraphQLContext.getDefault(), Locale.getDefault());
        assertNotNull(result);

    }

    @Test
    public void testStandardSerializePoint() {
        final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        CustomScalars customScalars = new CustomScalars();
        Coordinate coordinates = new Coordinate(10, 20);

        final Point point = geometryFactory.createPoint(coordinates);


        Object result = customScalars.GraphQLGeoJSONCoordinates.getCoercing().serialize(point.getCoordinates(), GraphQLContext.getDefault(), Locale.getDefault());
        assertNotNull(result);

    }

    @Test
    public void testParseValuePolygon() {
        CustomScalars customScalars = new CustomScalars();


        Object result = customScalars.GraphQLLegacyGeoJSONCoordinates.getCoercing().parseValue(getLists(), GraphQLContext.getDefault(), Locale.getDefault());
        assertNotNull(result);
        assertTrue(result instanceof Coordinate[]);
        assertEquals(3, ((Coordinate[]) result).length);
    }

    @Test
    public void testParseValueStandardPolygon() {
        CustomScalars customScalars = new CustomScalars();
        List<List<List<Double>>> coordinateList = new ArrayList<>();
        final List<List<Double>> subList = getLists();
        coordinateList.add(subList);

        Object result = customScalars.GraphQLGeoJSONCoordinates.getCoercing().parseValue(coordinateList, GraphQLContext.getDefault(), Locale.getDefault());
        assertNotNull(result);
        assertTrue(result instanceof Coordinate[]);
        assertEquals(3, ((Coordinate[]) result).length);
    }

    @Test
    public void testParseValuePoint() {
        CustomScalars customScalars = new CustomScalars();
        List<List<Double>> coordinateList = new ArrayList<>();
        final List<Double> subList = getLists().getFirst();
        coordinateList.add(subList);

        Object result = customScalars.GraphQLLegacyGeoJSONCoordinates.getCoercing().parseValue(coordinateList, GraphQLContext.getDefault(), Locale.getDefault());
        assertNotNull(result);
        assertTrue(result instanceof Coordinate[]);
        assertEquals(1, ((Coordinate[]) result).length);
    }

    @Test
    public void testParseValueStandardPoint() {
        CustomScalars customScalars = new CustomScalars();
        final List<Double> point = getLists().getFirst();


        Object result = customScalars.GraphQLGeoJSONCoordinates.getCoercing().parseValue(point, GraphQLContext.getDefault(), Locale.getDefault());
        assertNotNull(result);
        assertTrue(result instanceof Coordinate[]);
        assertEquals(1, ((Coordinate[]) result).length);
    }


    private static @NotNull List<List<Double>> getLists() {
        List<List<Double>> subList = new ArrayList<>();
        List<Double> coordinatePair = new ArrayList<>();
        coordinatePair.add(0.0);
        coordinatePair.add(0.0);
        subList.add(coordinatePair);
        List<Double> coordinatePair2 = new ArrayList<>();
        coordinatePair2.add(1.0);
        coordinatePair2.add(0.0);
        subList.add(coordinatePair2);
        List<Double> coordinatePair3 = new ArrayList<>();
        coordinatePair3.add(1.1);
        coordinatePair3.add(0.0);
        subList.add(coordinatePair3);
        return subList;
    }


}