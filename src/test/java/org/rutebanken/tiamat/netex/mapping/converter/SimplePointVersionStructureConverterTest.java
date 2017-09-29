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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import net.opengis.gml._3.DirectPositionType;
import org.assertj.core.data.Percentage;
import org.junit.Test;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class SimplePointVersionStructureConverterTest {

    private final SimplePointVersionStructureConverter simplePointVersionStructureConverter = new SimplePointVersionStructureConverter(new GeometryFactoryConfig().geometryFactory());
    private final GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();
    private final Type<Point> pointType = new TypeBuilder<Point>() {}.build();
    private final Type<SimplePoint_VersionStructure> simplePointVersionStructureType = new TypeBuilder<SimplePoint_VersionStructure>() {}.build();

    private final MappingContext mappingContext = new MappingContext(new HashMap<>());

    @Test
    public void convertNetexPositionToPoint() {
        double longitude = 10.01;
        double latitude = 20.24;
        SimplePoint_VersionStructure simplePointversionStructure = new SimplePoint_VersionStructure()
                .withLocation(new LocationStructure()
                        .withLongitude(BigDecimal.valueOf(longitude))
                        .withLatitude(BigDecimal.valueOf(latitude)));
        Point point = simplePointVersionStructureConverter.convertFrom(simplePointversionStructure, pointType, mappingContext);
        assertThat(point).isNotNull();
        assertThat(point.getX()).isEqualTo(longitude);
        assertThat(point.getY()).isEqualTo(latitude);
    }

    @Test
    public void convertPointToNetex() {
        double longitude = 10.01;
        double latitude = 20.24;
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        SimplePoint_VersionStructure simplePointVersionStructure = simplePointVersionStructureConverter.convertTo(point, simplePointVersionStructureType, mappingContext);
        assertThat(simplePointVersionStructure).isNotNull();
        assertThat(simplePointVersionStructure.getLocation().getLatitude().doubleValue()).isEqualTo(latitude);
        assertThat(simplePointVersionStructure.getLocation().getLongitude().doubleValue()).isEqualTo(longitude);

    }

    @Test
    public void allowMaxSixDecimalsWhenConvertingToNetex() {
        double longitude = 10.123456789;
        double latitude = 20.123123123123;
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        SimplePoint_VersionStructure simplePointversionStructure =  simplePointVersionStructureConverter.convertTo(point, simplePointVersionStructureType, mappingContext);

        assertThat(simplePointversionStructure.getLocation().getLongitude().doubleValue()).isEqualTo(10.123457);
        assertThat(simplePointversionStructure.getLocation().getLatitude().doubleValue()).isEqualTo(20.123123);
    }

    @Test
    public void allowMaxSixDecimalsWhenConvertingToPoint() {
        double longitude = 10.123456789;
        double latitude = 20.123123123123;
        SimplePoint_VersionStructure simplePointversionStructure = new SimplePoint_VersionStructure()
                .withLocation(new LocationStructure()
                        .withLongitude(BigDecimal.valueOf(longitude))
                        .withLatitude(BigDecimal.valueOf(latitude)));
        Point point = simplePointVersionStructureConverter.convertFrom(simplePointversionStructure, pointType, mappingContext);

        assertThat(point.getX()).isEqualTo(10.123457);
        assertThat(point.getY()).isEqualTo(20.123123);
    }

    @Test
    public void nullCheckLocation() {
        SimplePoint_VersionStructure simplePointversionStructure = new SimplePoint_VersionStructure();
        simplePointVersionStructureConverter.convertFrom(simplePointversionStructure, pointType, mappingContext);
    }

    @Test
    public void nullCheckSimplePoint() {
        SimplePoint_VersionStructure simplePointversionStructure = null;
        simplePointVersionStructureConverter.convertFrom(simplePointversionStructure, pointType, mappingContext);
    }

    @Test
    public void nullCheckLatitude() {
        SimplePoint_VersionStructure simplePointversionStructure = new SimplePoint_VersionStructure()
                .withLocation(new LocationStructure().withLongitude(BigDecimal.valueOf(10.00)));
        simplePointVersionStructureConverter.convertFrom(simplePointversionStructure, pointType, mappingContext);
    }

    @Test
    public void nullCheckLongitude() {
        SimplePoint_VersionStructure simplePointversionStructure = new SimplePoint_VersionStructure()
                .withLocation(new LocationStructure().withLatitude(BigDecimal.valueOf(10.00)));
        simplePointVersionStructureConverter.convertFrom(simplePointversionStructure, pointType, mappingContext);
    }

    @Test
    public void importUtmGML() {

        SimplePoint_VersionStructure simplePointversionStructure = new SimplePoint_VersionStructure()
                .withLocation(
                        new LocationStructure()
                                .withPos(
                                        new DirectPositionType()
                                                .withValue(6583758.0, 514477.0)
                                                .withSrsName("EPSG:32632")));

        Point point = simplePointVersionStructureConverter.convertFrom(simplePointversionStructure, pointType, mappingContext);
        assertNotNull(point);

        assertThat(point.getX()).isCloseTo(9.25, Percentage.withPercentage(2));
        assertThat(point.getY()).isCloseTo(59.39, Percentage.withPercentage(2));

    }

    @Test
    public void importUtmLongLat() {

        SimplePoint_VersionStructure simplePointversionStructure = new SimplePoint_VersionStructure()
                .withLocation(
                        new LocationStructure()
                                .withLatitude(BigDecimal.valueOf(6583758.0))
                                .withLongitude(BigDecimal.valueOf(514477.0))
                                .withSrsName("EPSG:32632"));

        Point point = simplePointVersionStructureConverter.convertFrom(simplePointversionStructure, pointType, mappingContext);
        assertNotNull(point);

        assertThat(point.getX()).isCloseTo(9.25, Percentage.withPercentage(2));
        assertThat(point.getY()).isCloseTo(59.39, Percentage.withPercentage(2));

    }
}