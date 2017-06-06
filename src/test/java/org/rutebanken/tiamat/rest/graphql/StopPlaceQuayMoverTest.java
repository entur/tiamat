package org.rutebanken.tiamat.rest.graphql;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class StopPlaceQuayMoverTest extends TiamatIntegrationTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private StopPlaceQuayMover stopPlaceQuayMover;

    @Test
    public void moveQuayToExistingStop() {

        StopPlace fromStopPlace = new StopPlace();

        Quay quayToMove = new Quay(new EmbeddableMultilingualString("quay to be moved"));
        quayToMove.setVersion(1L);
        fromStopPlace.getQuays().add(quayToMove);
        fromStopPlace.setVersion(1L);
        stopPlaceRepository.save(fromStopPlace);

        StopPlace destinationStopPlace = new StopPlace(new EmbeddableMultilingualString("Destination stop place"));
        destinationStopPlace.setVersion(1L);
        stopPlaceRepository.save(destinationStopPlace);

        StopPlace result = stopPlaceQuayMover.moveQuays(Arrays.asList(quayToMove.getNetexId()), destinationStopPlace.getNetexId(), null);

        assertThat(result.getNetexId()).isEqualTo(destinationStopPlace.getNetexId());
        assertThat(result.getQuays()).hasSize(1);
        assertThat(result.getVersion()).isEqualTo(2L);

        Quay actualQuay = result.getQuays().iterator().next();
        assertThat(actualQuay.getName()).isNotNull();
        assertThat(actualQuay.getVersion()).isEqualTo(2L);
        assertThat(actualQuay.getName().getValue()).isEqualTo(quayToMove.getName().getValue());

        fromStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(fromStopPlace.getNetexId());
        assertThat(fromStopPlace.getQuays()).isEmpty();
        assertThat(fromStopPlace.getVersion()).as("new version of source stop place with no quays").isEqualTo(2L);

        destinationStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(destinationStopPlace.getNetexId());
        assertThat(destinationStopPlace).isEqualTo(result);
    }

    @Test
    public void moveQuayToNewStop() {

        StopPlace fromStopPlace = new StopPlace();

        Quay quayToMove = new Quay(new EmbeddableMultilingualString("quay to be moved 2"));
        quayToMove.setVersion(1L);
        fromStopPlace.getQuays().add(quayToMove);
        fromStopPlace.setVersion(1L);
        stopPlaceRepository.save(fromStopPlace);

        StopPlace result = stopPlaceQuayMover.moveQuays(Arrays.asList(quayToMove.getNetexId()), null, null);

        fromStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(fromStopPlace.getNetexId());
        assertThat(fromStopPlace.getQuays()).isEmpty();
        assertThat(fromStopPlace.getVersion()).as("new version of source stop place with no quays").isEqualTo(2L);

        StopPlace createdStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(result.getNetexId());
        assertThat(createdStopPlace.getQuays()).hasSize(1);
        Quay actualQuay = createdStopPlace.getQuays().iterator().next();
        assertThat(actualQuay.getName()).isNotNull();
        assertThat(actualQuay.getVersion()).isEqualTo(2L);
        assertThat(actualQuay.getName().getValue()).isEqualTo(quayToMove.getName().getValue());
        assertThat(result).isEqualTo(createdStopPlace);

    }

    @Test(expected = IllegalArgumentException.class)
    public void doNotAcceptInvalidQuayId() {
        stopPlaceQuayMover.moveQuays(Arrays.asList("NSR:Quay:99999999"), null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void doNotAcceptInvalidStopPlaceDestionationId() {
        StopPlace fromStopPlace = new StopPlace();

        Quay quayToMove = new Quay(new EmbeddableMultilingualString("quay to be moved 4"));
        quayToMove.setVersion(1L);
        fromStopPlace.getQuays().add(quayToMove);
        fromStopPlace.setVersion(1L);
        stopPlaceRepository.save(fromStopPlace);

        stopPlaceQuayMover.moveQuays(Arrays.asList(quayToMove.getNetexId()), "NSR:StopPlace:91919191", null);
    }
}