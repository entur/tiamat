package org.rutebanken.tiamat.importer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotools.referencing.GeodeticCalculator;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class QuayMergerTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    private QuayMerger quayMerger = new QuayMerger();


    @Test
    public void twoQuaysWithSameOriginalIdAfterPrefixShouldBeTreatedAsSame() {

        AtomicInteger updatedQuaysCounter = new AtomicInteger();
        AtomicInteger createQuaysCounter = new AtomicInteger();

        Quay quay1 = new Quay();
        quay1.setId(123L);
        quay1.setCentroid(geometryFactory.createPoint(new Coordinate(59, 10)));
        quay1.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("BRA:StopArea:123123");

        Quay quay2 = new Quay();
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(60, 11)));
        quay2.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("RUT:StopArea:123123");

        Set<Quay> existingQuays = new HashSet<>();
        existingQuays.add(quay1);

        Set<Quay> incomingQuays = new HashSet<>();
        incomingQuays.add(quay2);

        Set<Quay> result = quayMerger.addNewQuaysOrAppendImportIds(incomingQuays, existingQuays, updatedQuaysCounter, createQuaysCounter);
        assertThat(result).hasSize(1);
    }

    @Test
    public void twoQuaysWithSameOriginalIdButDifferentCoordinatesShouldBeTreatedAsSame() {

        AtomicInteger updatedQuaysCounter = new AtomicInteger();
        AtomicInteger createQuaysCounter = new AtomicInteger();

        Quay quay1 = new Quay();
        quay1.setId(123L);
        quay1.setCentroid(geometryFactory.createPoint(new Coordinate(59, 10)));
        quay1.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-1");

        Quay quay2 = new Quay();
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(60, 11)));
        quay2.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-1");

        Set<Quay> existingQuays = new HashSet<>();
        existingQuays.add(quay1);

        Set<Quay> incomingQuays = new HashSet<>();
        incomingQuays.add(quay2);


        Set<Quay> result = quayMerger.addNewQuaysOrAppendImportIds(incomingQuays, existingQuays, updatedQuaysCounter, createQuaysCounter);
        assertThat(result).hasSize(1);
    }

    /**
     * https://rutebanken.atlassian.net/browse/NRP-894
     */
    @Test
    public void twoQuaysWithDifferentBearingPointShouldNotBeTreatedAsSame() {

        Quay west = new Quay();
        west.setCentroid(geometryFactory.createPoint(new Coordinate(60, 11)));
        west.setCompassBearing(270f);
        west.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-1");

        Quay east = new Quay();
        east.setCentroid(geometryFactory.createPoint(new Coordinate(60, 11)));
        east.setCompassBearing(40f);
        east.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-2");

        Set<Quay> existingQuays = new HashSet<>();
        existingQuays.add(west);

        Set<Quay> incomingQuays = new HashSet<>();
        incomingQuays.add(east);

        Set<Quay> result = quayMerger.addNewQuaysOrAppendImportIds(incomingQuays, existingQuays, new AtomicInteger(), new AtomicInteger());
        assertThat(result).as("Number of quays in response").hasSize(2);
    }

    @Test
    public void twoQuaysWithSimilarCompassBearing() {
        Quay one = new Quay();
        one.setCompassBearing(1f);

        Quay two = new Quay();
        two.setCompassBearing(60f);

        assertThat(quayMerger.haveSimilarOrAnyNullCompassBearing(one, two))
                .as("Quays with less than 180 degrees difference should be treated as same bearing point")
                .isTrue();
    }

    @Test
    public void twoQuaysWithSimilarCompassBearingCrossingZero() {
        Quay one = new Quay();
        one.setCompassBearing(350f);

        Quay two = new Quay();
        two.setCompassBearing(2f);

        assertThat(quayMerger.haveSimilarOrAnyNullCompassBearing(one, two))
                .as("Quays with less than 180 degrees difference should be treated as same bearing point")
                .isTrue();
    }

    @Test
    public void twoQuaysWithTooMuchdifferenceInCompassBearing() {
        Quay one = new Quay();
        one.setCompassBearing(90f);

        Quay two = new Quay();
        two.setCompassBearing(290f);

        assertThat(quayMerger.haveSimilarOrAnyNullCompassBearing(one, two)).isFalse();
    }

    @Test
    public void twoQuaysWithSimilarCompassBearingOneAndThreeFiftyNine() {
        Quay one = new Quay();
        one.setCompassBearing(1f);

        Quay two = new Quay();
        two.setCompassBearing(359f);

        assertThat(quayMerger.haveSimilarOrAnyNullCompassBearing(one, two)).isTrue();
    }

    @Test
    public void twoNewQuaysThatMatchesOnIdMustNotBeAddedMultipleTimes() {
        AtomicInteger updatedQuaysCounter = new AtomicInteger();
        AtomicInteger createQuaysCounter = new AtomicInteger();

        Quay quay1 = new Quay();
        quay1.setCentroid(geometryFactory.createPoint(new Coordinate(59, 10)));
        quay1.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-1");

        Quay quay2 = new Quay();
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(60, 11)));
        quay2.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-1");

        Set<Quay> incomingQuays = new HashSet<>();
        incomingQuays.add(quay2);
        incomingQuays.add(quay1);

        Set<Quay> result = quayMerger.addNewQuaysOrAppendImportIds(incomingQuays, new HashSet<>(), updatedQuaysCounter, createQuaysCounter);
        assertThat(result).hasSize(1);
    }

    @Test
    public void twoNewQuaysThatMatchesOnCoordinatesMustNotBeAddedMultipleTimes() {
        AtomicInteger updatedQuaysCounter = new AtomicInteger();
        AtomicInteger createQuaysCounter = new AtomicInteger();

        Quay quay1 = new Quay();
        quay1.setCentroid(geometryFactory.createPoint(new Coordinate(59, 10)));
        quay1.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-1");

        Quay quay2 = new Quay();
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(59, 10)));
        quay2.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("another-id");

        Set<Quay> incomingQuays = new HashSet<>();
        incomingQuays.add(quay2);
        incomingQuays.add(quay1);

        Set<Quay> result = quayMerger.addNewQuaysOrAppendImportIds(incomingQuays, new HashSet<>(), updatedQuaysCounter, createQuaysCounter);

        assertThat(result).hasSize(1);

        for(Quay actualQuay : result) {
            assertThat(actualQuay.getOriginalIds()).contains("original-id-1", "another-id");
        }
    }


    /**
     * Add two new quays with already existing original IDs with different coordinates that are close to other quay.
     */
    @Test
    public void idsMustNotBeAddedToOtherQuayEvenIfTheyAreClose() {
        Quay existingQuay1 = new Quay(new EmbeddableMultilingualString("Fredheimveien"));
        existingQuay1.setId(1L);
        existingQuay1.setCentroid(geometryFactory.createPoint(new Coordinate(11.142897636770531, 59.83297022041692)));
        existingQuay1.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("RUT:StopArea:0229012202");

        Quay existingQuay2 = new Quay(new EmbeddableMultilingualString("Fredheimveien"));
        existingQuay2.setId(2L);
        existingQuay2.setCentroid(geometryFactory.createPoint(new Coordinate(11.142676854561447, 59.83314448493502)));
        existingQuay2.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("RUT:StopArea:0229012201");

        Set<Quay> existingQuays = new HashSet<>();
        existingQuays.add(existingQuay1);
        existingQuays.add(existingQuay2);

        Quay incomingQuay1 = new Quay(new EmbeddableMultilingualString("Fredheimveien"));
        incomingQuay1.setCentroid(geometryFactory.createPoint(new Coordinate(11.14317535486387, 59.832848923825956)));
        incomingQuay1.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("RUT:StopArea:0229012202");

        Quay incomingQuay2 = new Quay(new EmbeddableMultilingualString("Fredheimveien"));
        incomingQuay2.setCentroid(geometryFactory.createPoint(new Coordinate(11.142902250197631, 59.83304200609072)));
        incomingQuay2.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("RUT:StopArea:0229012201");

        Set<Quay> incomingQuays = new HashSet<>();
        incomingQuays.add(incomingQuay2);
        incomingQuays.add(incomingQuay1);

        Set<Quay> result = quayMerger.addNewQuaysOrAppendImportIds(incomingQuays, existingQuays, new AtomicInteger(), new AtomicInteger());
        assertThat(result).hasSize(2);

        List<String> actualOriginalIds = result.stream()
                .flatMap(q -> q.getOriginalIds().stream())
                .peek(originalId -> System.out.println(originalId))
                .collect(toList());

        assertThat(actualOriginalIds).as("Number of original IDs in total").hasSize(2);

        result.forEach(q -> System.out.println(q.getOriginalIds()));
    }

    @Test
    public void quaysAreClose() {
        Quay quay1 = new Quay();
        quay1.setCentroid(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)));

        Quay quay2 = new Quay();
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)));

        assertThat(quayMerger.areClose(quay1, quay2)).isTrue();
    }

    @Test
    public void quaysAreCloseWithSimilarCoordinates() {
        Quay quay1 = new Quay();
        quay1.setCentroid(geometryFactory.createPoint(new Coordinate(59.933300, 10.775979)));

        Quay quay2 = new Quay();
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)));

        assertThat(quayMerger.areClose(quay1, quay2)).isTrue();
    }

    @Test
    public void doesNotHaveSameCoordinates() {
        Quay quay1 = new Quay();
        quay1.setCentroid(geometryFactory.createPoint(new Coordinate(60, 10.775973)));

        Quay quay2 = new Quay();
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(59.933307, 10.775973)));

        assertThat(quayMerger.areClose(quay1, quay2)).isFalse();
    }

    @Test
    public void notCloseEnoughIfAbout10MetersBetween() {
        Quay quay1 = new Quay(new EmbeddableMultilingualString("One side of the road"));
        quay1.setCentroid(geometryFactory.createPoint(new Coordinate(59.858690, 10.493860)));

        Quay quay2 = new Quay(new EmbeddableMultilingualString("Other side of the road."));
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(59.858684, 10.493682)));
        assertThat(quayMerger.areClose(quay1, quay2)).isFalse();
    }

    @Test
    public void closeEnoughIfAbout8MetersBetween() {
        Quay quay1 = new Quay();
        quay1.setCentroid(geometryFactory.createPoint(new Coordinate(59.858690, 10.493860)));

        Quay quay2 = new Quay();
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(59.858616, 10.493858)));
        assertThat(quayMerger.areClose(quay1, quay2)).isTrue();
    }

    @Test
    public void findQuayIfAlreadyExisting() {

        Point existingQuayPoint = geometryFactory.createPoint(new Coordinate(60, 11));

        Quay existingQuay = new Quay();
        existingQuay.setName(new EmbeddableMultilingualString("existing quay"));
        existingQuay.setCentroid(existingQuayPoint);

        Quay unrelatedExistingQuay = new Quay();
        unrelatedExistingQuay.setName(new EmbeddableMultilingualString("already added quay"));
        unrelatedExistingQuay.setCentroid(geometryFactory.createPoint(new Coordinate(59, 10)));

        Quay newQuayToInspect = new Quay();
        newQuayToInspect.setName(new EmbeddableMultilingualString("New quay which matches existing quay on the coordinates"));
        newQuayToInspect.setCentroid(existingQuayPoint);

        Set<Quay> existingQuays = new HashSet<>(Arrays.asList(existingQuay, unrelatedExistingQuay));
        Set<Quay> newQuays = new HashSet<>(Arrays.asList(unrelatedExistingQuay));

        Set<Quay> actual = quayMerger.addNewQuaysOrAppendImportIds(newQuays, existingQuays, new AtomicInteger(), new AtomicInteger() );
        assertThat(actual).as("The same quay object as existingQuay should be returned").contains(existingQuay);
    }

    /**
     * https://rutebanken.atlassian.net/browse/NRP-1149
     */
    @Test
    public void twoQuaysOneWithCompassBearingAndOtherWithoutShouldMatchIfNearby() {

        Quay first = new Quay();
        first.setCentroid(geometryFactory.createPoint(new Coordinate(60, 11)));
        first.setCompassBearing(270f);
        first.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-1");
        first.setName(new EmbeddableMultilingualString("A"));

        Quay second = new Quay();
        second.setCentroid(geometryFactory.createPoint(new Coordinate(60, 11)));
        // No compass bearing
        second.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-2");
        first.setName(new EmbeddableMultilingualString("A"));

        Set<Quay> existingQuays = new HashSet<>();
        existingQuays.add(first);

        Set<Quay> incomingQuays = new HashSet<>();
        incomingQuays.add(second);

        Set<Quay> result = quayMerger.addNewQuaysOrAppendImportIds(incomingQuays, existingQuays, new AtomicInteger(), new AtomicInteger());
        assertThat(result).as("Number of quays in response should be one. Because one quay lacks compass bearing").hasSize(1);
    }

    @Test
    public void twoQuaysOneWithCompassBearingAndOtherWithoutShouldNotMatchIfNearbyButDifferentName() {

        Quay first = new Quay();
        first.setCentroid(geometryFactory.createPoint(new Coordinate(60, 11)));
        first.setCompassBearing(270f);
        first.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-1");
        first.setName(new EmbeddableMultilingualString("A"));

        Quay second = new Quay();
        second.setCentroid(geometryFactory.createPoint(new Coordinate(60, 11)));
        // No compass bearing
        second.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-2");
        second.setName(new EmbeddableMultilingualString("B"));

        Set<Quay> existingQuays = new HashSet<>();
        existingQuays.add(first);

        Set<Quay> incomingQuays = new HashSet<>();
        incomingQuays.add(second);

        Set<Quay> result = quayMerger.addNewQuaysOrAppendImportIds(incomingQuays, existingQuays, new AtomicInteger(), new AtomicInteger());
        assertThat(result).as("Number of quays in response should be two. Because name differs.").hasSize(2);
    }

    @Test
    public void ifTwoQuaysAreMergedKeepName() {

        Quay first = new Quay();
        first.setCentroid(geometryFactory.createPoint(new Coordinate(60, 11)));
        first.setCompassBearing(270f);
        first.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-1");

        Quay second = new Quay();
        second.setCentroid(geometryFactory.createPoint(new Coordinate(60, 11)));
        // No compass bearing
        second.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-1");
        second.setName(new EmbeddableMultilingualString("A"));

        Set<Quay> existingQuays = new HashSet<>();
        existingQuays.add(first);

        Set<Quay> incomingQuays = new HashSet<>();
        incomingQuays.add(second);

        Set<Quay> result = quayMerger.addNewQuaysOrAppendImportIds(incomingQuays, existingQuays, new AtomicInteger(), new AtomicInteger());
        assertThat(result).as("Quays should have been merged.").hasSize(1);
        Quay actual = result.iterator().next();
        assertThat(actual.getName()).describedAs("name should not be null").isNotNull();
        assertThat(actual.getName().getValue()).isEqualTo("A");
    }


    @Test
    public void ifTwoQuaysAreMergedKeepCompassBearing() {

        Quay first = new Quay();
        first.setCentroid(geometryFactory.createPoint(new Coordinate(60, 11)));
        first.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-1");

        Quay second = new Quay();
        second.setCentroid(geometryFactory.createPoint(new Coordinate(60, 11)));
        second.setCompassBearing(270f);
        second.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-1");

        Set<Quay> existingQuays = new HashSet<>();
        existingQuays.add(first);

        Set<Quay> incomingQuays = new HashSet<>();
        incomingQuays.add(second);

        Set<Quay> result = quayMerger.addNewQuaysOrAppendImportIds(incomingQuays, existingQuays, new AtomicInteger(), new AtomicInteger());
        assertThat(result).as("Quays should have been merged.").hasSize(1);
        Quay actual = result.iterator().next();
        assertThat(actual.getCompassBearing()).describedAs("compass bearing should not be null").isNotNull();
        assertThat(actual.getCompassBearing()).isEqualTo(270f);
    }

    /**
     * When two quays have similar compass bearing, we can use a greater limit for distance in meters when merging.
     */
    @Test
    public void ifTwoQuaysHaveSimilarCompassBearingIncreaseMergeDistance() {

        Quay first = new Quay();
        Point firstQuayPoint = geometryFactory.createPoint(new Coordinate(60, 11));
        first.setCentroid(firstQuayPoint);
        first.setCompassBearing(270f);

        Quay second = new Quay();
        second.setCentroid(getOffsetPoint(firstQuayPoint, 29, 15));
        second.setCompassBearing(270f);

        Set<Quay> existingQuays = new HashSet<>();
        existingQuays.add(first);

        Set<Quay> incomingQuays = new HashSet<>();
        incomingQuays.add(second);

        Set<Quay> result = quayMerger.addNewQuaysOrAppendImportIds(incomingQuays, existingQuays, new AtomicInteger(), new AtomicInteger());
        assertThat(result).as("Quays should have been merged.").hasSize(1);
        Quay actual = result.iterator().next();

        assertThat(actual.getCentroid()).isEqualTo(first.getCentroid());
    }

    @Test
    public void twoQuaysWithSimilarCompassBearingNoMatchIfDistanceExceedsExtendedMergeDistance() {
        int distanceBetweenQuays = 40;

        Quay first = new Quay();
        Point firstQuayPoint = geometryFactory.createPoint(new Coordinate(60, 11));
        first.setCentroid(firstQuayPoint);
        first.setCompassBearing(270f);

        Quay second = new Quay();
        second.setCentroid(getOffsetPoint(firstQuayPoint, distanceBetweenQuays, 15));
        second.setCompassBearing(270f);

        Set<Quay> existingQuays = new HashSet<>();
        existingQuays.add(first);

        Set<Quay> incomingQuays = new HashSet<>();
        incomingQuays.add(second);

        Set<Quay> result = quayMerger.addNewQuaysOrAppendImportIds(incomingQuays, existingQuays, new AtomicInteger(), new AtomicInteger());
        assertThat(result).as("Quays should NOT have been merged, because the distance between them exceeds extended merge distance.").hasSize(2);
        Quay actual = result.iterator().next();

        assertThat(actual.getCentroid()).isEqualTo(first.getCentroid());
    }


    private Point getOffsetPoint(Point point, int offsetMeters, int azimuth) {
        GeodeticCalculator calc = new GeodeticCalculator();
        calc.setStartingGeographicPoint(point.getX(), point.getY());
        calc.setDirection(azimuth, offsetMeters);
        Point2D dest = calc.getDestinationGeographicPoint();
        return geometryFactory.createPoint(new Coordinate(dest.getX(), dest.getY()));
    }

}