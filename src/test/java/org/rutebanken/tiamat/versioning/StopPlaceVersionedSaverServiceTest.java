package org.rutebanken.tiamat.versioning;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class StopPlaceVersionedSaverServiceTest extends TiamatIntegrationTest {


    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Test
    public void testNewStopPlaceWithQuay() {
        Quay quay1 = new Quay();
        quay1.setName(new EmbeddableMultilingualString("quay1"));

        Quay quay2 = new Quay();
        quay2.setName(new EmbeddableMultilingualString("quay2"));

        StopPlace stopPlace = new StopPlace();
        stopPlace.getQuays().add(quay1);
        stopPlace.getQuays().add(quay2);

        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(actualStopPlace).isNotNull();
        assertThat(actualStopPlace.getVersion()).isEqualTo(1);
        actualStopPlace.getQuays().forEach(quay -> assertThat(quay.getVersion()).isEqualTo(1));
    }

    @Test
    public void noValidbetweenOnChildObjects() {
        Quay quay = new Quay();
        StopPlace stopPlace = new StopPlace();
        stopPlace.getQuays().add(quay);

        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());
        assertThat(actualStopPlace.getQuays().iterator().next().getValidBetweens()).isEmpty();
    }

    @Test
    public void testUpdateStopPlaceWithQuay() {
        Quay quay1 = new Quay();
        quay1.setName(new EmbeddableMultilingualString("quay1"));

        Quay quay2 = new Quay();
        quay2.setName(new EmbeddableMultilingualString("quay2"));

        StopPlace stopPlace = new StopPlace();
        String initialStopPlaceName = "Initial name";
        stopPlace.setName(new EmbeddableMultilingualString(initialStopPlaceName));
        stopPlace.getQuays().add(quay1);
        stopPlace.getQuays().add(quay2);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        // Verify that object-references are different
        assertThat(stopPlace != actualStopPlace);

        assertThat(actualStopPlace).isNotNull();
        assertThat(actualStopPlace.getVersion()).isEqualTo(1);
        assertThat(actualStopPlace.getName()).isNotNull();
        assertThat(actualStopPlace.getName().getValue()).isEqualTo(initialStopPlaceName);
        actualStopPlace.getQuays().forEach(quay -> assertThat(quay.getVersion()).isEqualTo(1));

        Map<String, String> versionNameMap = new HashMap<>();
        versionNameMap.put(actualStopPlace.getNetexId() + actualStopPlace.getVersion(), initialStopPlaceName);

        long expectedVersion = actualStopPlace.getVersion();
        String stopPlaceName = null;
        for (int i = 0; i < 3; i++) {
            stopPlaceName = "test " + i;
            StopPlace sp = stopPlaceVersionedSaverService.createNewVersion(actualStopPlace);
            sp.setName(new EmbeddableMultilingualString(stopPlaceName));
            actualStopPlace = stopPlaceVersionedSaverService.saveNewVersion(actualStopPlace, sp);

            // Verify that object-references are different
            assertThat(sp != actualStopPlace);

            actualStopPlace = sp;
            versionNameMap.put(actualStopPlace.getNetexId() + actualStopPlace.getVersion(), stopPlaceName);
            expectedVersion++;
        }

        StopPlace updatedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(actualStopPlace.getNetexId());

        assertThat(updatedStopPlace).isNotNull();
        assertThat(updatedStopPlace.getName()).isNotNull();
        assertThat(updatedStopPlace.getName().getValue()).isEqualTo(stopPlaceName);
        assertThat(updatedStopPlace.getVersion()).isEqualTo(expectedVersion);
        updatedStopPlace.getQuays().forEach(quay -> assertThat(quay.getVersion()).isEqualTo(updatedStopPlace.getVersion()));

        List<StopPlace> all = stopPlaceRepository.findAll();
        all.forEach(sp -> {
            assertThat(versionNameMap.get(sp.getNetexId() + sp.getVersion())).isEqualTo(sp.getName().getValue());
        });
    }

    @Test
    public void testUpdateStopPlaceSameObject() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Initial name"));

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        actualStopPlace.setName(new EmbeddableMultilingualString("Failing StopPlace"));

        boolean failedAsExpected = false;
        try {
            StopPlace fail = stopPlaceVersionedSaverService.saveNewVersion(actualStopPlace, actualStopPlace);
            fail("Saving the same version as new version is not allowed");
        } catch (IllegalArgumentException e) {
            //This should be thrown
            assertThat(e.getMessage()).isEqualTo("Existing and new StopPlace must be different objects");
            failedAsExpected = true;
        }
        assertThat(failedAsExpected).isTrue();
    }

    @Test
    public void testUpdateStopPlaceDifferentId() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Initial name"));

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace stopPlace1 = new StopPlace();
        stopPlace1.setName(new EmbeddableMultilingualString("Initial name"));

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace1);

        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());
        StopPlace actualStopPlace1 = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace1.getNetexId());

        actualStopPlace.setName(new EmbeddableMultilingualString("Failing StopPlace"));
        actualStopPlace1.setName(new EmbeddableMultilingualString("Another failing StopPlace"));

        boolean failedAsExpected = false;
        try {
            StopPlace fail = stopPlaceVersionedSaverService.saveNewVersion(actualStopPlace, actualStopPlace1);
            fail("Saving new version of different object is not allowed");
        } catch (IllegalArgumentException e) {
            //This should be thrown
            assertThat(e.getMessage()).isEqualTo("Existing and new StopPlace do not match");
            failedAsExpected = true;
        }
        assertThat(failedAsExpected).isTrue();
    }


}
