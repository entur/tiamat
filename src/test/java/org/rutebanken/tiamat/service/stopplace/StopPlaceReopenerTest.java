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

        Instant timeOfTermination = savedStopPlace.getValidBetween().getFromDate().plusMillis(1);

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

