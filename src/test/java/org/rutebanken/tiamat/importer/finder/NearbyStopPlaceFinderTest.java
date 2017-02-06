package org.rutebanken.tiamat.importer.finder;

import org.junit.Test;
import org.rutebanken.tiamat.importer.finder.NearbyStopPlaceFinder;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


public class NearbyStopPlaceFinderTest {
    @Test
    public void nullCentroid() throws Exception {
        NearbyStopPlaceFinder nearbyStopPlaceFinder = new NearbyStopPlaceFinder(mock(StopPlaceRepository.class), 0, 0, TimeUnit.DAYS);
        StopPlace stopPlace = new StopPlace();
        StopPlace actual = nearbyStopPlaceFinder.find(stopPlace);
        assertThat(actual).isNull();
    }

    @Test
    public void nullPoint() throws Exception {
        NearbyStopPlaceFinder nearbyStopPlaceFinder = new NearbyStopPlaceFinder(mock(StopPlaceRepository.class), 0, 0, TimeUnit.DAYS);
        StopPlace stopPlace = new StopPlace();
        StopPlace actual = nearbyStopPlaceFinder.find(stopPlace);
        assertThat(actual).isNull();
    }

    @Test
    public void nullType() throws Exception {
        NearbyStopPlaceFinder nearbyStopPlaceFinder = new NearbyStopPlaceFinder(mock(StopPlaceRepository.class), 0, 0, TimeUnit.DAYS);
        StopPlace stopPlace = new StopPlace();
        nearbyStopPlaceFinder.update(stopPlace);
    }
}
