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

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.HOURS;
import static no.rutebanken.tiamat.importers.DefaultStopPlaceImporter.ORIGINAL_ID_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test stop place importer with mocked dependencies.
 * See also {@link DefaultStopPlaceImporterWithGeoDBTest}
 */
public class DefaultStopPlaceImporterTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    private TopographicPlaceCreator topographicPlaceCreator = mock(TopographicPlaceCreator.class);

    private QuayRepository quayRepository = mock(QuayRepository.class);

    private StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);

    private StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder = new StopPlaceFromOriginalIdFinder(stopPlaceRepository, 100, 10, HOURS);

    private NearbyStopPlaceFinder nearbyStopPlaceFinder = new NearbyStopPlaceFinder(stopPlaceRepository, 20000, 30, TimeUnit.MINUTES);

    private DefaultStopPlaceImporter stopPlaceImporter = new DefaultStopPlaceImporter(topographicPlaceCreator,
            quayRepository, stopPlaceRepository, stopPlaceFromOriginalIdFinder, nearbyStopPlaceFinder);

    private SiteFrame siteFrame = new SiteFrame();

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

        mockStopPlaceSave("firstId", firstStopPlace);
        when(stopPlaceRepository.findNearbyStopPlace(any(Envelope.class), any(String.class))).thenReturn("firstId");
        when(stopPlaceRepository.findOne("firstId")).thenReturn(firstStopPlace);

        // Import only the second stop place as the first one is already "saved" (mocked)
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


        assertThat(importedStopPlace2.getId())
                .isEqualTo(importedStopPlace1.getId())
                .as("The same stop place should be returned as they have the same chouette id");
    }

    @Test
    public void keepOriginalIdsInKeyList() throws Exception {

        final String stopPlaceOriginalId = "stop-place-id-to-replace";
        final String quayOriginalId = "quay-id-to-replace";

        final String persistedStopPlaceId = "persisted-stop-place-id";
        mockStopPlaceSave(persistedStopPlaceId);

        final String persistedQuayId = "persisted-quay-id";
        mockQuayRepository(persistedQuayId);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setId(stopPlaceOriginalId);
        stopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)))));

        Quay quay = new Quay();
        quay.setId(quayOriginalId);
        createCentroid(quay);

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

    private void mockStopPlaceSave(String persistedStopPlaceId, StopPlace stopPlace) {
        when(stopPlaceRepository.save(stopPlace)).then(invocationOnMock -> {
            StopPlace stopPlaceToSave = (StopPlace) invocationOnMock.getArguments()[0];
            stopPlaceToSave.setId(persistedStopPlaceId);
            return stopPlaceToSave;
        });
    }

    private void mockStopPlaceSave(String persistedStopPlaceId) {
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

    private void createCentroid(Quay quay) {
        quay.setCentroid(new SimplePoint());
        quay.getCentroid().setLocation(new LocationStructure());
        quay.getCentroid().getLocation().setLatitude(BigDecimal.valueOf(71));
        quay.getCentroid().getLocation().setLongitude(BigDecimal.valueOf(6));
    }

}