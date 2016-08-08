package no.rutebanken.tiamat.importers;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.TiamatApplication;
import no.rutebanken.tiamat.model.*;
import no.rutebanken.tiamat.repository.QuayRepository;
import no.rutebanken.tiamat.repository.StopPlaceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Test stop place importer with geodb and repository.
 * See also {@link DefaultStopPlaceImporterTest}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class DefaultStopPlaceImporterWithGeoDBTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private DefaultStopPlaceImporter defaultStopPlaceImporter;

    /**
     * Import two stop places, each with one quay.
     * Expect one stop place with two quays.
     *
     * Test data from chouette reggtopp import (kolumbus).
     */
    @Test
    public void addQuaysToNearbyStopPlaceWithDataFromChouette() throws ExecutionException, InterruptedException {

        String name = "Skillebekkgata";

        double firstLatitude = 59.422556268440956728227320127189159393310546875;
        double firstLongitude = 5.265704397012616055917533230967819690704345703125;

        StopPlace firstStopPlace = createStopPlaceWithQuay(name,
                firstLongitude, firstLatitude, "k7:StopArea:11063200", "k7:StopArea:11063200");

        AtomicInteger topographicPlacesCounter = new AtomicInteger();
        SiteFrame siteFrame = new SiteFrame();

        // Import first stop place.
        defaultStopPlaceImporter.importStopPlace(firstStopPlace, siteFrame, topographicPlacesCounter);

        double secondLatitude = 59.4221750629462661663637845776975154876708984375;
        double secondLongitude = 5.2646351097871768587310725706629455089569091796875;

        StopPlace secondStopPlace = createStopPlaceWithQuay(name,
                secondLongitude, secondLatitude, "k7:StopArea:11063198", "k7:StopArea:11063198");

        // Import second stop place
        StopPlace importResult = defaultStopPlaceImporter.importStopPlace(secondStopPlace, siteFrame, topographicPlacesCounter);

        assertThat(importResult.getId()).isEqualTo(importResult.getId());
        assertThat(importResult.getQuays()).hasSize(2);

        assertThat(importResult.getQuays().get(0).getName().getValue()).isEqualTo(name);
        assertThat(importResult.getQuays().get(1).getName().getValue()).isEqualTo(name);
        assertThat(stopPlaceRepository.findAll().iterator()).hasSize(1);
    }

    private SimplePoint point(double longitude, double latitude) {
        return new SimplePoint(new LocationStructure(
                geometryFactory.createPoint(
                        new Coordinate(longitude, latitude))));
    }

    private StopPlace createStopPlaceWithQuay(String name, double longitude, double latitude, String stopPlaceId, String quayId) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setCentroid(point(longitude, latitude));
        stopPlace.setName(new MultilingualString(name, "", ""));
        stopPlace.setId(stopPlaceId);

        Quay quay = new Quay();
        quay.setName(new MultilingualString(name, "", ""));
        quay.setId(quayId);
        quay.setCentroid(point(longitude, latitude));

        stopPlace.getQuays().add(quay);
        return stopPlace;
    }


}
