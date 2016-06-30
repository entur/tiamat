package no.rutebanken.tiamat.rest.netex.siteframe;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.config.GeometryFactoryConfig;
import no.rutebanken.tiamat.model.*;
import no.rutebanken.tiamat.repository.QuayRepository;
import no.rutebanken.tiamat.repository.StopPlaceRepository;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.concurrent.atomic.AtomicInteger;

import static no.rutebanken.tiamat.rest.netex.siteframe.StopPlaceImporter.ORIGINAL_ID_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StopPlaceImporterTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    private TopographicPlaceCreator topographicPlaceCreator = mock(TopographicPlaceCreator.class);

    private QuayRepository quayRepository = mock(QuayRepository.class);

    private StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);

    private StopPlaceImporter stopPlaceImporter = new StopPlaceImporter(topographicPlaceCreator, quayRepository, stopPlaceRepository);


    @Test
    public void keepOriginalIdsInKeyList() throws Exception {

        final String stopPlaceOriginalId = "stop-place-id-to-replace";
        final String quayOriginalId = "quay-id-to-replace";

        final String persistedStopPlaceId = "persisted-stop-place-id";
        mockStopPlaceRepository(persistedStopPlaceId);

        final String persistedQuayId = "persisted-quay-id";
        mockQuayRepository(persistedQuayId);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setId(stopPlaceOriginalId);
        stopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)))));

        Quay quay = new Quay();
        quay.setId(quayOriginalId);

        stopPlace.getQuays().add(quay);

        StopPlace importedStopPlace = stopPlaceImporter.importStopPlace(stopPlace, new SiteFrame(), new AtomicInteger());

        assertThat(importedStopPlace.getId()).isEqualTo(persistedStopPlaceId);
        assertThat(importedStopPlace.getQuays().get(0).getId()).isEqualTo(persistedQuayId);

        assertThat(importedStopPlace.getKeyList()).isNotNull();
        assertThat(importedStopPlace.getKeyList().getKeyValue()).isNotEmpty();

        KeyValueStructure stopPlaceKeyVal = importedStopPlace
                .getKeyList()
                .getKeyValue()
                .get(0);

        assertThat(stopPlaceKeyVal.getKey()).as("key should be set")
                .isEqualTo(ORIGINAL_ID_KEY);

        assertThat(stopPlaceKeyVal.getValue())
                .as("the original ID should be stored as value")
                .isEqualTo(stopPlaceOriginalId);

        assertThat(stopPlaceKeyVal.getValue())
                .as("the original ID should not be the same as the new persisted ID")
                .isNotEqualTo(importedStopPlace.getId());

        Quay importedQuay = importedStopPlace.getQuays().get(0);

        KeyValueStructure quayKeyKeyValue = importedQuay.getKeyList().getKeyValue().get(0);
        assertThat(quayKeyKeyValue.getValue())
                .as("the original ID should be stored as value")
                .isEqualTo(quayOriginalId);

    }

    private void mockStopPlaceRepository(String persistedStopPlaceId) {
        when(stopPlaceRepository.save(any(StopPlace.class))).then((Answer<StopPlace>) invocationOnMock -> {
            StopPlace stopPlace = (StopPlace) invocationOnMock.getArguments()[0];
            stopPlace.setId(persistedStopPlaceId);
            return stopPlace;
        });
    }
    private void mockQuayRepository(String persistedQuayId) {
        when(quayRepository.save(any(Quay.class))).then((Answer<Quay>) invocationOnMock -> {
            Quay quay = (Quay) invocationOnMock.getArguments()[0];
            quay.setId(persistedQuayId);
            return quay;
        });
    }


}