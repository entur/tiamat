package org.rutebanken.tiamat.importer;


import com.vividsolutions.jts.geom.Coordinate;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.junit.Test;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionalStopPlacesImporterTest extends TiamatIntegrationTest {

    @Autowired
    private TransactionalStopPlacesImporter siteFrameImporter;

    /**
     * This test is implemented to reproduce an issue we had with lazy initialization exception
     * when returning a stop place that is already persisted, found by looking at imported key.
     * The test seems to be easier to reproduce if cache is disabled in {@link org.rutebanken.tiamat.repository.StopPlaceRepository}.
     */
    @Test
    public void lazyInitializationException() {

        int stopPlaces = 2;
        Random random = new Random();

        List<StopPlace> stopPlacesCreated = new ArrayList<>();

        for (int stopPlaceIndex = 0; stopPlaceIndex < stopPlaces; stopPlaceIndex++) {

            StopPlace stopPlace = new StopPlace();
            stopPlace.setNetexId(String.valueOf(stopPlaceIndex * Math.abs(random.nextLong())));

            double longitude = 39.61441 * Math.abs(random.nextDouble());
            double latitude = -144.22765 * Math.abs(random.nextDouble());

            stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
            stopPlacesCreated.add(stopPlace);
        }

        siteFrameImporter.importStopPlaces(stopPlacesCreated, new AtomicInteger());
    }


    @Test
    public void uniqueByIdAndVersion() {

        List<org.rutebanken.netex.model.StopPlace> stopPlacesWithDuplicates = new ArrayList<>();

        org.rutebanken.netex.model.StopPlace stopPlace1 = new org.rutebanken.netex.model.StopPlace();
        stopPlace1.setId("100");
        stopPlace1.setVersion("10");

        org.rutebanken.netex.model.StopPlace stopPlace2 = new org.rutebanken.netex.model.StopPlace();
        stopPlace2.setId("100");
        stopPlace2.setVersion("1");

        stopPlacesWithDuplicates.add(stopPlace2);
        stopPlacesWithDuplicates.add(stopPlace1);

        Collection<org.rutebanken.netex.model.StopPlace> actual = siteFrameImporter.distinctByIdAndHighestVersion(stopPlacesWithDuplicates);

        assertThat(actual).hasSize(1);
        assertThat(actual).containsOnlyOnce(stopPlace1);
        assertThat(actual).extracting(org.rutebanken.netex.model.StopPlace::getVersion).containsOnly("10");

    }
}