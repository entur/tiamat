package org.rutebanken.tiamat.importers;

import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import org.rutebanken.tiamat.model.LocationStructure;
import org.rutebanken.tiamat.model.SimplePoint;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;


public class NearbyStopPlaceFinderTest {
    @Test
    public void nullCentroid() throws Exception {
        NearbyStopPlaceFinder nearbyStopPlaceFinder = new NearbyStopPlaceFinder(mock(StopPlaceRepository.class), 0, 0, TimeUnit.DAYS);
        StopPlace stopPlace = new StopPlace();
        nearbyStopPlaceFinder.find(stopPlace);
    }

    @Test
    public void nullSimplePoint() throws Exception {
        NearbyStopPlaceFinder nearbyStopPlaceFinder = new NearbyStopPlaceFinder(mock(StopPlaceRepository.class), 0, 0, TimeUnit.DAYS);
        StopPlace stopPlace = new StopPlace();
        stopPlace.setCentroid(new SimplePoint());
        nearbyStopPlaceFinder.find(stopPlace);
    }

    @Test
    public void nullLocationStructure() throws Exception {
        NearbyStopPlaceFinder nearbyStopPlaceFinder = new NearbyStopPlaceFinder(mock(StopPlaceRepository.class), 0, 0, TimeUnit.DAYS);
        StopPlace stopPlace = new StopPlace();
        SimplePoint simplePoint = new SimplePoint(new LocationStructure());
        stopPlace.setCentroid(simplePoint);

        nearbyStopPlaceFinder.find(stopPlace);
    }

    @Test
    public void nullGeometryPoint() throws Exception {
        NearbyStopPlaceFinder nearbyStopPlaceFinder = new NearbyStopPlaceFinder(mock(StopPlaceRepository.class), 0, 0, TimeUnit.DAYS);
        StopPlace stopPlace = new StopPlace();
        SimplePoint simplePoint = new SimplePoint(new LocationStructure());
        stopPlace.setCentroid(simplePoint);
        nearbyStopPlaceFinder.update(stopPlace);
    }
}
