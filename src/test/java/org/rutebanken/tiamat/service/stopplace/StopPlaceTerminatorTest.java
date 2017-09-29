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
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.ValidBetween;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StopPlaceTerminatorTest extends TiamatIntegrationTest {

    @Autowired
    private StopPlaceTerminator stopPlaceTerminator;

    @Transactional
    @Test
    public void testTerminateStopPlace() {

        StopPlace savedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(new StopPlace(new EmbeddableMultilingualString("Name")));
        String stopPlaceNetexId = savedStopPlace.getNetexId();

        Instant timeOfTermination = savedStopPlace.getValidBetween().getFromDate().plusSeconds(30);

        System.out.println("Terminate at " + timeOfTermination);
        StopPlace terminatedStopPlace = stopPlaceTerminator.terminateStopPlace(stopPlaceNetexId, timeOfTermination, "Terminating Stop");

        assertThat(terminatedStopPlace.getValidBetween().getToDate()).isNotNull();
    }

    @Transactional
    @Test
    public void testCannotTerminateChild() {

        StopPlace savedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(new StopPlace(new EmbeddableMultilingualString("Name")));
        savedStopPlace.setParentSiteRef(new SiteRefStructure("x", "2"));
        stopPlaceRepository.save(savedStopPlace);
        String stopPlaceNetexId = savedStopPlace.getNetexId();

        assertThatThrownBy(() -> stopPlaceTerminator.terminateStopPlace(stopPlaceNetexId, Instant.now(), "Terminating Stop")).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * When attempting to terminate stop place that is already terminated, expect exception.
     */
    @Transactional
    @Test(expected = IllegalArgumentException.class)
    public void testTerminateStopPlaceWithFromDate() {

        Instant now = Instant.now();

        StopPlace savedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(new StopPlace(new EmbeddableMultilingualString("LIAB")));
        stopPlaceRepository.save(savedStopPlace);
        savedStopPlace.setValidBetween(new ValidBetween(now, now.plusSeconds(10)));
        String stopPlaceNetexId = savedStopPlace.getNetexId();

        Instant terminateDate = now.plusSeconds(20);
        System.out.println(terminateDate);
        stopPlaceTerminator.terminateStopPlace(stopPlaceNetexId, terminateDate, "Terminating Stop");
    }
}
