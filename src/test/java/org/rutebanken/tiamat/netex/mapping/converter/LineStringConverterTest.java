package org.rutebanken.tiamat.netex.mapping.converter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import ma.glasnost.orika.metadata.TypeBuilder;
import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LineStringType;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class LineStringConverterTest {

    private static GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    private LineStringConverter lineStringConverter = new LineStringConverter(geometryFactory);

    @Test
    public void convertFromLineStringToNetexGmlLineStringType() throws Exception {

        Coordinate[] coordinates = new Coordinate[2];
        coordinates[0] = new Coordinate(11, 60);
        coordinates[1] = new Coordinate(11.1, 60.1);

        CoordinateSequence points = new CoordinateArraySequence(coordinates);

        LineString lineString = new LineString(points, geometryFactory);

        LineStringType gisLineString = lineStringConverter.convertFrom(lineString, new TypeBuilder<LineStringType>() {}.build());
        assertThat(gisLineString).isNotNull();
        assertThat(gisLineString.getPosList().getCount().intValue()).isEqualTo(4);
        assertThat(gisLineString.getPosList().getSrsDimension().intValue()).isEqualTo(geometryFactory.getSRID());
        // assertThat(gisLineString.getPosList().getSrsName()).isEqualTo("WGS84");

    }

    @Test
    public void convertToLineString() throws Exception {

        LineStringType lineStringType = new LineStringType()
                .withId("LineString")
                .withPosList(new DirectPositionListType()
                        .withSrsName("WGS84")
                        .withValue(9.1,
                                71.1,
                                9.5,
                                74.1));

        LineString lineString = lineStringConverter.convertTo(lineStringType, new TypeBuilder<LineString>(){}.build());
        assertThat(lineString).isNotNull();
        assertThat(lineString.getCoordinates()).hasSize(2);

    }
}