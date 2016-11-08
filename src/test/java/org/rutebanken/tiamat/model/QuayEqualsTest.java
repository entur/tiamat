package org.rutebanken.tiamat.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;

import static org.assertj.core.api.Assertions.assertThat;

public class QuayEqualsTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    @Test
    public void quaysWithSameNameAndCoordinatesEquals() {
        /**
         * Quay{id=52036,
         *      name=Ellas minne (no),
         *      keyValues={imported-id=Value{id=52042,
         *      items=[NOR:StopArea:1805014601]}}}
         *
         *  Quay{name=Ellas minne (no),
         *      keyValues={imported-id=Value{id=0, items=[TRO:StopArea:1805014601]}}}
         */

        double longitude = 39.61441;
        double latitude = -144.22765;

        Quay quay1 = new Quay(new MultilingualString("Ellas minne"));
        Quay quay2 = new Quay(new MultilingualString("Ellas minne"));

        quay1.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(longitude, latitude)))));
        quay2.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(longitude, latitude)))));
        assertThat(quay1).isEqualTo(quay2);
    }

    @Test
    public void quaysWithDifferentNameButSameCoordinates() {

        double longitude = 39.61441;
        double latitude = -144.22765;

        Quay quay1 = new Quay(new MultilingualString("Ellas minne"));
        Quay quay2 = new Quay(new MultilingualString("Different"));

        quay1.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(longitude, latitude)))));
        quay2.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(longitude, latitude)))));
        assertThat(quay1).isNotEqualTo(quay2);
    }

    @Test
    public void quaysWithSameNameButDifferentCoordinates() {
        Quay quay1 = new Quay(new MultilingualString("Ellas minne"));
        Quay quay2 = new Quay(new MultilingualString("Ellas minne"));

        quay1.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(70, 80)))));
        quay2.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(60, 50)))));
        assertThat(quay1).isNotEqualTo(quay2);
    }

    @Test
    public void quaysWithNoCoordinatesAndSameName() {
        Quay quay1 = new Quay(new MultilingualString("Ellas minne"));
        Quay quay2 = new Quay(new MultilingualString("Ellas minne"));
        assertThat(quay1).isEqualTo(quay2);
    }
}
