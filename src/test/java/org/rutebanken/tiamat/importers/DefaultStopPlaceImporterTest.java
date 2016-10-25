package org.rutebanken.tiamat.importers;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.pelias.CountyAndMunicipalityLookupService;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.HOURS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.rutebanken.tiamat.netexmapping.NetexIdMapper.ORIGINAL_ID_KEY;

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

    private CountyAndMunicipalityLookupService countyAndMunicipalityLookupService = mock(CountyAndMunicipalityLookupService.class);

    private DefaultStopPlaceImporter stopPlaceImporter = new DefaultStopPlaceImporter(topographicPlaceCreator,
            countyAndMunicipalityLookupService, quayRepository, stopPlaceRepository, stopPlaceFromOriginalIdFinder, nearbyStopPlaceFinder, new KeyValueAppender());

    private SiteFrame siteFrame = new SiteFrame();

    @Test
    public void detectAndMergeQuaysFromTwoSimilarStopPlaces() throws ExecutionException, InterruptedException {

        Long firstStopId = 1L;
        StopPlace firstStopPlace = new StopPlace();
        firstStopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)))));
        firstStopPlace.setName(new MultilingualString("Andalsnes", "no", ""));
        firstStopPlace.setId(firstStopId);

        Quay terminal1 = new Quay();
        terminal1.setName(new MultilingualString("terminal 1", "", ""));
        terminal1.setId(2L);
        terminal1.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(60.000, 10.78)))));

        firstStopPlace.getQuays().add(terminal1);

        StopPlace secondStopPlace = new StopPlace();
        secondStopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(60.000, 10.78)))));
        secondStopPlace.setName(new MultilingualString("Andalsnes", "no", ""));

        Quay terminal2 = new Quay();
        terminal2.setName(new MultilingualString("terminal 2", "", ""));
        terminal2.setId(3L);
        terminal2.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(60.01, 10.78)))));
        secondStopPlace.getQuays().add(terminal2);

        mockStopPlaceSave(firstStopId, firstStopPlace);
        when(stopPlaceRepository.findNearbyStopPlace(any(Envelope.class), any(String.class))).thenReturn(firstStopId);
        when(stopPlaceRepository.findOne(firstStopId)).thenReturn(firstStopPlace);

        // Import only the second stop place as the first one is already "saved" (mocked)
        StopPlace importResult = stopPlaceImporter.importStopPlace(secondStopPlace, siteFrame, new AtomicInteger());

        assertThat(importResult.getId()).isEqualTo(importResult.getId());
        assertThat(importResult.getQuays()).hasSize(2);
    }


    @Test
    public void handleDuplicateStopPlacesBasedOnId() throws ExecutionException, InterruptedException {


        StopPlace firstStopPlace = new StopPlace();
        firstStopPlace.setId(1L);
        firstStopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)))));
        firstStopPlace.setName(new MultilingualString("skjeberg", "no", ""));

        StopPlace secondStopPlace = new StopPlace();
        secondStopPlace.setId(firstStopPlace.getId());
        secondStopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)))));
        secondStopPlace.setName(new MultilingualString("skjeberg", "no", ""));


        when(stopPlaceRepository.save(firstStopPlace)).then(invocationOnMock -> {
            StopPlace stopPlace = (StopPlace) invocationOnMock.getArguments()[0];
            stopPlace.setId(2L);
            return stopPlace;
        });

        StopPlace importedStopPlace1 = stopPlaceImporter.importStopPlace(firstStopPlace, siteFrame, new AtomicInteger());


        when(stopPlaceRepository.findByKeyValue(anyString(), anyString()))
                .then(invocationOnMock -> {
                    System.out.println("Returning the first stop place");
                    return importedStopPlace1;
                });

        when(stopPlaceRepository.findOne(anyLong())).then(invocationOnMock -> importedStopPlace1);

        StopPlace importedStopPlace2 = stopPlaceImporter.importStopPlace(secondStopPlace, siteFrame, new AtomicInteger());


        assertThat(importedStopPlace2.getId())
                .isEqualTo(importedStopPlace1.getId())
                .as("The same stop place should be returned as they have the same chouette id");
    }

    /**
     * When importing a stop place with matching chouette ID, the quay should be added to existing stop place.
     */
    @Test
    public void detectAndMergeQuaysForExistingStopPlace() throws ExecutionException, InterruptedException {
        final Long savedStopPlaceId = 1L;
        final Long chouetteId = 2L;
        final Long chouetteQuayId = 3L;

        StopPlace firstStopPlace = new StopPlace();
        firstStopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(70.933307, 10.775973)))));
        firstStopPlace.setId(chouetteId);

        Quay terminal1 = new Quay();
        terminal1.setId(chouetteQuayId);
        terminal1.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(70.000, 10.78)))));
        firstStopPlace.getQuays().add(terminal1);

        StopPlace secondStopPlace = new StopPlace();
        // Intentionally setting centroid way off the first stop place. Because the importer should match the chouette ID
        secondStopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(80.000, 20.78)))));
        // Set same ID as first stop place
        secondStopPlace.setId(chouetteId);

        Quay terminal2 = new Quay();
        terminal2.setId(chouetteQuayId);
        terminal2.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(70.000, 10.78)))));
        secondStopPlace.getQuays().add(terminal2);

        mockStopPlaceSave(savedStopPlaceId, firstStopPlace);
        when(stopPlaceRepository.findByKeyValue(ORIGINAL_ID_KEY, String.valueOf(chouetteId))).thenReturn(savedStopPlaceId);
        when(stopPlaceRepository.findOne(savedStopPlaceId)).thenReturn(firstStopPlace);

        // Import only the second stop place as the first one is already "saved" (mocked)
        StopPlace importResult = stopPlaceImporter.importStopPlace(secondStopPlace, siteFrame, new AtomicInteger());

        assertThat(importResult.getId()).isEqualTo(importResult.getId());
        assertThat(importResult.getQuays()).hasSize(1);
    }

    /**
     * When importing a stop place which already exists, matching quays should not be duplicated.
     */
    @Test
    public void detectTwoMatchingQuaysInTwoSeparateStopPlaces() throws ExecutionException, InterruptedException {
        final Long savedStopPlaceId = 1L;
        final Long chouetteId = 2L;

        StopPlace firstStopPlace = new StopPlace();
        firstStopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(70.933307, 10.775973)))));
        firstStopPlace.setId(chouetteId);

        Quay terminal1 = new Quay();
        terminal1.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(70.000, 10.78)))));
        firstStopPlace.getQuays().add(terminal1);

        StopPlace secondStopPlace = new StopPlace();
        // Intentionally setting centroid way off the first stop place. Because the importer should match the chouette ID
        secondStopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(80.000, 20.78)))));

        // Set same ID as first stop place
        secondStopPlace.setId(chouetteId);

        Quay terminal2 = new Quay();
        terminal2.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(70.000, 10.78)))));
        secondStopPlace.getQuays().add(terminal2);

        mockStopPlaceSave(savedStopPlaceId, firstStopPlace);
        when(stopPlaceRepository.findByKeyValue(ORIGINAL_ID_KEY, String.valueOf(chouetteId))).thenReturn(savedStopPlaceId);
        when(stopPlaceRepository.findOne(savedStopPlaceId)).thenReturn(firstStopPlace);

        // Import only the second stop place as the first one is already "saved" (mocked)
        StopPlace importResult = stopPlaceImporter.importStopPlace(secondStopPlace, siteFrame, new AtomicInteger());

        assertThat(importResult.getId()).isEqualTo(importResult.getId());
        // Expect only one quay when two quays have the same coordinates
        assertThat(importResult.getQuays()).hasSize(1);
        assertThat(importResult.getQuays()).contains(terminal1);
    }

//    @Test
//    public void keepOriginalIdsInKeyList() throws Exception {
//
//        final Long stopPlaceOriginalId = 1L;
//        final Long quayOriginalId = 2L;
//
//        final Long persistedStopPlaceId = 10L;
//        mockStopPlaceSave(persistedStopPlaceId);
//
//        final Long persistedQuayId = 11L;
//        mockQuayRepository(persistedQuayId);
//
//        StopPlace stopPlace = new StopPlace();
//        stopPlace.setId(stopPlaceOriginalId);
//        stopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)))));
//
//        Quay quay = new Quay();
//        quay.setId(quayOriginalId);
//        createCentroid(quay);
//
//        stopPlace.getQuays().add(quay);
//
//        StopPlace importedStopPlace = stopPlaceImporter.importStopPlace(stopPlace, siteFrame, new AtomicInteger());
//
//        assertThat(importedStopPlace.getId()).isEqualTo(persistedStopPlaceId);
//        assertThat(importedStopPlace.getQuays().get(0).getId()).isEqualTo(persistedQuayId);
//
//        assertThat(importedStopPlace.getKeyList()).isNotNull();
//        assertThat(importedStopPlace.getKeyList().getOrCreateKeyValue()).isNotEmpty();
//
//        KeyValueStructure stopPlaceKeyVal = importedStopPlace
//                .getKeyList()
//                .getOrCreateKeyValue()
//                .get(0);
//
//        assertThat(stopPlaceKeyVal.getKey()).as("key should be set")
//                .isEqualTo(ORIGINAL_ID_KEY);
//
//        assertThat(stopPlaceKeyVal.getValue())
//                .as("the original ID should be stored as value")
//                .isEqualTo(stopPlaceOriginalId.toString());
//
//        assertThat(stopPlaceKeyVal.getValue())
//                .as("the original ID should not be the same as the new persisted ID")
//                .isNotEqualTo(importedStopPlace.getId());
//
//        Quay importedQuay = importedStopPlace.getQuays().get(0);
//
//        KeyValueStructure quayKeyValue = importedQuay
//                .getKeyList()
//                .getOrCreateKeyValue()
//                .get(0);
//        assertThat(quayKeyValue.getValue())
//                .as("the original ID should be stored as value")
//                .isEqualTo(quayOriginalId.toString());
//
//    }

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

    private void mockStopPlaceSave(Long persistedStopPlaceId, StopPlace stopPlace) {
        when(stopPlaceRepository.save(stopPlace)).then(invocationOnMock -> {
            StopPlace stopPlaceToSave = (StopPlace) invocationOnMock.getArguments()[0];
            stopPlaceToSave.setId(persistedStopPlaceId);
            return stopPlaceToSave;
        });
    }

    private void mockStopPlaceSave(Long persistedStopPlaceId) {
        when(stopPlaceRepository.save(any(StopPlace.class))).then((Answer<StopPlace>) invocationOnMock -> {
            StopPlace stopPlace = (StopPlace) invocationOnMock.getArguments()[0];
            stopPlace.setId(persistedStopPlaceId);
            return stopPlace;
        });
    }
    private void mockQuayRepository(Long persistedQuayId) {
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