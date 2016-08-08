package no.rutebanken.tiamat.repository;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.TiamatApplication;
import no.rutebanken.tiamat.model.*;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class StopPlaceRepositoryImplTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    @Test
    public void findStopPlaceFromKeyList() {
        StopPlace stopPlace = new StopPlace();

        KeyListStructure keyListStructure = new KeyListStructure();
        keyListStructure.getKeyValue().add(new KeyValueStructure("key", "value"));
        stopPlace.setKeyList(keyListStructure);

        stopPlaceRepository.save(stopPlace);

        StopPlace actual = stopPlaceRepository.findByKeyValue("key", "value");
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getKeyList().getKeyValue().get(0).getKey()).isEqualTo("key");
        Assertions.assertThat(actual.getKeyList().getKeyValue().get(0).getValue()).isEqualTo("value");

    }

    @Test
    public void testFindStopPlacesWithin() throws Exception {
        StopPlace stopPlace = new StopPlace();
        SimplePoint centroid = new SimplePoint();

        double southEastLatitude = 59.875649;
        double southEastLongitude = 10.500340;

        double northWestLatitude = 59.875924;
        double northWestLongitude = 10.500699;

        double latitude = 59.875679;
        double longitude = 10.500430;

        centroid.setLocation(new LocationStructure(geometryFactory.createPoint(new Coordinate(longitude, latitude))));

        stopPlace.setCentroid(centroid);
        stopPlaceRepository.save(stopPlace);

        Pageable pageable = new PageRequest(0, 10);

        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithin(southEastLongitude, southEastLatitude, northWestLongitude, northWestLatitude, pageable);
        assertThat(result.getContent()).extracting(EntityStructure::getId).contains(stopPlace.getId());
    }

    @Test
    public void testFindStopPlaceWithinNoStopsInBoundingBox() throws Exception {
        StopPlace stopPlace = new StopPlace();
        SimplePoint centroid = new SimplePoint();

        double southEastLatitude = 59.875649;
        double southEastLongitude = 10.500340;

        double northWestLatitude = 59.875924;
        double northWestLongitude = 10.500699;

        // Outside boundingBox
        double latitude = 60.00;
        double longitude = 11.00;
        Pageable pageable = new PageRequest(0, 10);

        centroid.setLocation(new LocationStructure(geometryFactory.createPoint(new Coordinate(longitude, latitude))));

        stopPlace.setCentroid(centroid);
        stopPlaceRepository.save(stopPlace);

        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithin(southEastLongitude, southEastLatitude, northWestLongitude, northWestLatitude, pageable);

        assertThat(result.getContent()).extracting(EntityStructure::getId).doesNotContain(stopPlace.getId());
    }


    @Test
    public void findNearbyStopPlace() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString("name", "", ""));
        SimplePoint centroid = new SimplePoint();

        centroid.setLocation(new LocationStructure(geometryFactory.createPoint(new Coordinate(10.500430, 59.875679))));

        stopPlace.setCentroid(centroid);
        stopPlaceRepository.save(stopPlace);

        Envelope envelope = new Envelope(10.500340, 59.875649, 10.500699, 59.875924);

        StopPlace result = stopPlaceRepository.findNearbyStopPlace(envelope, stopPlace.getName().getValue());
        assertThat(result).isNotNull();
        assertThat(result.getName().getValue()).isEqualTo(stopPlace.getName().getValue());
    }

    @Test
    public void noNearbyStopPlace() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString("stop place", "", ""));
        SimplePoint centroid = new SimplePoint();

        centroid.setLocation(new LocationStructure(geometryFactory.createPoint(new Coordinate(15, 60))));

        stopPlace.setCentroid(centroid);
        stopPlaceRepository.save(stopPlace);

        Envelope envelope = new Envelope(10.500340, 59.875649, 10.500699, 59.875924);

        StopPlace result = stopPlaceRepository.findNearbyStopPlace(envelope, stopPlace.getName().getValue());
        assertThat(result).isNull();
    }

    @Test
    public void noNearbyStopPlaceIfNameIsDifferent() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString("This name is different", "", ""));
        SimplePoint centroid = new SimplePoint();
        centroid.setLocation(new LocationStructure(geometryFactory.createPoint(new Coordinate(15, 60))));

        stopPlace.setCentroid(centroid);
        stopPlaceRepository.save(stopPlace);

        // Stop place coordinates within envelope
        Envelope envelope = new Envelope(14, 16, 50, 70);

        StopPlace result = stopPlaceRepository.findNearbyStopPlace(envelope, "Another stop place which does not exist");


        assertThat(result).isNull();
    }
}