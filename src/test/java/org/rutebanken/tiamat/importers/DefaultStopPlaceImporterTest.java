package org.rutebanken.tiamat.importers;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netexmapping.NetexIdMapper;
import org.rutebanken.tiamat.pelias.CountyAndMunicipalityLookupService;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.HOURS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
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
            countyAndMunicipalityLookupService, quayRepository, stopPlaceRepository, stopPlaceFromOriginalIdFinder, nearbyStopPlaceFinder, new KeyValueListAppender());

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


        Long savedId = 2L;

        when(stopPlaceRepository.save(firstStopPlace)).then(invocationOnMock -> {
            StopPlace stopPlace = (StopPlace) invocationOnMock.getArguments()[0];
            stopPlace.setId(savedId);
            return stopPlace;
        });

        StopPlace importedStopPlace1 = stopPlaceImporter.importStopPlace(firstStopPlace, siteFrame, new AtomicInteger());


        when(stopPlaceRepository.findByKeyValue(anyString(), anyList()))
                .then(invocationOnMock -> {
                    System.out.println("Returning the first stop place");
                    return importedStopPlace1;
                });

        when(stopPlaceRepository.findOne(anyLong())).then(invocationOnMock -> importedStopPlace1);

        StopPlace importedStopPlace2 = stopPlaceImporter.importStopPlace(secondStopPlace, siteFrame, new AtomicInteger());


        assertThat(importedStopPlace2.getId())
                .isEqualTo(importedStopPlace1.getId())
                .isEqualTo(savedId)
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
        when(stopPlaceRepository.findByKeyValue(ORIGINAL_ID_KEY, Arrays.asList(String.valueOf(chouetteId)))).thenReturn(savedStopPlaceId);
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
        final String chouetteId = "OPP:StopArea:123123";

        StopPlace firstStopPlace = new StopPlace();
        firstStopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(70.933307, 10.775973)))));
        firstStopPlace.setId(savedStopPlaceId);

        Quay terminal1 = new Quay();
        terminal1.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(70.000, 10.78)))));
        firstStopPlace.getQuays().add(terminal1);

        StopPlace secondStopPlace = new StopPlace();
        // Intentionally setting centroid way off the first stop place. Because the importer should match the chouette ID
        secondStopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(80.000, 20.78)))));

        // Set same ID as first stop place
        secondStopPlace.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add(chouetteId);

        Quay terminal2 = new Quay();
        terminal2.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(70.000, 10.78)))));
        secondStopPlace.getQuays().add(terminal2);

        mockStopPlaceSave(savedStopPlaceId, firstStopPlace);
        when(stopPlaceRepository.findByKeyValue(ORIGINAL_ID_KEY, Arrays.asList(String.valueOf(chouetteId)))).thenReturn(savedStopPlaceId);
        when(stopPlaceRepository.findOne(savedStopPlaceId)).thenReturn(firstStopPlace);

        // Import only the second stop place as the first one is already "saved" (mocked)
        StopPlace importResult = stopPlaceImporter.importStopPlace(secondStopPlace, siteFrame, new AtomicInteger());

        assertThat(importResult.getId()).isEqualTo(savedStopPlaceId);
        // Expect only one quay when two quays have the same coordinates
        assertThat(importResult.getQuays()).hasSize(1);
        assertThat(importResult.getQuays()).contains(terminal1);
    }

    @Test
    public void haveSameCoordinates() {
        Quay quay1 = new Quay();
        quay1.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)))));

        Quay quay2 = new Quay();
        quay2.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)))));

        assertThat(stopPlaceImporter.hasSameCoordinates(quay1, quay2)).isTrue();
    }

    @Test
    public void doesNotHaveSameCoordinates() {
        Quay quay1 = new Quay();
        quay1.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(60, 10.775973)))));

        Quay quay2 = new Quay();
        quay2.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)))));

        assertThat(stopPlaceImporter.hasSameCoordinates(quay1, quay2)).isFalse();
    }

    @Test
    public void findQuayIfAlreadyExisting() {

        Point existingQuayPoint = geometryFactory.createPoint(new Coordinate(60, 11));

        Quay existingQuay = new Quay();
        existingQuay.setName(new MultilingualString("existing quay"));
        existingQuay.setCentroid(new SimplePoint(new LocationStructure(existingQuayPoint)));

        Quay alreadyAdded = new Quay();
        alreadyAdded.setName(new MultilingualString("already added quay"));
        alreadyAdded.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(59, 10)))));

        Quay newQuayToInspect = new Quay();
        newQuayToInspect.setName(new MultilingualString("New quay which matches existing quay on the coordinates"));
        newQuayToInspect.setCentroid(new SimplePoint(new LocationStructure(existingQuayPoint)));

        List<Quay> existingQuays = Arrays.asList(existingQuay);
        List<Quay> alreadyAddedQuays = Arrays.asList(alreadyAdded);

        Quay actual = stopPlaceImporter.findQuayWithCoordinates(newQuayToInspect, existingQuays, alreadyAddedQuays).get();
        assertThat(actual).as("The same quay object as existingQuay should be returned").isSameAs(existingQuay);
    }

    @Test
    public void findQuayIfAlreadyAdded() {

        Point alreadyAddedQuayPoint = geometryFactory.createPoint(new Coordinate(61, 12));

        Quay existingQuay = new Quay();
        existingQuay.setName(new MultilingualString("Existing quay"));
        existingQuay.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(71, 9)))));

        Quay alreadyAddedQuay = new Quay();
        alreadyAddedQuay.setName(new MultilingualString("Quay to be added"));
        alreadyAddedQuay.setCentroid(new SimplePoint(new LocationStructure(alreadyAddedQuayPoint)));

        Quay newQuayToInspect = new Quay();
        newQuayToInspect.setName(new MultilingualString("New quay to check for match"));
        newQuayToInspect.setCentroid(new SimplePoint(new LocationStructure(alreadyAddedQuayPoint)));

        List<Quay> existingQuays = Arrays.asList(existingQuay);
        List<Quay> alreadyAddedQuays = Arrays.asList(alreadyAddedQuay);

        Quay actual = stopPlaceImporter.findQuayWithCoordinates(newQuayToInspect, existingQuays, alreadyAddedQuays).get();
        assertThat(actual).as("The same quay object as addedQuay should be returned").isSameAs(alreadyAddedQuay);
    }


    private void mockStopPlaceSave(Long persistedStopPlaceId, StopPlace stopPlace) {
        when(stopPlaceRepository.save(stopPlace)).then(invocationOnMock -> {
            StopPlace stopPlaceToSave = (StopPlace) invocationOnMock.getArguments()[0];
            stopPlaceToSave.setId(persistedStopPlaceId);
            return stopPlaceToSave;
        });
    }
}