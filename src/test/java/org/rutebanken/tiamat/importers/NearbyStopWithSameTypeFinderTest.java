package org.rutebanken.tiamat.importers;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotools.referencing.GeodeticCalculator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.awt.geom.Point2D;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
@Transactional
public class NearbyStopWithSameTypeFinderTest {

    private static final Coordinate OSL_GARDERMOEN = new Coordinate(60.190448, 11.106292);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private NearbyStopWithSameTypeFinder nearbyStopWithSameTypeFinder;

    @Test
    public void findNearbyAirport() throws FactoryException, TransformException {

        Point point = geometryFactory.createPoint(OSL_GARDERMOEN);
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("OSL Gardermoen"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.AIRPORT);
        stopPlace.setCentroid(point);

        stopPlaceRepository.save(stopPlace);

        int azimuth = 90;
        int offsetMeters = 300;

        GeodeticCalculator calc = new GeodeticCalculator();
        calc.setStartingGeographicPoint(point.getX(), point.getY());
        calc.setDirection(azimuth, offsetMeters);
        Point2D dest = calc.getDestinationGeographicPoint();
        Point offsetPoint = geometryFactory.createPoint(new Coordinate(dest.getX(), dest.getY()));
        System.out.println(offsetPoint);

        StopPlace incomingStopPlace = new StopPlace(new EmbeddableMultilingualString("Gardermoen"));
        incomingStopPlace.setStopPlaceType(StopTypeEnumeration.AIRPORT);
        incomingStopPlace.setCentroid(point);

        List<StopPlace> foundStopPlaces = nearbyStopWithSameTypeFinder.find(incomingStopPlace);
        assertThat(foundStopPlaces).isNotNull();
        assertThat(foundStopPlaces).hasSize(1);
    }

    @Test
    public void noNearByAirport() throws FactoryException, TransformException {

        Point point = geometryFactory.createPoint(OSL_GARDERMOEN);
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("OSL Gardermoen"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.AIRPORT);
        stopPlace.setCentroid(point);

        stopPlaceRepository.save(stopPlace);

        int offsetMeters = 9000;
        int azimuth = 90;

        GeodeticCalculator calc = new GeodeticCalculator();
        calc.setStartingGeographicPoint(point.getX(), point.getY());
        calc.setDirection(azimuth, offsetMeters);
        Point2D dest = calc.getDestinationGeographicPoint();
        Point offsetPoint = geometryFactory.createPoint(new Coordinate(dest.getX(), dest.getY()));
        System.out.println(offsetPoint);

        StopPlace incomingStopPlace = new StopPlace(new EmbeddableMultilingualString("Gardermoen"));
        incomingStopPlace.setStopPlaceType(StopTypeEnumeration.AIRPORT);
        incomingStopPlace.setCentroid(offsetPoint);

        System.out.println("Searching with stop place " + stopPlace);
        List<StopPlace> foundStopPlaces = nearbyStopWithSameTypeFinder.find(incomingStopPlace);
        assertThat(foundStopPlaces).isNotNull();
        assertThat(foundStopPlaces).isEmpty();
    }

    @Test
    public void findNearbyTrainStation() {

    }

    @Test
    public void findNearbyBusStop() {

    }

    @Test
    public void findNearbyTramStation() {


    }

}