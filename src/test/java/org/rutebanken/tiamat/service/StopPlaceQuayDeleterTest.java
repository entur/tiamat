package org.rutebanken.tiamat.service;

import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Test;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.rest.graphql.AbstractGraphQLResourceIntegrationTest;
import org.rutebanken.tiamat.service.StopPlaceQuayDeleter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class StopPlaceQuayDeleterTest extends AbstractGraphQLResourceIntegrationTest {


    @Autowired
    StopPlaceQuayDeleter stopPlaceQuayDeleter;

    @Transactional
    @Test
    public void testTerminateAndReopenStopPlace() {

        StopPlace savedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(new StopPlace(new EmbeddableMultilingualString("Name")));
        String stopPlaceNetexId = savedStopPlace.getNetexId();

        StopPlace fetchedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId);

        assertThat(fetchedStopPlace).isNotNull();
        assertThat(fetchedStopPlace.getValidBetween().getToDate()).isNull();

        long latestVersion = fetchedStopPlace.getVersion();

        Instant timeOfTermination = fetchedStopPlace.getValidBetween().getFromDate().plusMillis(1);

        String terminatedVersionComment = "Terminating Stop";
        StopPlace terminatedStopPlace = stopPlaceQuayDeleter.terminateStopPlace(stopPlaceNetexId, timeOfTermination, terminatedVersionComment);

        assertThat(terminatedStopPlace).isNotNull();

        terminatedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId);
        assertThat(terminatedStopPlace).isNotNull();
        assertThat(terminatedStopPlace.getVersion()).isGreaterThan(latestVersion);
        assertThat(terminatedStopPlace.getVersionComment()).isEqualTo(terminatedVersionComment);
        assertThat(terminatedStopPlace.getValidBetween().getToDate()).isNotNull();
        assertThat(terminatedStopPlace.getValidBetween().getToDate()).isEqualTo(timeOfTermination);


        String reopenedVersionComment = "Reopened StopPlace";
        StopPlace reopenedStopPlace = stopPlaceQuayDeleter.reopenStopPlace(stopPlaceNetexId, reopenedVersionComment);
        assertThat(reopenedStopPlace).isNotNull();
        assertThat(reopenedStopPlace.getVersion()).isGreaterThan(terminatedStopPlace.getVersion());
        assertThat(reopenedStopPlace.getVersionComment()).isEqualTo(reopenedVersionComment);
        assertThat(reopenedStopPlace.getValidBetween().getFromDate())
                .as("Reopened stop place from date").isAfterOrEqualTo(timeOfTermination);
        assertThat(reopenedStopPlace.getValidBetween().getToDate()).isNull();
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
