package no.rutebanken.tiamat.repository;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.TiamatApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import no.rutebanken.tiamat.model.EntityStructure;
import no.rutebanken.tiamat.model.LocationStructure;
import no.rutebanken.tiamat.model.SimplePoint;
import no.rutebanken.tiamat.model.StopPlace;

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
}