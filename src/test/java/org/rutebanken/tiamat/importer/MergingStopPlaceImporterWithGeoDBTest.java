package org.rutebanken.tiamat.importer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.junit.Before;
import org.rutebanken.tiamat.CommonSpringBootTest;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.junit.Test;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test stop place importer with geodb and repository.
 * See also {@link MergingStopPlaceImporterTest}
 */
@Transactional
public class MergingStopPlaceImporterWithGeoDBTest extends CommonSpringBootTest {

    @Autowired
    private MergingStopPlaceImporter mergingStopPlaceImporter;

    /**
     * Two stop places with the same name and coordinates should become one stop place.
     * Because the those stop places each have one quay with the same coordinates, they should treated as one quay.
     */
    @Test
    public void quaysWithSameCoordinatesMustNotBeAddedMultipleTimes() throws ExecutionException, InterruptedException {
        String name = "Hestehovveien";

        double stopPlaceLatitude = 59.422556;
        double stopPlaceLongitude = 5.265704;

        double quayLatitude = 59.422556;
        double quayLongitude = 5.265704;

        StopPlace firstStopPlace = createStopPlace(name,
                stopPlaceLongitude, stopPlaceLatitude, null);
        firstStopPlace.getQuays().add(createQuay(name, quayLongitude, quayLatitude, null));
        firstStopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);

        // Import first stop place.
        StopPlace firstImportResult = mergingStopPlaceImporter.importStopPlaceWithoutNetexMapping(firstStopPlace);

        StopPlace secondStopPlace = createStopPlace(name,
                stopPlaceLongitude, stopPlaceLatitude, null);
        secondStopPlace.getQuays().add(createQuay(name, quayLongitude, quayLatitude, null));
        secondStopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);


        // Import second stop place
        StopPlace importResult = mergingStopPlaceImporter.importStopPlaceWithoutNetexMapping(secondStopPlace);

        assertThat(importResult.getNetexId()).isEqualTo(firstImportResult.getNetexId());
        assertThat(importResult.getQuays()).hasSize(1);

        assertThat(importResult.getQuays().iterator().next().getName().getValue()).isEqualTo(name);

    }

    @Test
    public void addQuaysToStopPlaceWithoutQuays() throws ExecutionException, InterruptedException {
        String name = "Eselbergveien";

        double longitude = 5;
        double latitude = 71;

        StopPlace firstStopPlace = createStopPlace(name, longitude, latitude, null);
        SiteFrame siteFrame = new SiteFrame();

        // Import first stop place.
        mergingStopPlaceImporter.importStopPlace(firstStopPlace);

        StopPlace secondStopPlace = createStopPlace(name, longitude, latitude, null);
        secondStopPlace.getQuays().add(createQuay(name, longitude, latitude, null));

        // Import second stop place
        StopPlace importResult = mergingStopPlaceImporter.importStopPlaceWithoutNetexMapping(secondStopPlace);

        assertThat(importResult.getNetexId()).isEqualTo(importResult.getNetexId());
        assertThat(importResult.getQuays()).hasSize(1);

        assertThat(importResult.getQuays().iterator().next().getName().getValue()).isEqualTo(name);
    }

    /**
     * Two nearby stops with the same type should be treated as the same.
     */
    @Test
    public void findNearbyStopWithSameType() throws ExecutionException, InterruptedException {

        StopPlace firstStopPlace = createStopPlace("Filipstad", 10.7096245, 59.9086885, null);
        firstStopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        SiteFrame siteFrame = new SiteFrame();

        mergingStopPlaceImporter.importStopPlace(firstStopPlace);

        StopPlace secondStopPlace = createStopPlace("Filipstad ferjeterminal", 10.709707, 59.908737, null);
        secondStopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);

        StopPlace importResult = mergingStopPlaceImporter.importStopPlaceWithoutNetexMapping(secondStopPlace);

        assertThat(importResult.getNetexId()).isEqualTo(importResult.getNetexId());
        assertThat(importResult.getName().getValue()).isEqualTo(firstStopPlace.getName().getValue());
    }

    @Test
    public void reproduceIssueWithCollectionNotAssosiatedWithAnySession() throws ExecutionException, InterruptedException {
        String name = "Skillebekkgata";
        StopPlace firstStopPlace = createStopPlaceWithQuay(name, 6, 60, "11063200", "11063200");
        mergingStopPlaceImporter.importStopPlace(firstStopPlace);
        StopPlace secondStopPlace = createStopPlaceWithQuay(name, 6, 60.0001, "11063198", "11063198");
        mergingStopPlaceImporter.importStopPlace(secondStopPlace);
    }

    /**
     * Import two stop places with the same coordinates.
     * Verify that quays are not added multiple times.
     */
    @Test
    public void quaysWithSameCoordinatesMustNotBeAddedMultipleTimes2() throws ExecutionException, InterruptedException {
        String name = "Mjåsund";

        double latitude = 59.32262902417035;
        double longitude = 5.447071920203443;
        String stopPlaceId = "11463484";
        String quayId = "11463483";

        StopPlace firstStopPlace = createStopPlaceWithQuay(name,
                longitude, latitude, stopPlaceId, quayId);

        SiteFrame siteFrame = new SiteFrame();

        // Import first stop place.
        mergingStopPlaceImporter.importStopPlace(firstStopPlace);

        StopPlace secondStopPlace = createStopPlaceWithQuay(name,
                longitude, latitude, stopPlaceId, quayId);

        // Import second stop place with a quay with the same coordinates as second stop place
        StopPlace actualStopPlace = mergingStopPlaceImporter.importStopPlaceWithoutNetexMapping(secondStopPlace);

        assertThat(actualStopPlace.getQuays()).hasSize(1);
    }

    private Point point(double longitude, double latitude) {
        return 
                geometryFactory.createPoint(
                        new Coordinate(longitude, latitude));
    }

    private StopPlace createStopPlaceWithQuay(String name, double longitude, double latitude, String stopPlaceId, String quayId) {
        StopPlace stopPlace = createStopPlace(name, longitude, latitude, stopPlaceId);
        stopPlace.getQuays().add(createQuay(name, longitude, latitude, quayId));
        return stopPlace;
    }

    private StopPlace createStopPlace(String name, double longitude, double latitude, String stopPlaceId) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setCentroid(point(longitude, latitude));
        stopPlace.setName(new EmbeddableMultilingualString(name, ""));
        stopPlace.setNetexId(stopPlaceId);
        return stopPlace;
    }

    private Quay createQuay(String name, double longitude, double latitude, String id) {
        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString(name, ""));
        quay.setNetexId(id);
        quay.setCentroid(point(longitude, latitude));
        return quay;
    }


}
