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

package org.rutebanken.tiamat.service.stopplace;

import com.vividsolutions.jts.geom.Coordinate;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.service.stopplace.StopPlaceDeleter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class StopPlaceDeleterIntegrationTest extends TiamatIntegrationTest {

    @Autowired
    private StopPlaceDeleter stopPlaceDeleter;

    @Transactional
    @Test
    public void testDeleteStopPlace() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));
        stopPlace.getOriginalIds().add("TEST:StopPlace:1234");
        stopPlace.getOriginalIds().add("TEST:StopPlace:5678");

        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("testQuay"));
        stopPlace.getQuays().add(quay);

        //Saving two versions to verify that both are deleted
        StopPlace tmpStopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        StopPlace savedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(tmpStopPlace,
                stopPlaceVersionedSaverService.createCopy(tmpStopPlace, StopPlace.class));

        Assertions.assertThat(tmpStopPlace.getNetexId()).isEqualTo(savedStopPlace.getNetexId());
        Assertions.assertThat(tmpStopPlace.getVersion()).isLessThan(savedStopPlace.getVersion());

        String stopPlaceNetexId = savedStopPlace.getNetexId();
        String quayNetexId = stopPlace.getQuays().iterator().next().getNetexId();

        StopPlace fetchedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId);

        Assertions.assertThat(fetchedStopPlace).isNotNull();
        Assertions.assertThat(fetchedStopPlace.getQuays()).isNotNull();
        Assertions.assertThat(fetchedStopPlace.getQuays()).hasSize(1);

        Quay fetchedQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quayNetexId);
        Assertions.assertThat(fetchedQuay).isNotNull();

        boolean isDeleted = stopPlaceDeleter.deleteStopPlace(stopPlaceNetexId);

        Assertions.assertThat(isDeleted);

        StopPlace deletedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId);
        Assertions.assertThat(deletedStopPlace).isNull();

        Quay deletedQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quayNetexId);
        // Verify that associated quay is also deleted
        Assertions.assertThat(deletedQuay).isNull();
    }
}