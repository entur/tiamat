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
import org.rutebanken.tiamat.geo.DoubleValuesToCoordinateSequence;

import java.math.BigInteger;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class LineStringConverterTest {

    private static GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    private LineStringConverter lineStringConverter = new LineStringConverter(geometryFactory, new DoubleValuesToCoordinateSequence());

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
        assertThat(gisLineString.getId()).isNotEmpty();
        // Check that the format is Y,X
        assertThat(gisLineString.getPosList().getValue().get(0)).isEqualTo(coordinates[0].y);
        assertThat(gisLineString.getPosList().getValue().get(1)).isEqualTo(coordinates[0].x);
        assertThat(gisLineString.getPosList().getValue().get(2)).isEqualTo(coordinates[1].y);
        assertThat(gisLineString.getPosList().getValue().get(3)).isEqualTo(coordinates[1].x);

        assertThat(gisLineString.getSrsDimension().intValue()).isEqualTo(2);
    }

    @Test
    public void convertToLineString() throws Exception {

        LineStringType lineStringType = new LineStringType()
                .withId("LineString")
                .withPosList(new DirectPositionListType()
                        .withSrsDimension(BigInteger.valueOf(geometryFactory.getSRID()))
                        .withValue(71.1, 9.1, 4.1, 9.5));

        LineString lineString = lineStringConverter.convertTo(lineStringType, new TypeBuilder<LineString>(){}.build());
        assertThat(lineString).isNotNull();
        assertThat(lineString.getCoordinates()).hasSize(2);
        assertThat(lineString.getCoordinates()[0].x).isEqualTo(lineStringType.getPosList().getValue().get(1));
        assertThat(lineString.getCoordinates()[0].y).isEqualTo(lineStringType.getPosList().getValue().get(0));
        assertThat(lineString.getCoordinates()[1].x).isEqualTo(lineStringType.getPosList().getValue().get(3));
        assertThat(lineString.getCoordinates()[1].y).isEqualTo(lineStringType.getPosList().getValue().get(2));

    }
}