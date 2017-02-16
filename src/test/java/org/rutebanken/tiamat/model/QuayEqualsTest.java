package org.rutebanken.tiamat.model;

import com.vividsolutions.jts.geom.*;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;

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

        Quay quay1 = new Quay(new EmbeddableMultilingualString("Ellas minne"));
        Quay quay2 = new Quay(new EmbeddableMultilingualString("Ellas minne"));

        quay1.setCentroid(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
        assertThat(quay1).isEqualTo(quay2);
    }

    @Test
    public void quaysWithDifferentNameButSameCoordinates() {

        double longitude = 39.61441;
        double latitude = -144.22765;

        Quay quay1 = new Quay(new EmbeddableMultilingualString("Ellas minne"));
        Quay quay2 = new Quay(new EmbeddableMultilingualString("Different"));

        quay1.setCentroid(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
        assertThat(quay1).isNotEqualTo(quay2);
    }

    @Test
    public void quaysWithSameNameButDifferentCoordinates() {
        Quay quay1 = new Quay(new EmbeddableMultilingualString("Ellas minne"));
        Quay quay2 = new Quay(new EmbeddableMultilingualString("Ellas minne"));

        quay1.setCentroid(geometryFactory.createPoint(new Coordinate(70, 80)));
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(60, 50)));
        assertThat(quay1).isNotEqualTo(quay2);
    }

    @Test
    public void quaysWithNoCoordinatesAndSameName() {
        Quay quay1 = new Quay(new EmbeddableMultilingualString("Ellas minne"));
        Quay quay2 = new Quay(new EmbeddableMultilingualString("Ellas minne"));
        assertThat(quay1).isEqualTo(quay2);
    }

    @Test
    public void quaysWithDifferentIdInKeyValNotEqual() {
        Quay quay1 = new Quay(new EmbeddableMultilingualString("Ellas minne"));
        quay1.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("1");
        Quay quay2 = new Quay(new EmbeddableMultilingualString("Ellas minne"));
        quay2.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("12");
        assertThat(quay1).isNotEqualTo(quay2);
    }

    @Test
    public void quaysWithSameNameAndIdEquals() {
        Quay quay1 = new Quay(new EmbeddableMultilingualString("Ellas minne"));
        quay1.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("1");
        Quay quay2 = new Quay(new EmbeddableMultilingualString("Ellas minne"));
        quay2.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("1");
        assertThat(quay1).isEqualTo(quay2);
    }

    @Test
    public void quaysWithSameNameAndIdEqualsEvenIfOrderDiffers() {
        Quay quay1 = new Quay(new EmbeddableMultilingualString("Ellas minne"));
        quay1.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("1");
        quay1.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("2");
        Quay quay2 = new Quay(new EmbeddableMultilingualString("Ellas minne"));
        quay2.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("2");
        quay2.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("1");
        assertThat(quay1).isEqualTo(quay2);
    }

    @Test
    public void quaysWithSlightlyDifferentCoordinatesShouldNotBeEqaual() {
        double quayLatitude = 59.4221750629462661663637845776975154876708984375;
        double quayLongitude = 5.2646351097871768587310725706629455089569091796875;

        String name = "Name";
        Quay quay1 = new Quay(new EmbeddableMultilingualString(name));
        quay1.setId(987987L); // The ID should not influence the equals method.
        Quay quay2 = new Quay(new EmbeddableMultilingualString(name));
        quay2.setId(987987L);

        quay1.setCentroid(geometryFactory.createPoint(new Coordinate(quayLongitude, quayLatitude)));
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(quayLongitude + 0.01, quayLatitude + 0.01)));
        assertThat(quay1).isNotEqualTo(quay2);
    }

    @Test
    public void quaysWithDifferentPlateCodeIsNotEqual() {
        Quay first = new Quay();
        first.setPlateCode("X");

        Quay second = new Quay();
        second.setPlateCode("Y");
        assertThat(first).isNotEqualTo(second);
    }
}
