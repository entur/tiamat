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
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.stopplace.StopPlaceQuayMover;
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

        StopPlace result = stopPlaceQuayMover.moveQuays(Arrays.asList(quayToMove.getNetexId()), destinationStopPlace.getNetexId(),null, null);

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

        StopPlace result = stopPlaceQuayMover.moveQuays(Arrays.asList(quayToMove.getNetexId()), null, null,null);

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

    @Test
    public void moveQuayBetweenChildStops() {

        StopPlace sourceStopPlace = new StopPlace();
        Quay quayToMove = new Quay();
        quayToMove.setPublicCode("1");
        sourceStopPlace.getQuays().add(quayToMove);
        StopPlace sourceParentStopPlace = new StopPlace(new EmbeddableMultilingualString("parent from stop place"));
        sourceParentStopPlace.getChildren().add(sourceStopPlace);
        sourceParentStopPlace = stopPlaceVersionedSaverService.saveNewVersion(sourceParentStopPlace);

        StopPlace destinationStopPlace = new StopPlace();
        Quay existingQuay = new Quay();
        existingQuay.setPublicCode("2");
        destinationStopPlace.getQuays().add(existingQuay);
        StopPlace parentDestinationStopPlace = new StopPlace(new EmbeddableMultilingualString("destination parent stop place"));
        parentDestinationStopPlace.getChildren().add(destinationStopPlace);
        parentDestinationStopPlace = stopPlaceVersionedSaverService.saveNewVersion(parentDestinationStopPlace);


        StopPlace actualParentDestinationStopPlace = stopPlaceQuayMover.moveQuays(Arrays.asList(quayToMove.getNetexId()), destinationStopPlace.getNetexId(), "from comment", "to comment");


        assertThat(actualParentDestinationStopPlace).isNotNull();
        assertThat(actualParentDestinationStopPlace.getNetexId()).isEqualTo(parentDestinationStopPlace.getNetexId());

        assertThat(actualParentDestinationStopPlace.getChildren()).hasSize(1);

        StopPlace actualDestinationStopPlace = actualParentDestinationStopPlace.getChildren().iterator().next();

        assertThat(actualDestinationStopPlace.getNetexId()).isEqualTo(destinationStopPlace.getNetexId());
        assertThat(actualDestinationStopPlace.getQuays())
                .hasSize(2)
                .extracting(quay -> quay.getNetexId()).contains(quayToMove.getNetexId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void doNotAcceptInvalidQuayId() {
        stopPlaceQuayMover.moveQuays(Arrays.asList("NSR:Quay:99999999"), null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void doNotAcceptInvalidStopPlaceDestionationId() {
        StopPlace fromStopPlace = new StopPlace();

        Quay quayToMove = new Quay(new EmbeddableMultilingualString("quay to be moved 4"));
        quayToMove.setVersion(1L);
        fromStopPlace.getQuays().add(quayToMove);
        fromStopPlace.setVersion(1L);
        stopPlaceRepository.save(fromStopPlace);

        stopPlaceQuayMover.moveQuays(Arrays.asList(quayToMove.getNetexId()), "NSR:StopPlace:91919191", null, null);
    }
}