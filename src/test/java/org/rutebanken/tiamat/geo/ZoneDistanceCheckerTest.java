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

package org.rutebanken.tiamat.geo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;

import static org.assertj.core.api.Assertions.assertThat;


public class ZoneDistanceCheckerTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();


    private ZoneDistanceChecker zoneDistanceChecker = new ZoneDistanceChecker();

    @Test
    public void exceedsLimit() {


        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("name1"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));

        StopPlace stopPlace2 = new StopPlace(new EmbeddableMultilingualString("name2"));
        stopPlace2.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));

        boolean actual = zoneDistanceChecker.exceedsLimit(stopPlace, stopPlace2, 30);

        assertThat(actual).isFalse();
    }
}