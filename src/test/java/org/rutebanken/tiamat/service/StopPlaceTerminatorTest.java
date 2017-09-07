package org.rutebanken.tiamat.service;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class StopPlaceTerminatorTest extends TiamatIntegrationTest {

    @Autowired
    private StopPlaceTerminator stopPlaceTerminator;

    @Autowired
    private StopPlaceReopener stopPlaceReopener;

    @Transactional
    @Test
    public void testTerminateAndReopenStopPlace() {

        StopPlace savedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(new StopPlace(new EmbeddableMultilingualString("Name")));
        String stopPlaceNetexId = savedStopPlace.getNetexId();

        Instant timeOfTermination = savedStopPlace.getValidBetween().getFromDate().plusMillis(1);

        StopPlace terminatedStopPlace = stopPlaceTerminator.terminateStopPlace(stopPlaceNetexId, timeOfTermination, "Terminating Stop");

        String reopenedVersionComment = "Reopened StopPlace";

        // Act
        StopPlace reopenedStopPlace = stopPlaceReopener.reopenStopPlace(stopPlaceNetexId, reopenedVersionComment);

        assertThat(reopenedStopPlace).isNotNull();
        assertThat(reopenedStopPlace.getVersion()).isGreaterThan(terminatedStopPlace.getVersion());
        assertThat(reopenedStopPlace.getVersionComment()).isEqualTo(reopenedVersionComment);
        assertThat(reopenedStopPlace.getValidBetween().getFromDate())
                .as("Reopened stop place from date").isAfterOrEqualTo(timeOfTermination);
        assertThat(reopenedStopPlace.getValidBetween().getToDate()).isNull();
    }
}
