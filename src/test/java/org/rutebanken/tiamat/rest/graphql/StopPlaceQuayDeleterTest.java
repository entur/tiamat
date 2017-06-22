package org.rutebanken.tiamat.rest.graphql;

import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Test;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

public class StopPlaceQuayDeleterTest extends AbstractGraphQLResourceIntegrationTest  {


    @Autowired
    StopPlaceQuayDeleter stopPlaceQuayDeleter;

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

        assertThat(tmpStopPlace.getNetexId()).isEqualTo(savedStopPlace.getNetexId());
        assertThat(tmpStopPlace.getVersion()).isLessThan(savedStopPlace.getVersion());

        String stopPlaceNetexId = savedStopPlace.getNetexId();
        String quayNetexId = stopPlace.getQuays().iterator().next().getNetexId();

        StopPlace fetchedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId);

        assertThat(fetchedStopPlace).isNotNull();
        assertThat(fetchedStopPlace.getQuays()).isNotNull();
        assertThat(fetchedStopPlace.getQuays()).hasSize(1);

        Quay fetchedQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quayNetexId);
        assertThat(fetchedQuay).isNotNull();

        boolean isDeleted = stopPlaceQuayDeleter.deleteStopPlace(stopPlaceNetexId);

        assertThat(isDeleted);

        StopPlace deletedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId);
        assertThat(deletedStopPlace).isNull();

        Quay deletedQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quayNetexId);
        // Verify that associated quay is also deleted
        assertThat(deletedQuay).isNull();
    }

    @Transactional
    @Test
    public void testDeleteQuay() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));
        stopPlace.getOriginalIds().add("TEST:StopPlace:1234");
        stopPlace.getOriginalIds().add("TEST:StopPlace:5678");

        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("testQuay"));
        stopPlace.getQuays().add(quay);

        Quay quay2 = new Quay();
        quay2.setName(new EmbeddableMultilingualString("testQuay2"));
        stopPlace.getQuays().add(quay2);

        StopPlace savedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String stopPlaceNetexId = savedStopPlace.getNetexId();
        String quayNetexId  = stopPlace.getQuays().iterator().next().getNetexId();

        StopPlace fetchedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId);

        assertThat(fetchedStopPlace).isNotNull();
        assertThat(fetchedStopPlace.getQuays()).isNotNull();
        assertThat(fetchedStopPlace.getQuays()).hasSize(2);

        Quay fetchedQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quayNetexId);
        assertThat(fetchedQuay).isNotNull();

        String versionComment = "Deleting quay";
        StopPlace updated = stopPlaceQuayDeleter.deleteQuay(stopPlaceNetexId, quayNetexId, versionComment);

        assertThat(updated).isNotNull();
        assertThat(updated.getQuays()).isNotNull();
        assertThat(updated.getQuays()).hasSize(1);
        assertThat(updated.getVersionComment()).isEqualTo(versionComment);

    }
}
