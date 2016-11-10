package org.rutebanken.tiamat.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StopPlaceEqualsHashCodeTest {
    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    @Test
    public void stopPlaceWithSameNameShouldBeEqual() {
        StopPlace stopPlace = createStopPlaceWithQuayAndCentroid("Lillehammer", 12.233, 23.33);
        StopPlace stopPlace2 = createStopPlaceWithQuayAndCentroid("Lillehammer", 12.233, 23.33);
        assertThat(stopPlace).isEqualTo(stopPlace2);
    }

    @Test
    public void stopPlaceWithSameNameButDifferentCoordinatesShouldNotBeEqual() {
        StopPlace stopPlace = createStopPlaceWithQuayAndCentroid("Lillehammer", 30, 50);
        StopPlace stopPlace2 = createStopPlaceWithQuayAndCentroid("Lillehammer", 12.233, 23.33);
        assertThat(stopPlace).isNotEqualTo(stopPlace2);
    }

    @Test
    public void hasCodeShouldBeEqual() {
        StopPlace stopPlace = createStopPlaceWithQuayAndCentroid("Lillehammer", 12, 23);
        StopPlace stopPlace2 = createStopPlaceWithQuayAndCentroid("Lillehammer", 12, 23);
        assertThat(stopPlace.hashCode()).isEqualTo(stopPlace2.hashCode());
    }

    @Test
    public void hasCodeShouldNotBeEqualWithDifferentName() {
        StopPlace stopPlace = createStopPlaceWithQuayAndCentroid("Åndalsnes", 12, 23);
        StopPlace stopPlace2 = createStopPlaceWithQuayAndCentroid("Lillehammer", 12, 23);
        assertThat(stopPlace.hashCode()).isNotEqualTo(stopPlace2.hashCode());
    }

    private StopPlace createStopPlaceWithQuayAndCentroid(String name, double longitude, double latitude) {
        Quay quay = new Quay();
        quay.setName(new MultilingualString(name));
        quay.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(longitude, latitude)))));
        StopPlace stopPlace = new StopPlace(new MultilingualString(name));
        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);
        stopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(longitude, latitude)))));
        return stopPlace;
    }

}
