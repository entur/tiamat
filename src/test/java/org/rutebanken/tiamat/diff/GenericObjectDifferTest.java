package org.rutebanken.tiamat.diff;


import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.model.*;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericObjectDifferTest {

    private static final GenericObjectDiffer genericObjectDiffer = new GenericObjectDiffer();

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    @Test
    public void diffChangeName() throws IllegalAccessException {
        StopPlace oldStopPlace = new StopPlace(new EmbeddableMultilingualString("old name"));
        StopPlace newStopPlace = new StopPlace(new EmbeddableMultilingualString("new name"));

        String diffString = compareObjectsAndPrint(oldStopPlace, newStopPlace);
        assertThat(diffString)
                .contains(newStopPlace.getName().getValue())
                .contains(oldStopPlace.getName().getValue());
    }

    @Test
    public void diffAddCoordinate() throws IllegalAccessException {
        StopPlace oldStopPlace = new StopPlace();
        StopPlace newStopPlace = new StopPlace();
        newStopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 22)));
        String diffString = compareObjectsAndPrint(oldStopPlace, newStopPlace);
        assertThat(diffString)
                .contains("centroid")
                .contains("10")
                .contains("22");
    }

    @Test
    public void diffChangeCoordinate() throws IllegalAccessException {
        StopPlace oldStopPlace = new StopPlace();
        oldStopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.123123, 22.123123)));

        StopPlace newStopPlace = new StopPlace();
        newStopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11, 22)));
        String diffString = compareObjectsAndPrint(oldStopPlace, newStopPlace);
        assertThat(diffString)
                .contains("centroid")
                .contains("10")
                .contains("22");
    }

    @Test
    public void diffStopPlaceWithTariffZone() throws IllegalAccessException {
        StopPlace oldStopPlace = new StopPlace();
        StopPlace newStopPlace = new StopPlace();

        newStopPlace.getTariffZones().add(new TariffZoneRef(new TariffZone()));

        String diffString = compareObjectsAndPrint(oldStopPlace, newStopPlace);
        assertThat(diffString)
                .contains("tariffZones");
    }

    /**
     * If objects have their equal / hash code method implemented and are equal, they should not be treated as different.
     * Also, if the objects have the same identifier, they should not be treated as equal.
     *
     * @throws IllegalAccessException
     */
    @Test
    public void diffStopPlaceWithTariffZones() throws IllegalAccessException {
        StopPlace oldStopPlace = new StopPlace();
        TariffZoneRef tariffZoneRef1 = new TariffZoneRef("NSR:TariffZone:1");
        oldStopPlace.getTariffZones().add(tariffZoneRef1);

        StopPlace newStopPlace = new StopPlace();
        TariffZoneRef tariffZoneRef2 = new TariffZoneRef("NSR:TariffZone:1");
        newStopPlace.getTariffZones().add(tariffZoneRef2);

        String diffString = compareObjectsAndPrint(oldStopPlace, newStopPlace);
        assertThat(diffString)
                .doesNotContain("tariffZones");
    }

    @Test
    public void diffAddQuayCoordinate() throws IllegalAccessException {
        StopPlace oldStopPlace = new StopPlace();
        Quay quay = new Quay();
        quay.setNetexId("NSR:Quay:321");
        oldStopPlace.getQuays().add(quay);

        StopPlace newStopPlace = new StopPlace();

        Quay changedQuay = new Quay();
        changedQuay.setNetexId("NSR:Quay:321");
        changedQuay.setCentroid(geometryFactory.createPoint(new Coordinate(10, 22)));
        newStopPlace.getQuays().add(changedQuay);

        String diffString = compareObjectsAndPrint(oldStopPlace, newStopPlace);
        assertThat(diffString)
                .contains("centroid")
                .contains("10")
                .contains("22");
    }

    @Test
    public void diffRemoveQuay() throws IllegalAccessException {
        StopPlace oldStopPlace = new StopPlace();
        oldStopPlace.getQuays().add(new Quay(new EmbeddableMultilingualString("old stop place quay")));
        StopPlace newStopPlace = new StopPlace();
        String diffString = compareObjectsAndPrint(oldStopPlace, newStopPlace);

        assertThat(diffString)
                .contains("old stop place quay");
    }

    @Test
    public void diffAddQuay() throws IllegalAccessException {
        StopPlace oldStopPlace = new StopPlace();
        StopPlace newStopPlace = new StopPlace();

        Quay quay = new Quay(new EmbeddableMultilingualString("new stop place quay"));
        quay.setNetexId("NSR:Quay:3");
        ;

        newStopPlace.getQuays().add(quay);
        String diffString = compareObjectsAndPrint(oldStopPlace, newStopPlace);

        assertThat(diffString)
                .contains("new stop place quay");
    }

    @Test
    public void diffAddQuayImportedId() throws IllegalAccessException {
        StopPlace oldStopPlace = new StopPlace();
        Quay oldQuay = new Quay();
        oldQuay.setNetexId("NSR:Quay:3");
        oldQuay.getOriginalIds().add("HED:Quay:2");
        oldStopPlace.getQuays().add(oldQuay);

        StopPlace newStopPlace = new StopPlace();
        Quay newQuay = new Quay();
        newQuay.setNetexId("NSR:Quay:3");
        newQuay.getOriginalIds().add("HED:Quay:2");
        newQuay.getOriginalIds().add("OPP:Quay:1");
        newStopPlace.getQuays().add(newQuay);

        String diffString = compareObjectsAndPrint(oldStopPlace, newStopPlace);

        assertThat(diffString)
                .contains("OPP:Quay:1");
    }

    @Test
    public void diffRemoveQuayImportedId() throws IllegalAccessException {
        StopPlace oldStopPlace = new StopPlace();
        Quay oldQuay = new Quay();
        oldQuay.setNetexId("NSR:Quay:3");
        oldQuay.getOriginalIds().add("HED:Quay:2");
        oldQuay.getOriginalIds().add("OPP:Quay:1");
        oldStopPlace.getQuays().add(oldQuay);

        StopPlace newStopPlace = new StopPlace();
        Quay newQuay = new Quay();
        newQuay.setNetexId("NSR:Quay:3");
        newQuay.getOriginalIds().add("HED:Quay:2");
        newStopPlace.getQuays().add(newQuay);

        String diffString = compareObjectsAndPrint(oldStopPlace, newStopPlace);

        assertThat(diffString)
                .contains("OPP:Quay:1");
    }


    @Test
    public void diffChangeQuay() throws IllegalAccessException {
        Quay oldQuay = new Quay(new EmbeddableMultilingualString("old quay"));
        oldQuay.setVersion(1L);
        oldQuay.setNetexId("NSR:Quay:1");

        StopPlace oldStopPlace = new StopPlace();
        oldStopPlace.getQuays().add(oldQuay);

        Quay changedQuay = new Quay(new EmbeddableMultilingualString("quay changed"));
        changedQuay.setVersion(2L);
        changedQuay.setNetexId("NSR:Quay:1");

        StopPlace newStopPlace = new StopPlace();
        newStopPlace.getQuays().add(changedQuay);

        String diffString = compareObjectsAndPrint(oldStopPlace, newStopPlace);

        assertThat(diffString)
                .contains(changedQuay.getName().getValue());
    }

    @Test
    public void detectDifferencesInQuayOnNetexId() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Quay oldQuay = new Quay(new EmbeddableMultilingualString("old quay"));
        oldQuay.setVersion(1L);
        oldQuay.setNetexId("NSR:Quay:1");

        StopPlace oldStopPlace = new StopPlace();
        oldStopPlace.getQuays().add(oldQuay);

        Quay changedQuay = new Quay(new EmbeddableMultilingualString("quay changed", "NO"));
        changedQuay.setVersion(2L);
        changedQuay.setNetexId("NSR:Quay:1");

        StopPlace newStopPlace = new StopPlace();
        newStopPlace.getQuays().add(changedQuay);


        String diffString = compareObjectsAndPrint(oldStopPlace, newStopPlace);
        assertThat(diffString)
                .contains(changedQuay.getName().getValue())
                .contains(oldQuay.getName().getValue())
                .contains("NO");

    }

    @Test
    public void ignoreCertainFields() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Quay oldQuay = new Quay(new EmbeddableMultilingualString("quay 1"));
        oldQuay.setNetexId("NSR:Quay:1");

        StopPlace oldStopPlace = new StopPlace();
        oldStopPlace.getQuays().add(oldQuay);

        Quay changedQuay = new Quay(new EmbeddableMultilingualString("quay 2", "NO"));
        changedQuay.setNetexId("NSR:Quay:1");

        StopPlace newStopPlace = new StopPlace();
        newStopPlace.getQuays().add(changedQuay);


        List<Difference> differences = genericObjectDiffer.compareObjects(oldStopPlace,
                newStopPlace, Sets.newHashSet("netexId"), Sets.newHashSet("id", "name"), Sets.newHashSet());
        assertThat(differences).hasSize(0);
    }

    @Test
    public void diffStopPlaceAccessibilityLimitation() throws IllegalAccessException {
        StopPlace stopPlace = new StopPlace();

        AccessibilityLimitation accessibilityLimitation = new AccessibilityLimitation();
        accessibilityLimitation.setNetexId("NSR:AccessibilityLimitation:1");

        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();
        accessibilityAssessment.setNetexId("NSR:AccessibilityAssessment:1");

        accessibilityAssessment.setLimitations(Arrays.asList(accessibilityLimitation));

        stopPlace.setAccessibilityAssessment(accessibilityAssessment);

        StopPlace stopPlace2 = new StopPlace();

        AccessibilityLimitation accessibilityLimitation2 = new AccessibilityLimitation();
        accessibilityLimitation2.setNetexId("NSR:AccessibilityLimitation:1");
        accessibilityLimitation2.setChanged(Instant.now());

        AccessibilityAssessment accessibilityAssessment2 = new AccessibilityAssessment();
        accessibilityAssessment2.setNetexId("NSR:AccessibilityAssessment:1");
        accessibilityAssessment2.setVersion(2L);

        accessibilityAssessment2.setLimitations(Arrays.asList(accessibilityLimitation2));

        stopPlace2.setAccessibilityAssessment(accessibilityAssessment2);

        String diff = compareObjectsAndPrint(stopPlace, stopPlace2);
        assertThat(diff)
                .contains("accessibilityAssessment")
                .contains("limitations")
                .contains("version");

    }

    @Test
    public void maxDepth() throws IllegalAccessException {

        RecursiveObject recursiveObject1 = new RecursiveObject();
        RecursiveObject recursiveObject2 = new RecursiveObject();

        addRecursively(recursiveObject1, 0, 20);
        addRecursively(recursiveObject2, 20, 40);


        List<Difference> differences = genericObjectDiffer.compareObjects(recursiveObject1, recursiveObject2, null, Sets.newHashSet(), Sets.newHashSet());
        System.out.println(genericObjectDiffer.diffListToString(differences));

        assertThat(differences).hasSize(10);

    }

    public void addRecursively(RecursiveObject recursiveObject, int depth, int maxDepth) {

        if(depth >= maxDepth) {
            return;
        }
        RecursiveObject newChild = new RecursiveObject();
        newChild.value = depth;
        recursiveObject.child = newChild;
        addRecursively(newChild, ++depth, maxDepth);
    }

    public String compareObjectsAndPrint(Object oldObject, Object newObject) throws IllegalAccessException {
        List<Difference> differences = genericObjectDiffer.compareObjects(oldObject, newObject, Sets.newHashSet("netexId", "ref"), Sets.newHashSet("id"), Sets.newHashSet(Geometry.class));
        String diff = genericObjectDiffer.diffListToString(differences);

        System.out.println("-----------");
        System.out.println(diff);
        System.out.println("-----------");
        return diff;
    }

    private static class RecursiveObject {

        private RecursiveObject child;

        public int value;

        public RecursiveObject() {
        }

        @Override
        public String toString() {
            return "depth: "+ value;
        }


    }
}
