package no.rutebanken.tiamat.importers;

import com.vividsolutions.jts.geom.Coordinate;
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
     * <p>
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
    }

    /**
     * Two stop places with the same name and coordinates should become one stop place.
     * Because the those stop places each have one quay with the same coordinates, they should treated as one quay.
     */
    @Test
    public void quaysWithSameCoordinatesMustNotBeAddedMultipleTimes() throws ExecutionException, InterruptedException {
        String name = "Hestehovveien";

        double stopPlaceLatitude = 59.422556268440956728227320127189159393310546875;
        double stopPlaceLongitude = 5.265704397012616055917533230967819690704345703125;

        double quayLatitude = 59.4221750629462661663637845776975154876708984375;
        double quayLongitude = 5.2646351097871768587310725706629455089569091796875;

        StopPlace firstStopPlace = createStopPlace(name,
                stopPlaceLongitude, stopPlaceLatitude, "k7:StopArea:987987");
        firstStopPlace.getQuays().add(createQuay(name, quayLongitude, quayLatitude, "k7:StopArea:987987"));
        firstStopPlace.getQuays().add(createQuay(name, quayLongitude + 0.01, quayLatitude + 0.01, "k7:StopArea:987987"));

        AtomicInteger topographicPlacesCounter = new AtomicInteger();
        SiteFrame siteFrame = new SiteFrame();

        // Import first stop place.
        defaultStopPlaceImporter.importStopPlace(firstStopPlace, siteFrame, topographicPlacesCounter);

        StopPlace secondStopPlace = createStopPlace(name,
                stopPlaceLongitude, stopPlaceLatitude, "k7:StopArea:321321321");
        secondStopPlace.getQuays().add(createQuay(name, quayLongitude, quayLatitude, "k7:StopArea:321321321"));

        // Import second stop place
        StopPlace importResult = defaultStopPlaceImporter.importStopPlace(secondStopPlace, siteFrame, topographicPlacesCounter);

        assertThat(importResult.getId()).isEqualTo(importResult.getId());
        assertThat(importResult.getQuays()).hasSize(2);

        assertThat(importResult.getQuays().get(0).getName().getValue()).isEqualTo(name);

    }


    @Test
    public void quaysWithSameCoordinatesMustNotBeAddedMultipleTimes2() throws ExecutionException, InterruptedException {
        String name = "Mjåsund";

        StopPlace firstStopPlace = createStopPlaceWithQuay(name,
                5.447336160641715, 59.32245646609804, "k16:StopArea:11463484", "k16:StopArea:11463484");

        AtomicInteger topographicPlacesCounter = new AtomicInteger();
        SiteFrame siteFrame = new SiteFrame();

        // Import first stop place.
        defaultStopPlaceImporter.importStopPlace(firstStopPlace, siteFrame, topographicPlacesCounter);

        StopPlace secondStopPlace = createStopPlaceWithQuay(name,
                5.447071920203443, 59.32262902417035, "k16:StopArea:11463483", "k16:StopArea:11463483");

        // Import second stop place
        defaultStopPlaceImporter.importStopPlace(secondStopPlace, siteFrame, topographicPlacesCounter);

        StopPlace thirdStopPlace = createStopPlaceWithQuay(name,
                5.447071920203443, 59.32262902417035, "k16:StopArea:11463483", "k16:StopArea:11463483");

        // Import third stop place with a quay with the same coordinates
        StopPlace actualStopPlace = defaultStopPlaceImporter.importStopPlace(thirdStopPlace, siteFrame, topographicPlacesCounter);

        assertThat(actualStopPlace.getQuays()).hasSize(2);
    }

    private SimplePoint point(double longitude, double latitude) {
        return new SimplePoint(new LocationStructure(
                geometryFactory.createPoint(
                        new Coordinate(longitude, latitude))));
    }

    private StopPlace createStopPlaceWithQuay(String name, double longitude, double latitude, String stopPlaceId, String quayId) {
        StopPlace stopPlace = createStopPlace(name, longitude, latitude, stopPlaceId);
        stopPlace.getQuays().add(createQuay(name, longitude, latitude, quayId));
        return stopPlace;
    }

    private StopPlace createStopPlace(String name, double longitude, double latitude, String stopPlaceId) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setCentroid(point(longitude, latitude));
        stopPlace.setName(new MultilingualString(name, "", ""));
        stopPlace.setId(stopPlaceId);
        return stopPlace;
    }

    private Quay createQuay(String name, double longitude, double latitude, String id) {
        Quay quay = new Quay();
        quay.setName(new MultilingualString(name, "", ""));
        quay.setId(id);
        quay.setCentroid(point(longitude, latitude));
        return quay;
    }


}
