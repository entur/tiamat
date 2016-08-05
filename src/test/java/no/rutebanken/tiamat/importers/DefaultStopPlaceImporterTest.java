package no.rutebanken.tiamat.importers;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.config.GeometryFactoryConfig;
import no.rutebanken.tiamat.model.*;
import no.rutebanken.tiamat.repository.QuayRepository;
import no.rutebanken.tiamat.repository.StopPlaceRepository;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static no.rutebanken.tiamat.importers.DefaultStopPlaceImporter.ORIGINAL_ID_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultStopPlaceImporterTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    private TopographicPlaceCreator topographicPlaceCreator = mock(TopographicPlaceCreator.class);

    private QuayRepository quayRepository = mock(QuayRepository.class);

    private StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);

    private DefaultStopPlaceImporter stopPlaceImporter = new DefaultStopPlaceImporter(topographicPlaceCreator, quayRepository, stopPlaceRepository);

    private static SiteFrame siteFrame = new SiteFrame();
    static {
        siteFrame.setTopographicPlaces(new TopographicPlacesInFrame_RelStructure());
    }

    @Test
    public void detectAndMergeQuaysFromTwoSimilarStopPlaces() throws ExecutionException, InterruptedException {

        StopPlace firstStopPlace = new StopPlace();
        firstStopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)))));
        firstStopPlace.setName(new MultilingualString("Andalsnes", "no", ""));

        Quay terminal1 = new Quay();
        terminal1.setName(new MultilingualString("terminal 1", "", ""));
        terminal1.setId("chouette-id-1");
        terminal1.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(60.000, 10.78)))));

        firstStopPlace.getQuays().add(terminal1);

        StopPlace secondStopPlace = new StopPlace();
        secondStopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(60.000, 10.78)))));
        secondStopPlace.setName(new MultilingualString("Andalsnes", "no", ""));

        Quay terminal2 = new Quay();
        terminal2.setName(new MultilingualString("terminal 2", "", ""));
        terminal2.setId("chouette-id-2");
        terminal2.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(60.01, 10.78)))));
        secondStopPlace.getQuays().add(terminal2);

        when(stopPlaceRepository.findNearbyStopPlace(any(Envelope.class), anyString())).then(invocationOnMock -> firstStopPlace);

        StopPlace importResult = stopPlaceImporter.importStopPlace(secondStopPlace, siteFrame, new AtomicInteger());

        assertThat(importResult.getId()).isEqualTo(importResult.getId());
        assertThat(importResult.getQuays()).hasSize(2);
    }

    @Test
    public void handleDuplicateStopPlacesBasedOnId() throws ExecutionException, InterruptedException {


        StopPlace firstStopPlace = new StopPlace();
        firstStopPlace.setId("chouette-id-1");
        firstStopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)))));
        firstStopPlace.setName(new MultilingualString("skjeberg", "no", ""));

        StopPlace secondStopPlace = new StopPlace();
        secondStopPlace.setId(firstStopPlace.getId());
        secondStopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)))));
        secondStopPlace.setName(new MultilingualString("skjeberg", "no", ""));


        when(stopPlaceRepository.save(firstStopPlace)).then(invocationOnMock -> {
            StopPlace stopPlace = (StopPlace) invocationOnMock.getArguments()[0];
            stopPlace.setId("generated-id-first-stop-place");
            return stopPlace;
        });

        StopPlace importedStopPlace1 = stopPlaceImporter.importStopPlace(firstStopPlace, siteFrame, new AtomicInteger());


        when(stopPlaceRepository.findByKeyValue(anyString(), anyString()))
                .then(invocationOnMock -> {
                    System.out.println("Returning the first stop place");
                    return importedStopPlace1;
                });

        when(stopPlaceRepository.findOne(anyString())).then(invocationOnMock -> importedStopPlace1);

        StopPlace importedStopPlace2 = stopPlaceImporter.importStopPlace(secondStopPlace, siteFrame, new AtomicInteger());


        assertThat(importedStopPlace2.getId()).isEqualTo(importedStopPlace1.getId())
                .as("The same stop place should be returned as they have the same chouette id");
    }

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

        StopPlace importedStopPlace = stopPlaceImporter.importStopPlace(stopPlace, siteFrame, new AtomicInteger());

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

        KeyValueStructure quayKeyValue = importedQuay
                .getKeyList()
                .getKeyValue()
                .get(0);
        assertThat(quayKeyValue.getValue())
                .as("the original ID should be stored as value")
                .isEqualTo(quayOriginalId);

    }

    @Test
    public void haveSameCoordinates() {
        Quay quay1 = new Quay();
        quay1.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)))));

        Quay quay2 = new Quay();
        quay2.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)))));

        stopPlaceImporter.hasSameCoordinates(quay1, quay2);
    }

    @Test
    public void doesNotHaveSameCoordinates() {
        Quay quay1 = new Quay();
        quay1.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(60, 10.775973)))));

        Quay quay2 = new Quay();
        quay2.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)))));

        stopPlaceImporter.hasSameCoordinates(quay1, quay2);

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