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

    @Test
    public void quaysWithDifferentPublicCodeShouldNotHaveEqualHashCode() {
        Quay first = new Quay();
        first.setPublicCode("X");

        Quay second = new Quay();
        second.setPublicCode("Y");
        assertThat(first.hashCode()).isNotEqualTo(second.hashCode());
    }
}
