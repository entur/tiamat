package org.rutebanken.tiamat.service;

import com.vividsolutions.jts.geom.Coordinate;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.changelog.EntityChangedListener;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StopPlaceDeleterIntegrationTest extends TiamatIntegrationTest {

    @Autowired
    private StopPlaceDeleter stopPlaceDeleter;

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

        Assertions.assertThat(tmpStopPlace.getNetexId()).isEqualTo(savedStopPlace.getNetexId());
        Assertions.assertThat(tmpStopPlace.getVersion()).isLessThan(savedStopPlace.getVersion());

        String stopPlaceNetexId = savedStopPlace.getNetexId();
        String quayNetexId = stopPlace.getQuays().iterator().next().getNetexId();

        StopPlace fetchedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId);

        Assertions.assertThat(fetchedStopPlace).isNotNull();
        Assertions.assertThat(fetchedStopPlace.getQuays()).isNotNull();
        Assertions.assertThat(fetchedStopPlace.getQuays()).hasSize(1);

        Quay fetchedQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quayNetexId);
        Assertions.assertThat(fetchedQuay).isNotNull();

        boolean isDeleted = stopPlaceDeleter.deleteStopPlace(stopPlaceNetexId);

        Assertions.assertThat(isDeleted);

        StopPlace deletedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId);
        Assertions.assertThat(deletedStopPlace).isNull();

        Quay deletedQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quayNetexId);
        // Verify that associated quay is also deleted
        Assertions.assertThat(deletedQuay).isNull();
    }
}