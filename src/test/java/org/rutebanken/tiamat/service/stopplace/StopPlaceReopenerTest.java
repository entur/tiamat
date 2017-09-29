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

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.service.stopplace.StopPlaceTerminator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class StopPlaceReopenerTest extends TiamatIntegrationTest {

    @Autowired
    private StopPlaceTerminator stopPlaceTerminator;

    @Transactional
    @Test
    public void testReopenStopPlace() {

        StopPlace savedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(new StopPlace(new EmbeddableMultilingualString("Name")));
        String stopPlaceNetexId = savedStopPlace.getNetexId();

        long latestVersion = savedStopPlace.getVersion();

        Instant timeOfTermination = savedStopPlace.getValidBetween().getFromDate().plusSeconds(10);

        String terminatedVersionComment = "Terminating Stop";
        StopPlace terminatedStopPlace = stopPlaceTerminator.terminateStopPlace(stopPlaceNetexId, timeOfTermination, terminatedVersionComment);

        assertThat(terminatedStopPlace).isNotNull();

        terminatedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId);
        assertThat(terminatedStopPlace).isNotNull();
        assertThat(terminatedStopPlace.getVersion()).isGreaterThan(latestVersion);
        assertThat(terminatedStopPlace.getVersionComment()).isEqualTo(terminatedVersionComment);
        assertThat(terminatedStopPlace.getValidBetween().getToDate()).isNotNull();
        assertThat(terminatedStopPlace.getValidBetween().getToDate()).isEqualTo(timeOfTermination);
    }
}

