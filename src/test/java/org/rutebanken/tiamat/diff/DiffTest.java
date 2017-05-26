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

public class DiffTest {

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
        List<Difference> differences = compareObjects(oldObject, newObject, "netexId");
        String diff = differences.stream().map(difference -> difference.toString()).collect(Collectors.joining("\n"));


        System.out.println(diff);
        return diff;
    }

    public List<Difference> compareObjects(Object oldObject, Object newObject, String identifierPropertyName) throws IllegalAccessException {
        return compareObjects(null, oldObject, newObject, identifierPropertyName);
    }


    public List<Difference> compareObjects(String property, Object oldObject, Object newObject, String identifierPropertyName) throws IllegalAccessException {
        List<Difference> differences = new ArrayList<>();

        Class clazz = oldObject.getClass();

        Field[] fields = getAllFields(clazz);
        Field identifierField  = Stream.of(fields).filter(field -> field.getName().equals(identifierPropertyName)).findFirst().get();

        if(property == null) {
            property = oldObject.getClass().getSimpleName();
        }

        identifierField.setAccessible(true);

        for (Field field : fields) {

            field.setAccessible(true);

            Object oldvalue = field.get(oldObject);
            Object newValue = field.get(newObject);

            if (oldvalue == null && newValue == null){
                continue;
            }

            if(oldvalue == null && newValue != null || oldvalue != null && newValue == null) {
                differences.add(new Difference(property + '.'+ field.getName(), oldvalue, newValue));
                continue;
            }

            if(Collection.class.isAssignableFrom(field.getType())) {

                Collection oldCollection = (Collection) oldvalue;
                Collection newCollection = (Collection) newValue;


                compareCollection(property + '.' + field.getName(), oldCollection, newCollection, differences, identifierPropertyName, fields);
                continue;
            } else if(Map.class.isAssignableFrom(field.getType())) {
                System.out.println("map not supported yet!");
                continue;

            }

            if(oldvalue == newValue) {
                continue;
            }

            if(oldvalue.equals(newValue)) {
                continue;
            }


            differences.add(new Difference(property + '.' + field.getName(), oldvalue, newValue));
        }


        return differences;
    }

    public class Difference {

        public Difference(String property, Object oldValue, Object newValue) {
            this.property = property;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public String property;
        public Object oldValue;
        public Object newValue;


        public String toString() {
            return property + ": " + oldValue + " => " + newValue;
        }

    }

    public void compareCollection(final String propertyName, Collection oldCollection, Collection newCollection, List<Difference> differences, String identifierPropertyName, Field[] fields) throws IllegalAccessException {

        if(oldCollection == null && newCollection == null) {
            return;
        }

        if(oldCollection == null && newCollection != null) {
            differences.add(new Difference(propertyName, null, newCollection.size()));
        } else if(oldCollection != null && newCollection == null) {
            differences.add(new Difference(propertyName, oldCollection.size(), null));
        } else if(oldCollection.isEmpty() && newCollection.isEmpty()) {
            return;
        } else if(Collections.disjoint(oldCollection, newCollection)) {
            Field identifierField  = Stream.of(fields).filter(field -> field.getName().equals(identifierPropertyName)).findFirst().get();

            Set<Object> ignoreIdentifiers = new HashSet<>();
            compareListItems(propertyName, oldCollection, newCollection, identifierField, differences, identifierPropertyName, ignoreIdentifiers, false);
            compareListItems(propertyName, newCollection, oldCollection, identifierField, differences, identifierPropertyName, ignoreIdentifiers, true);

        }
    }

    public void compareListItems(String propertyName, Collection collection1, Collection collection2, Field identifierField, List<Difference> differences, String identifierPropertyName, Set<Object> ignoreIdentifiers, boolean reverse) throws IllegalAccessException {

        for(Object collection1Item : collection1) {

            Object collection1ItemIdentifier = identifierField.get(collection1Item);
            if(ignoreIdentifiers.contains(collection1ItemIdentifier)) {
                continue;
            }

            boolean foundMatchOnId = false;
            for(Object collection2Item : collection2) {
                Object collection2ItemIdentifier = identifierField.get(collection2Item);
                if(collection1ItemIdentifier.equals(collection2ItemIdentifier)) {

                    String newProperty = propertyName + "[" + collection2ItemIdentifier + "]";
                    ignoreIdentifiers.add(collection1ItemIdentifier);
                    differences.addAll(compareObjects(newProperty, collection1Item, collection2Item, identifierPropertyName));
                    foundMatchOnId = true;
                    break;
                }
            }

            if(!foundMatchOnId) {
                if(reverse) {
                    differences.add(new Difference(propertyName + "[] added", null, collection1Item));
                } else {
                    differences.add(new Difference(propertyName + "[] removed", collection1Item, null));
                }
            }
        }

    }

    public Field[] getAllFields(Class clazz) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            fields.addAll(Arrays.asList(getAllFields(clazz.getSuperclass())));
        }
        return fields.toArray(new Field[]{});
    }

    private String emptyStringIfNull(Object object) {
        return object == null ? "" : object.toString();
    }

    private String getPath(DiffNode diffNode) {
        return diffNode.getPath()
                .getElementSelectors()
                .stream()
                .map(elementSelector -> elementSelector.toString())
                .filter(value -> !Strings.isNullOrEmpty(value))
                .collect(Collectors.joining("."));
    }
}
