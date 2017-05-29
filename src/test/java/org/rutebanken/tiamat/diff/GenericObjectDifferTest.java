package org.rutebanken.tiamat.diff;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.circular.CircularReferenceMatchingMode;
import de.danielbechler.diff.identity.EqualsIdentityStrategy;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;
import org.assertj.core.util.Strings;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        quay.setNetexId("NSR:Quay:3");;

        newStopPlace.getQuays().add(quay);
        String diffString = compareObjectsAndPrint(oldStopPlace, newStopPlace);

        assertThat(diffString)
                .contains("new stop place quay");
    }

    @Test
    public void diffChangeQuayImportedId() throws IllegalAccessException {
        StopPlace oldStopPlace = new StopPlace();
        StopPlace newStopPlace = new StopPlace();

        Quay quay = new Quay(new EmbeddableMultilingualString("new stop place quay"));
        quay.setNetexId("NSR:Quay:3");;

        newStopPlace.getQuays().add(quay);
        String diffString = compareObjectsAndPrint(oldStopPlace, newStopPlace);

        assertThat(diffString)
                .contains("new stop place quay");
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
    public void testRecursive() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
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


        compareObjectsAndPrint(oldStopPlace, newStopPlace);

    }

    public String compareObjectsAndPrint(Object oldObject, Object newObject) throws IllegalAccessException {
        List<GenericObjectDiffer.Difference> differences = genericObjectDiffer.compareObjects(oldObject, newObject, "netexId");
        String diff = differences.stream().map(difference -> difference.toString()).collect(Collectors.joining("\n"));

        System.out.println("-----------");
        System.out.println(diff);
        return diff;
    }
}
