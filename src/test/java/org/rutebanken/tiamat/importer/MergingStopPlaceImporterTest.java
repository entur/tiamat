package org.rutebanken.tiamat.importer;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.*;
import org.junit.Test;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

/**
 * Test stop place importer with geodb and repository.
 * See also {@link MergingStopPlaceImporterTest}
 */
@Transactional
public class MergingStopPlaceImporterTest extends TiamatIntegrationTest {

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
     * The second time the stop place is imported, the type must be updated if it was empty.
     */
    @Test
    public void updateStopPlaceType() throws ExecutionException, InterruptedException {

        Point point = point(10.7096245, 59.9086885);

        StopPlace firstStopPlace = new StopPlace();
        firstStopPlace.setCentroid(point);
        firstStopPlace.setName(new EmbeddableMultilingualString("Filipstad", "no"));
        firstStopPlace.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-filipstad");
        firstStopPlace.setVersion(1L);

        stopPlaceRepository.save(firstStopPlace);

        StopPlace newStopPlace = new StopPlace();
        newStopPlace.setCentroid(point);
        newStopPlace.setName(new EmbeddableMultilingualString("Filipstad", "no"));
        newStopPlace.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-filipstad");
        newStopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);

        StopPlace importResult = mergingStopPlaceImporter.importStopPlaceWithoutNetexMapping(newStopPlace);

        assertThat(importResult.getNetexId()).isEqualTo(firstStopPlace.getNetexId());
        assertThat(importResult.getStopPlaceType()).isEqualTo(StopTypeEnumeration.ONSTREET_BUS);
    }

    @Test
    public void detectAndMergeQuaysFromTwoSimilarStopPlaces() throws ExecutionException, InterruptedException {

        StopPlace firstStopPlace = new StopPlace();
        firstStopPlace.setCentroid(point(60.000, 10.78));
        firstStopPlace.setName(new EmbeddableMultilingualString("Andalsnes", "no"));
        firstStopPlace.setVersion(1L);
        firstStopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);

        Quay terminal1 = new Quay();
        terminal1.setName(new EmbeddableMultilingualString("terminal 1"));
        terminal1.setCentroid(point(60.000, 10.78));

        firstStopPlace.getQuays().add(terminal1);

        stopPlaceRepository.save(firstStopPlace);

        StopPlace secondStopPlace = new StopPlace();
        secondStopPlace.setCentroid(point(60.000, 10.78));
        secondStopPlace.setName(new EmbeddableMultilingualString("Andalsnes", "no"));
        secondStopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);

        Quay terminal2 = new Quay();
        terminal2.setName(new EmbeddableMultilingualString("terminal 2"));
        terminal2.setCentroid(point(60.01, 10.78));
        secondStopPlace.getQuays().add(terminal2);

        StopPlace importResult = mergingStopPlaceImporter.importStopPlaceWithoutNetexMapping(secondStopPlace);

        assertThat(importResult.getNetexId()).isEqualTo(firstStopPlace.getNetexId());
        assertThat(importResult.getQuays()).hasSize(2);
        assertThat(importResult.getVersion()).isEqualTo(2L);
    }

    /**
     * When importing a stop place with matching chouette ID, the quay should be added to existing stop place.
     */
    @Test
    public void detectAndMergeQuaysForExistingStopPlace() throws ExecutionException, InterruptedException {
        final String chouetteId = "OPP:StopArea:321";
        final String chouetteQuayId = "OPP:Quays:3333";

        StopPlace firstStopPlace = new StopPlace();
        firstStopPlace.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add(chouetteId);

        Quay terminal1 = new Quay();
        terminal1.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add(chouetteQuayId);
        terminal1.setCentroid(point(70.000, 10.78));
        firstStopPlace.getQuays().add(terminal1);

        stopPlaceRepository.save(firstStopPlace);

        StopPlace secondStopPlace = new StopPlace();

        // Set same ID as first stop place
        secondStopPlace.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add(chouetteId);

        Quay terminal2 = new Quay();
        terminal2.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add(chouetteQuayId);
        terminal2.setCentroid(point(70.000, 10.78));
        secondStopPlace.getQuays().add(terminal2);

        stopPlaceRepository.save(secondStopPlace);

        // Import only the second stop place as the first one is already "saved" (mocked)
        StopPlace importResult = mergingStopPlaceImporter.importStopPlaceWithoutNetexMapping(secondStopPlace);

        assertThat(importResult.getNetexId()).isEqualTo(firstStopPlace.getNetexId());
        assertThat(importResult.getQuays()).hasSize(1);
    }

    /**
     * When importing a stop place which already exists, matching quays should not be duplicated.
     */
    @Test
    public void detectTwoMatchingQuaysInTwoSeparateStopPlaces() throws ExecutionException, InterruptedException {
        final String chouetteId = "OPP:StopArea:123123";

        StopPlace firstStopPlace = new StopPlace();

        Quay terminal1 = new Quay();
        terminal1.setCentroid(point(70.000, 10.78));
        firstStopPlace.getQuays().add(terminal1);

        firstStopPlace.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add(chouetteId);

        stopPlaceRepository.save(firstStopPlace);

        StopPlace secondStopPlace = new StopPlace();

        // Set same ID as first stop place
        secondStopPlace.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add(chouetteId);

        Quay terminal2 = new Quay();
        terminal2.setCentroid(point(70.000, 10.78));
        secondStopPlace.getQuays().add(terminal2);

        // Import only the second stop place as the first one is already saved
        StopPlace importResult = mergingStopPlaceImporter.importStopPlaceWithoutNetexMapping(secondStopPlace);

        assertThat(importResult.getNetexId()).isEqualTo(firstStopPlace.getNetexId());
        // Expect only one quay when two quays have the same coordinates
        assertThat(importResult.getQuays()).hasSize(1);
        assertThat(importResult.getQuays()).contains(terminal1);
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
