package org.rutebanken.tiamat.importer.modifier;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class StopPlaceSplitterTest {

    private StopPlaceSplitter stopPlaceSplitter = new StopPlaceSplitter();

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();

    @Test
    public void splitQuays() {

        StopPlace stopPlace = new StopPlace();

        Quay quay = new Quay();
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(5, 30)));

        Quay quay2 = new Quay();
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(6, 31)));


        stopPlace.getQuays().add(quay);
        stopPlace.getQuays().add(quay2);

        List<StopPlace> actual = stopPlaceSplitter.split(Arrays.asList(stopPlace));

        assertThat(actual).hasSize(2);
    }

    /**
     * NRP-1511
     */
    @Test
    public void oneQuayLacksCoordinates() {

        StopPlace stopPlace = new StopPlace();

        Quay quay = new Quay();
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(5, 30)));

        Quay quay2 = new Quay();
        quay2.setCentroid(null);


        stopPlace.getQuays().add(quay);
        stopPlace.getQuays().add(quay2);

        List<StopPlace> actual = stopPlaceSplitter.split(Arrays.asList(stopPlace));

        assertThat(actual).hasSize(2);
    }

    @Test
    public void splitMultipleQuays() {

        StopPlace originalStopplace = new StopPlace();

        Quay quay1 = new Quay();
        quay1.setNetexId("XYZ:Quay:1");
        quay1.setCentroid(geometryFactory.createPoint(new Coordinate(5, 30)));

        Quay quay2 = new Quay();
        quay2.setNetexId("XYZ:Quay:2");
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(6, 31)));

        Quay quay3 = new Quay();
        quay3.setNetexId("XYZ:Quay:3");
        quay3.setCentroid(geometryFactory.createPoint(new Coordinate(6.00001, 31.00001)));


        originalStopplace.getQuays().add(quay1);
        originalStopplace.getQuays().add(quay2);
        originalStopplace.getQuays().add(quay3);

        List<StopPlace> actual = stopPlaceSplitter.split(Arrays.asList(originalStopplace));

        assertThat(actual).hasSize(2);

        long matchingQuays = actual.stream()
                .flatMap(stopPlace -> stopPlace.getQuays().stream())
                .filter(quay -> {
                    String netexId = quay.getNetexId();
                    return netexId.equals(quay1.getNetexId()) || netexId.equals(quay2.getNetexId()) || netexId.equals(quay3.getNetexId());
                })
                .count();

        assertThat(matchingQuays).as("number of matching quays from netex ID").isEqualTo(3);
    }

    @Test
    public void expectNameButNoOriginalId() {

        StopPlace originalStopplace = new StopPlace();
        originalStopplace.setName(new EmbeddableMultilingualString("name"));
        String originalId = "zYx:StopPlace:321";
        originalStopplace.getOriginalIds().add(originalId);

        Quay quay1 = new Quay();
        quay1.setNetexId("XYZ:Quay:1");
        quay1.setCentroid(geometryFactory.createPoint(new Coordinate(5, 30)));

        Quay quay2 = new Quay();
        quay2.setNetexId("XYZ:Quay:2");
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(6.00001, 31.00001)));


        originalStopplace.getQuays().add(quay1);
        originalStopplace.getQuays().add(quay2);

        List<StopPlace> actual = stopPlaceSplitter.split(Arrays.asList(originalStopplace));

        assertThat(actual).hasSize(2);
        assertThat(actual).extracting(StopPlace::getName)
                .as("name")
                .extracting(EmbeddableMultilingualString::getValue)
                .as("name value")
                .containsExactly("name", "name");

        assertThat(actual
                .stream()
                .flatMap(s -> s.getOriginalIds().stream())
                .anyMatch(s -> s.contains(originalId)))
                .as("Original ID on stop place")
                .isFalse();
    }

    @Test
    public void splitQuaysMultipleStops() {

        int numberOfStops = 10;
        List<StopPlace> stops = new ArrayList<>();

        for (int i = 0; i < numberOfStops; i++) {
            StopPlace stopPlace = new StopPlace();

            Quay quay = new Quay();
            quay.setCentroid(geometryFactory.createPoint(new Coordinate(1+i, 30)));

            Quay quay2 = new Quay();
            quay2.setCentroid(geometryFactory.createPoint(new Coordinate(2+i, 31)));


            stopPlace.getQuays().add(quay);
            stopPlace.getQuays().add(quay2);
            stops.add(stopPlace);
        }

        List<StopPlace> actual = stopPlaceSplitter.split(stops);

        assertThat(actual).hasSize(numberOfStops*2);
    }

    @Test
    public void noSplit() {

        StopPlace stopPlace = new StopPlace();

        Quay quay = new Quay();
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(5, 30)));

        Quay quay2 = new Quay();
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(5.001, 30.0001)));


        stopPlace.getQuays().add(quay);
        stopPlace.getQuays().add(quay2);

        List<StopPlace> actual = stopPlaceSplitter.split(Arrays.asList(stopPlace));

        assertThat(actual).hasSize(1);
    }

}