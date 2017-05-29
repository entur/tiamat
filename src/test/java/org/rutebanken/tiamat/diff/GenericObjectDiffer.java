package org.rutebanken.tiamat.diff;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

@Component
public class GenericObjectDiffer {

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

            Object oldValue = field.get(oldObject);
            Object newValue = field.get(newObject);

            if (oldValue == null && newValue == null){
                continue;
            }

            if(oldValue == null && newValue != null || oldValue != null && newValue == null) {
                differences.add(new Difference(property + '.'+ field.getName(), oldValue, newValue));
                continue;
            }

            if(Collection.class.isAssignableFrom(field.getType())) {

                Collection oldCollection = (Collection) oldValue;
                Collection newCollection = (Collection) newValue;


                compareCollection(property + '.' + field.getName(), oldCollection, newCollection, differences, identifierPropertyName, fields);
                continue;
            } else if(Map.class.isAssignableFrom(field.getType())) {
                System.out.println("map not supported yet!");


                Map oldMap = (Map) oldValue;
                Map newMap = (Map) newValue;

//                compareMap(property + '.' + field.getName())
//

                continue;

            }

            if(oldValue == newValue) {
                continue;
            }

            if(oldValue.equals(newValue)) {
                continue;
            }


            differences.add(new Difference(property + '.' + field.getName(), oldValue, newValue));
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

}
