package org.rutebanken.tiamat.importer;

import org.junit.Test;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;


public class NearbyStopPlaceFinderTest {
    @Test
    public void nullCentroid() throws Exception {
        NearbyStopPlaceFinder nearbyStopPlaceFinder = new NearbyStopPlaceFinder(mock(StopPlaceRepository.class), 0, 0, TimeUnit.DAYS);
        StopPlace stopPlace = new StopPlace();
        nearbyStopPlaceFinder.find(stopPlace);
    }

    @Test
    public void nullPoint() throws Exception {
        NearbyStopPlaceFinder nearbyStopPlaceFinder = new NearbyStopPlaceFinder(mock(StopPlaceRepository.class), 0, 0, TimeUnit.DAYS);
        StopPlace stopPlace = new StopPlace();
        nearbyStopPlaceFinder.find(stopPlace);
    }
}
