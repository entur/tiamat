package org.rutebanken.tiamat.netex.mapping.converter;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import ma.glasnost.orika.metadata.TypeBuilder;
import net.opengis.gml._3.*;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.geo.DoubleValuesToCoordinateSequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PolygonConverterTest {

    private static final net.opengis.gml._3.ObjectFactory openGisObjectFactory = new ObjectFactory();

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();
    private final DoubleValuesToCoordinateSequence doubleValuesToCoordinateSequence = new DoubleValuesToCoordinateSequence();
    private PolygonConverter polygonConverter = new PolygonConverter(geometryFactory, doubleValuesToCoordinateSequence);

    @Test
    public void convertFrom() throws Exception {
        List<Double> values = new ArrayList<>();
        values.add(9.8468);
        values.add(59.2649);
        values.add(9.8456);
        values.add(59.2654);
        values.add(9.8457);
        values.add(59.2655);
        values.add(values.get(0));
        values.add(values.get(1));

        DirectPositionListType positionList = new DirectPositionListType().withValue(values);

        LinearRingType linearRing = new LinearRingType()
                .withPosList(positionList);

        PolygonType polygonType = new PolygonType()
                .withId("KVE-07")
                .withExterior(new AbstractRingPropertyType()
                        .withAbstractRing(openGisObjectFactory.createLinearRing(linearRing)));

        Polygon polygon = polygonConverter.convertFrom(polygonType, new TypeBuilder<Polygon>() {
        }.build());

        assertThat(polygon).isExactlyInstanceOf(Polygon.class).isNotNull();
        assertThat(polygon.getExteriorRing().getCoordinates()).hasSize(values.size() / 2);
        assertCoordinatesMatch(polygon.getExteriorRing(), values, "Exterior ring");
    }

    @Test
    public void convertFromWithHoles() throws Exception {
        List<Double> values = new ArrayList<>();
        values.add(9.8468);
        values.add(59.2649);
        values.add(9.8456);
        values.add(59.2654);
        values.add(9.8457);
        values.add(59.2655);
        values.add(values.get(0));
        values.add(values.get(1));

        DirectPositionListType positionList = new DirectPositionListType().withValue(values);

        LinearRingType linearRing = new LinearRingType()
                .withPosList(positionList);

        PolygonType polygonType = new PolygonType()
                .withId("KVE-07")
                .withExterior(new AbstractRingPropertyType()
                        .withAbstractRing(openGisObjectFactory.createLinearRing(linearRing)))
                .withInterior(new AbstractRingPropertyType().withAbstractRing(openGisObjectFactory.createLinearRing(linearRing)));

        Polygon polygon = polygonConverter.convertFrom(polygonType, new TypeBuilder<Polygon>() {
        }.build());

        assertThat(polygon).isNotNull();
        assertThat(polygon.getExteriorRing().getCoordinates()).hasSize(values.size() / 2);
        assertThat(polygon.getNumInteriorRing()).isEqualTo(1);
        assertCoordinatesMatch(polygon.getExteriorRing(), values, "Exterior ring");
        assertInteriorRingsMatch(polygon, Arrays.asList(values));
    }

    @Test
    public void convertTo() throws Exception {

        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(9.8468, 59.2649),
                new Coordinate(9.8456, 59.2654),
                new Coordinate(9.8457, 59.2655),
                new Coordinate(9.8468, 59.2649)};

        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(coordinates), geometryFactory);
        Polygon polygon = new Polygon(linearRing, null, geometryFactory);

        PolygonType actual = polygonConverter.convertTo(polygon, new TypeBuilder<PolygonType>() {
        }.build());
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotEmpty();

        List<Double> values = polygonConverter.extractValues(actual.getExterior());
        assertThat(values).hasSize(coordinates.length * 2);

    }

    @Test
    public void convertToWithHoles() throws Exception {

        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(9.8468, 59.2649),
                new Coordinate(9.8456, 59.2654),
                new Coordinate(9.8457, 59.2655),
                new Coordinate(9.8468, 59.2649)};

        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(coordinates), geometryFactory);
        LinearRing[] holes = new LinearRing[] { new LinearRing(new CoordinateArraySequence(coordinates), geometryFactory)};
        Polygon polygon = new Polygon(linearRing, holes, geometryFactory);

        PolygonType actual = polygonConverter.convertTo(polygon, new TypeBuilder<PolygonType>() {
        }.build());
        assertThat(actual).isNotNull();

        List<Double> actualDoublevalues = polygonConverter.extractValues(actual.getExterior());
        assertThat(actualDoublevalues).hasSize(coordinates.length * 2);

        List<Double> actualHoleDoubleValues = polygonConverter.extractValues(actual.getInterior().get(0));
        assertThat(actualHoleDoubleValues).hasSize(coordinates.length * 2);

    }

    private void assertCoordinatesMatch(LineString actual, List<Double> expectedExteriorValues, String description) {
        int counter = 0;
        for (Coordinate coordinate : actual.getCoordinates()) {
            assertThat(coordinate.x).as(description + " x coordinate").isEqualTo(expectedExteriorValues.get(counter++));
            assertThat(coordinate.y).as(description + " y coordinate").isEqualTo(expectedExteriorValues.get(counter++));
        }
    }

    private void assertInteriorRingsMatch(Polygon actual, List<List<Double>> expectedInteriorValues) {
        for (int interiorIndex = 0; interiorIndex < actual.getNumInteriorRing(); interiorIndex++) {
            assertCoordinatesMatch(actual.getInteriorRingN(interiorIndex), expectedInteriorValues.get(0), "interior ring number " + interiorIndex);
        }
    }


}