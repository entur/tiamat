package org.rutebanken.tiamat.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;

import static org.assertj.core.api.Assertions.assertThat;

public class QuayHashCodeTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    @Test
    public void sameHashCodeForEqualsQuays() {
        double longitude = 39.61441;
        double latitude = -144.22765;

        Quay quay1 = new Quay(new EmbeddableMultilingualString("Ellas minne"));
        Quay quay2 = new Quay(new EmbeddableMultilingualString("Ellas minne"));

        quay1.setCentroid(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
        assertThat(quay1.hashCode()).isEqualTo(quay2.hashCode());
    }
}
