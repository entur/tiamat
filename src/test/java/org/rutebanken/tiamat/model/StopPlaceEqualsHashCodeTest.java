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

package org.rutebanken.tiamat.model;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;

import java.util.HashSet;

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
    public void hashCodeShouldBeEqual() {
        StopPlace stopPlace = createStopPlaceWithQuayAndCentroid("Lillehammer", 12, 23);
        StopPlace stopPlace2 = createStopPlaceWithQuayAndCentroid("Lillehammer", 12, 23);
        assertThat(stopPlace.hashCode()).isEqualTo(stopPlace2.hashCode());
    }

    @Test
    public void hashCodeShouldNotBeEqualWithDifferentName() {
        StopPlace stopPlace = createStopPlaceWithQuayAndCentroid("Åndalsnes", 12, 23);
        StopPlace stopPlace2 = createStopPlaceWithQuayAndCentroid("Lillehammer", 12, 23);
        assertThat(stopPlace.hashCode()).isNotEqualTo(stopPlace2.hashCode());
    }

    private StopPlace createStopPlaceWithQuayAndCentroid(String name, double longitude, double latitude) {
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString(name));
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(name));
        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
        return stopPlace;
    }

}
