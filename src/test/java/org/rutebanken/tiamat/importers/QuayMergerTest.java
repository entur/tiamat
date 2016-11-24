package org.rutebanken.tiamat.importers;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.repository.QuayRepository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class QuayMergerTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();
    
    private QuayRepository quayRepository = mock(QuayRepository.class);
    
    private QuayMerger quayMerger = new QuayMerger(new KeyValueListAppender(), quayRepository);

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

        Quay alreadyAdded = new Quay();
        alreadyAdded.setName(new EmbeddableMultilingualString("already added quay"));
        alreadyAdded.setCentroid(geometryFactory.createPoint(new Coordinate(59, 10)));

        Quay newQuayToInspect = new Quay();
        newQuayToInspect.setName(new EmbeddableMultilingualString("New quay which matches existing quay on the coordinates"));
        newQuayToInspect.setCentroid(existingQuayPoint);

        List<Quay> existingQuays = Arrays.asList(existingQuay);
        List<Quay> alreadyAddedQuays = Arrays.asList(alreadyAdded);

        Quay actual = quayMerger.findQuayWithCoordinates(newQuayToInspect, existingQuays, alreadyAddedQuays).get();
        assertThat(actual).as("The same quay object as existingQuay should be returned").isSameAs(existingQuay);
    }

    @Test
    public void findQuayIfAlreadyAdded() {

        Point alreadyAddedQuayPoint = geometryFactory.createPoint(new Coordinate(61, 12));

        Quay existingQuay = new Quay();
        existingQuay.setName(new EmbeddableMultilingualString("Existing quay"));
        existingQuay.setCentroid(geometryFactory.createPoint(new Coordinate(71, 9)));

        Quay alreadyAddedQuay = new Quay();
        alreadyAddedQuay.setName(new EmbeddableMultilingualString("Quay to be added"));
        alreadyAddedQuay.setCentroid(alreadyAddedQuayPoint);

        Quay newQuayToInspect = new Quay();
        newQuayToInspect.setName(new EmbeddableMultilingualString("New quay to check for match"));
        newQuayToInspect.setCentroid(alreadyAddedQuayPoint);

        List<Quay> existingQuays = Arrays.asList(existingQuay);
        List<Quay> alreadyAddedQuays = Arrays.asList(alreadyAddedQuay);

        Quay actual = quayMerger.findQuayWithCoordinates(newQuayToInspect, existingQuays, alreadyAddedQuays).get();
        assertThat(actual).as("The same quay object as addedQuay should be returned").isSameAs(alreadyAddedQuay);
    }
}