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

package org.rutebanken.tiamat.importer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.model.ValidBetween;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class TopographicPlaceImporterTest extends TiamatIntegrationTest {

    @Autowired
    private TopographicPlaceImporter topographicPlaceImporter;

    @Test
    public void importPointOfInterest() {

        Instant testStarted = Instant.now();

        TopographicPlace poi = new TopographicPlace(new EmbeddableMultilingualString("name"));
        poi.setVersion(2L);
        poi.setTopographicPlaceType(TopographicPlaceTypeEnumeration.PLACE_OF_INTEREST);
        poi.setValidBetween(new ValidBetween(Instant.EPOCH));
        Geometry geometry =  geometryFactory.createPoint(new Coordinate(9.84, 59.26)).buffer(20);
        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(geometry.getCoordinates()), geometryFactory);
        poi.setPolygon(geometryFactory.createPolygon(linearRing, null));
        topographicPlaceRepository.save(poi);

        TopographicPlace poiNew = new TopographicPlace(new EmbeddableMultilingualString("name"));
        poiNew.setNetexId(poi.getNetexId());
        poiNew.setTopographicPlaceType(TopographicPlaceTypeEnumeration.PLACE_OF_INTEREST);
        poiNew.setVersion(5L);
        LinearRing linearRingNew = new LinearRing(new CoordinateArraySequence(new Coordinate[] {new Coordinate(12, 12), new Coordinate(12, 12), new Coordinate(12, 12), new Coordinate(12, 12)}), geometryFactory);
        poiNew.setPolygon(geometryFactory.createPolygon(linearRingNew, null));

        AtomicInteger counter = new AtomicInteger();
        List<org.rutebanken.netex.model.TopographicPlace> imported = topographicPlaceImporter.importTopographicPlaces(Arrays.asList(poiNew), counter);

        assertThat(imported).isNotNull().hasSize(1);

        List<TopographicPlace> actuals = topographicPlaceRepository.findByNetexId(poi.getNetexId());

        assertThat(actuals)
                .as("Imported topographic place of type poi should be updating existing version")
                .hasSize(1);

        assertThat(actuals.get(0).getValidBetween()).isEqualTo(poi.getValidBetween());
        assertThat(actuals.get(0).getVersion()).isEqualTo(poi.getVersion());
        assertThat(actuals.get(0).getChanged()).isAfterOrEqualTo(testStarted);
        assertThat(actuals.get(0).getPolygon()).isEqualTo(poiNew.getPolygon());

    }


}