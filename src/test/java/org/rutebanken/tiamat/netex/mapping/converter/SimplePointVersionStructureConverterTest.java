package org.rutebanken.tiamat.netex.mapping.converter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.junit.Test;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SimplePointVersionStructureConverterTest {

    private final SimplePointVersionStructureConverter simplePointVersionStructureConverter = new SimplePointVersionStructureConverter();
    private final GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();
    private final Type<Point> pointType = new TypeBuilder<Point>() {}.build();
    private final Type<SimplePoint_VersionStructure> simplePointVersionStructureType = new TypeBuilder<SimplePoint_VersionStructure>() {}.build();


    @Test
    public void convertNetexPositionToPoint() {
        double longitude = 10.01;
        double latitude = 20.24;
        SimplePoint_VersionStructure simplePointversionStructure = new SimplePoint_VersionStructure()
                .withLocation(new LocationStructure()
                        .withLongitude(BigDecimal.valueOf(longitude))
                        .withLatitude(BigDecimal.valueOf(latitude)));
        Point point = simplePointVersionStructureConverter.convertFrom(simplePointversionStructure, pointType);
        assertThat(point).isNotNull();
        assertThat(point.getX()).isEqualTo(longitude);
        assertThat(point.getY()).isEqualTo(latitude);
    }

    @Test
    public void convertPointToNetex() {
        double longitude = 10.01;
        double latitude = 20.24;
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        SimplePoint_VersionStructure simplePointVersionStructure = simplePointVersionStructureConverter.convertTo(point, simplePointVersionStructureType);
        assertThat(simplePointVersionStructure).isNotNull();
        assertThat(simplePointVersionStructure.getLocation().getLatitude().doubleValue()).isEqualTo(latitude);
        assertThat(simplePointVersionStructure.getLocation().getLongitude().doubleValue()).isEqualTo(longitude);

    }

    @Test
    public void allowMaxSixDecimalsWhenConvertingToNetex() {
        double longitude = 10.123456789;
        double latitude = 20.123123123123;
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        SimplePoint_VersionStructure simplePointversionStructure =  simplePointVersionStructureConverter.convertTo(point, simplePointVersionStructureType);

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
        Point point = simplePointVersionStructureConverter.convertFrom(simplePointversionStructure, pointType);

        assertThat(point.getX()).isEqualTo(10.123457);
        assertThat(point.getY()).isEqualTo(20.123123);
    }

    @Test
    public void nullCheckLocation() {
        SimplePoint_VersionStructure simplePointversionStructure = new SimplePoint_VersionStructure();
        simplePointVersionStructureConverter.convertFrom(simplePointversionStructure, pointType);
    }

    @Test
    public void nullCheckSimplePoint() {
        SimplePoint_VersionStructure simplePointversionStructure = null;
        simplePointVersionStructureConverter.convertFrom(simplePointversionStructure, pointType);
    }

    @Test
    public void nullCheckLatitude() {
        SimplePoint_VersionStructure simplePointversionStructure = new SimplePoint_VersionStructure()
                .withLocation(new LocationStructure().withLongitude(BigDecimal.valueOf(10.00)));
        simplePointVersionStructureConverter.convertFrom(simplePointversionStructure, pointType);
    }

    @Test
    public void nullCheckLongitude() {
        SimplePoint_VersionStructure simplePointversionStructure = new SimplePoint_VersionStructure()
                .withLocation(new LocationStructure().withLatitude(BigDecimal.valueOf(10.00)));
        simplePointVersionStructureConverter.convertFrom(simplePointversionStructure, pointType);
    }
}