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

package org.rutebanken.tiamat.importer.filter;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.model.Zone_VersionStructure;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ZoneTopographicPlaceFilterTest extends TiamatIntegrationTest {

    @Autowired
    private ZoneTopographicPlaceFilter zoneTopographicPlaceFilter;

    @Test
    public void filterByCountyMatch() throws Exception {

        TopographicPlace county1 = new TopographicPlace(new EmbeddableMultilingualString("county1"));
        county1.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

        Point point = geometryFactory.createPoint(new Coordinate(9.84, 59.26));
        Geometry geometry =  point.buffer(20);
        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(geometry.getCoordinates()), geometryFactory);
        county1.setPolygon(geometryFactory.createPolygon(linearRing, null));

        System.out.println("Polygon for county is:"+county1.getPolygon().toString());

        topographicPlaceRepository.save(county1);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setCentroid(point);

        List<? extends Zone_VersionStructure> list = zoneTopographicPlaceFilter.filterByTopographicPlaceMatch(Collections.singletonList(county1.getNetexId()), List.of(stopPlace));

        assertThat(list).as("List of stops filtered by county").hasSize(1);

        list = zoneTopographicPlaceFilter.filterByTopographicPlaceMatch(Collections.singletonList(county1.getNetexId()), Arrays.asList(stopPlace), true);

        assertThat(list).as("Negated list of stops not in county").isEmpty();

    }

}