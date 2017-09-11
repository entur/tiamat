package org.rutebanken.tiamat.service.stopplace;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
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

        Instant timeOfTermination = savedStopPlace.getValidBetween().getFromDate().plusMillis(1);

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
}
