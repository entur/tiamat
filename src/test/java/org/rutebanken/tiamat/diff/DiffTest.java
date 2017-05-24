package org.rutebanken.tiamat.diff;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.circular.CircularReferenceMatchingMode;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;
import org.assertj.core.util.Strings;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class DiffTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    private static final ObjectDiffer objectDiffer = ObjectDifferBuilder.startBuilding()
//                .inclusion()
//                    .exclude()
//                        .propertyNameOfType(GeometryCollection.class, "boundary")
//                        .propertyNameOfType(Geometry.class, "boundary")
//                .and()
            .circularReferenceHandling()
            .matchCircularReferencesUsing(CircularReferenceMatchingMode.EQUALS_METHOD)
            .and()
            .comparison()
            .ofType(Geometry.class).toUseEqualsMethod()
            .ofType(Coordinate.class).toUseEqualsMethod()
            .ofType(Point.class).toUseEqualsMethod()
            .and()
//                .differs()
//                .register(new DifferFactory() {
//                    @Override
//                    public Differ createDiffer(DifferDispatcher differDispatcher, NodeQueryService nodeQueryService) {
//                        return null;
//                    }
//                })
            .build();

//    public String executeDiff(StopPlace oldStopPlace, StopPlace newStopPlace) {
//
//    }

    public String diff(Object working, Object base) {
        DiffNode diff = objectDiffer.compare(working, base);

        StringBuilder stringBuilder = new StringBuilder("Diff:\n");

        diff.visit(new DiffNode.Visitor() {

            public void node(DiffNode node, Visit visit) {
                final Object baseValue = node.canonicalGet(base);
                final Object workingValue = node.canonicalGet(working);


                if (baseValue == null) {
                    System.out.println("base value is null for path " + getPath(node));
                    visit.dontGoDeeper();
                } else if (workingValue == null) {
                    System.out.println("working value is null for path " + getPath(node));
                    visit.dontGoDeeper();
                }

//                System.out.println(Collection.class.isAssignableFrom(node.getValueType()) +" "+ node.getValueType());

//                if (!node.hasChildren() || Collection.class.isAssignableFrom(node.getValueType())) {
                    stringBuilder.append(message(node, baseValue, workingValue)).append("\n");
//                }
            }
        });

        return stringBuilder.toString();
    }


    private String printAndReturnDiff(StopPlace newStopPlace, StopPlace oldStopPlace) {
        String diffString = diff(newStopPlace, oldStopPlace);
        System.out.println(diffString);
        return diffString;
    }

    @Test
    public void diffChangeName() {
        StopPlace oldStopPlace = new StopPlace(new EmbeddableMultilingualString("old name"));
        StopPlace newStopPlace = new StopPlace(new EmbeddableMultilingualString("new name"));

        String diffString = printAndReturnDiff(newStopPlace, oldStopPlace);
        assertThat(diffString)
                .contains(newStopPlace.getName().getValue())
                .contains(oldStopPlace.getName().getValue());
    }

    @Test
    public void diffAddCoordinate() {
        StopPlace oldStopPlace = new StopPlace();
        StopPlace newStopPlace = new StopPlace();
        newStopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 22)));
        String diffString = printAndReturnDiff(newStopPlace, oldStopPlace);
        assertThat(diffString)
                .contains("centroid")
                .contains("10")
                .contains("22");
    }

    @Test
    public void diffRemoveQuay() {
        StopPlace oldStopPlace = new StopPlace();
        oldStopPlace.getQuays().add(new Quay(new EmbeddableMultilingualString("old stop place quay")));
        StopPlace newStopPlace = new StopPlace();
        String diffString = printAndReturnDiff(newStopPlace, oldStopPlace);

        assertThat(diffString)
                .contains("old stop place quay")
                .contains("CHANGED");
    }

    @Test
    public void diffAddQuay() {
        StopPlace oldStopPlace = new StopPlace();
        StopPlace newStopPlace = new StopPlace();
        newStopPlace.getQuays().add(new Quay(new EmbeddableMultilingualString("new stop place quay")));
        String diffString = printAndReturnDiff(newStopPlace, oldStopPlace);

        assertThat(diffString)
                .contains("new stop place quay")
                .contains("CHANGED");
    }

    @Test
    public void diffChangeQuay() {
        Quay oldQuay = new Quay(new EmbeddableMultilingualString("old quay"));
        oldQuay.setVersion(1L);

        StopPlace oldStopPlace = new StopPlace();
        oldStopPlace.getQuays().add(oldQuay);

        Quay changedQuay = new Quay(new EmbeddableMultilingualString("quay changed"));
        changedQuay.setVersion(2L);

        StopPlace newStopPlace = new StopPlace();
        newStopPlace.getQuays().add(changedQuay);

        String diffString = printAndReturnDiff(newStopPlace, oldStopPlace);

        assertThat(diffString)
                .contains(changedQuay.getName().getValue())
                .contains("CHANGED");
    }

    private String message(DiffNode diffNode, Object baseValue, Object workingValue) {
        final String path = getPath(diffNode);
        return diffNode.getState() + " " + path + ": '" + emptyStringIfNull(baseValue) + "' => '" + emptyStringIfNull(workingValue) + "'";
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
