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

package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.TypeBuilder;
import net.opengis.gml._3.AbstractRingPropertyType;
import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LinearRingType;
import net.opengis.gml._3.ObjectFactory;
import net.opengis.gml._3.PolygonType;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.geo.DoubleValuesToCoordinateSequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
        }.build(), new MappingContext(new HashMap<>()));

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
        }.build(), new MappingContext(new HashMap<>()));

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
        }.build(), new MappingContext(new HashMap<>()));
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotEmpty();

        List<Double> values = polygonConverter.extractValues(actual.getExterior());
        assertThat(values).hasSize(coordinates.length * 2);


        // Tiamat is storing polygons with X, Y
        // In NeTEx we receive polygons with Y, X
        // Expect Y, X when converting to PolygonType (Netex)
        int counter = 0;
        for(Coordinate coordinate : coordinates) {
            assertThat(values.get(counter++).doubleValue()).isEqualTo(coordinate.y);
            assertThat(values.get(counter++).doubleValue()).isEqualTo(coordinate.x);
        }
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
        }.build(), new MappingContext(new HashMap<>()));
        assertThat(actual).isNotNull();

        List<Double> actualDoublevalues = polygonConverter.extractValues(actual.getExterior());
        assertThat(actualDoublevalues).hasSize(coordinates.length * 2);

        List<Double> actualHoleDoubleValues = polygonConverter.extractValues(actual.getInterior().getFirst());
        assertThat(actualHoleDoubleValues).hasSize(coordinates.length * 2);

    }

    private void assertCoordinatesMatch(LineString actual, List<Double> expectedExteriorValues, String description) {
        int counter = 0;
        for (Coordinate coordinate : actual.getCoordinates()) {
            assertThat(coordinate.y).as(description + " x coordinate").isEqualTo(expectedExteriorValues.get(counter++));
            assertThat(coordinate.x).as(description + " y coordinate").isEqualTo(expectedExteriorValues.get(counter++));
        }
    }

    private void assertInteriorRingsMatch(Polygon actual, List<List<Double>> expectedInteriorValues) {
        for (int interiorIndex = 0; interiorIndex < actual.getNumInteriorRing(); interiorIndex++) {
            assertCoordinatesMatch(actual.getInteriorRingN(interiorIndex), expectedInteriorValues.getFirst(), "interior ring number " + interiorIndex);
        }
    }


}