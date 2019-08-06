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

package org.rutebanken.tiamat.service;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class TopographicPlaceLookupServiceTest extends TiamatIntegrationTest {

    @Autowired
    private TopographicPlaceLookupService topographicPlaceLookupService;

    /**
     * Ensure that a stale version of a topographic place can exist without from date and to date
     * and still getting the most recent topographic place from the lookup service.
     * <p>
     * (Old data might not have validity condition stored, as versioning for topographic places was implemented after
     * the initial baselining of data)
     * <p>
     * Test is implemented after merging of two counties (Sør- and Nordtrøndelag), were municipalities got new IDs.
     */
    @Test
    @DirtiesContext
    public void findTopographicPlace() {

        Point point = geometryFactory.createPoint(new Coordinate(9.84, 59.26));
        Geometry geometry = point.buffer(20);
        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(geometry.getCoordinates()), geometryFactory);

        TopographicPlace staleTopographicPlace = new TopographicPlace(new EmbeddableMultilingualString("Skaun"));
        staleTopographicPlace.setCreated(Instant.EPOCH);
        staleTopographicPlace.setPolygon(geometryFactory.createPolygon(linearRing, null));
        staleTopographicPlace.setNetexId("KVE:TopographicPlace:1657");
        staleTopographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);

        TopographicPlace newTopographicPlace = new TopographicPlace(new EmbeddableMultilingualString("Skaun"));
        newTopographicPlace.setCreated(Instant.now());
        newTopographicPlace.setPolygon(geometryFactory.createPolygon(linearRing, null));
        newTopographicPlace.setNetexId("KVE:TopographicPlace:5029");
        newTopographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);

        topographicPlaceRepository.save(staleTopographicPlace);
        topographicPlaceRepository.save(newTopographicPlace);
        topographicPlaceRepository.flush();

        topographicPlaceLookupService.reset();
        Optional<TopographicPlace> result = topographicPlaceLookupService.findTopographicPlace(point);
        assertThat(result.isPresent()).describedAs("Found topographic place?").isTrue();
        assertThat(result.get().getNetexId()).as("Topographic place found").isEqualTo(newTopographicPlace.getNetexId());
    }
}