package org.rutebanken.tiamat.model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import org.junit.Test;
import org.rutebanken.tiamat.CommonSpringBootTest;
import org.rutebanken.tiamat.repository.PathJunctionRepository;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class PathLinkTest extends CommonSpringBootTest {

    @Test
    public void simplePersistTest() {
        PathLink pathLink = new PathLink();
        pathLinkRepository.save(pathLink);
        assertThat(pathLink.getNetexId()).describedAs("Path link should get ID when saved").isNotNull();
    }

    @Test
    public void persistPathLinkWithPathLinkEnd() {
        PathLink pathLink = new PathLink();
        PathLinkEnd from = new PathLinkEnd(new AddressablePlaceRefStructure(createAndSaveStop("A stop place that is referenced to by a path link")));

        pathLink.setFrom(from);

        pathLinkRepository.save(pathLink);

        PathLink actualPathLink = pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(pathLink.getNetexId());

        assertThat(actualPathLink.getFrom()).isNotNull();
    }

    @Test
    public void pathLinkBetweenQuays() {
        Quay quay1 = new Quay();
        Quay quay2 = new Quay();
        quayRepository.save(quay1);
        quayRepository.save(quay2);


        PathLinkEnd from = new PathLinkEnd(new AddressablePlaceRefStructure(quay1));
        PathLinkEnd to = new PathLinkEnd(new AddressablePlaceRefStructure(quay2));

        PathLink pathLink = new PathLink(from, to);
        pathLinkRepository.save(pathLink);

        PathLink actualPathLink = pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(pathLink.getNetexId());

        assertThat(actualPathLink.getFrom().getPlaceRef().getRef()).isEqualTo(quay1.getNetexId());
        assertThat(actualPathLink.getTo().getPlaceRef().getRef()).isEqualTo(quay2.getNetexId());
    }

    @Test
    public void pathLinkWithQuaysAndPathJunction() {
        Quay fromQuay = new Quay();
        Quay toQuay = new Quay();
        fromQuay = quayRepository.save(fromQuay);
        toQuay = quayRepository.save(toQuay);

        PathJunction pathJunction = new PathJunction();
        pathJunction = pathJunctionRepository.save(pathJunction);

        PathLinkEnd pathLinkEndFromQuay = new PathLinkEnd(new AddressablePlaceRefStructure(fromQuay));
        PathLinkEnd pathLinkEndToPathJunction = new PathLinkEnd(pathJunction);
        PathLinkEnd pathLinkEndFromPathJunction = new PathLinkEnd(pathJunction);
        PathLinkEnd pathLinkEndToQuay = new PathLinkEnd(new AddressablePlaceRefStructure(toQuay));

        PathLink pathLinkToPathJunction = new PathLink(pathLinkEndFromQuay, pathLinkEndToPathJunction);
        PathLink pathLinkToQuay = new PathLink(pathLinkEndFromPathJunction, pathLinkEndToQuay);

        pathLinkRepository.save(pathLinkToPathJunction);
        pathLinkRepository.save(pathLinkToQuay);

        PathLink actualPathLinkToPathJunction = pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(pathLinkToPathJunction.getNetexId());
        PathLink actualPathLinkToQuay = pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(pathLinkToQuay.getNetexId());

        assertThat(actualPathLinkToPathJunction.getFrom().getPlaceRef().getRef()).isEqualTo(fromQuay.getNetexId());
        assertThat(actualPathLinkToPathJunction.getTo().getPathJunction().getNetexId()).isEqualTo(pathJunction.getNetexId());

        assertThat(actualPathLinkToQuay.getFrom().getPathJunction().getNetexId()).isEqualTo(pathJunction.getNetexId());
        assertThat(actualPathLinkToQuay.getTo().getPlaceRef().getRef()).isEqualTo(pathLinkToQuay.getTo().getPlaceRef().getRef());
    }

    @Test
    public void pathLinkWithLineString() {

        Coordinate[] coordinates = new Coordinate[2];
        coordinates[0] = new Coordinate(11, 60);
        coordinates[1] = new Coordinate(11.1, 60.1);

        CoordinateSequence points = new CoordinateArraySequence(coordinates);

        LineString lineString = new LineString(points, geometryFactory);

        PathLink pathLink = new PathLink();
        pathLink.setLineString(lineString);

        pathLinkRepository.save(pathLink);

        PathLink actual = pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(pathLink.getNetexId());
        assertThat(actual.getLineString()).isNotNull();
        assertThat(actual.getLineString().getCoordinates()).isEqualTo(coordinates);

    }

    @Test
    public void pathLinkWithTransferDuration() {

        PathLink pathLink = new PathLink();

        TransferDuration transferDuration = new TransferDuration();
        transferDuration.setDefaultDuration(Duration.ofMillis(10000));
        transferDuration.setOccasionalTravellerDuration(Duration.ofMillis(20000));
        transferDuration.setMobilityRestrictedTravellerDuration(Duration.ofMillis(30000));
        transferDuration.setFrequentTravellerDuration(Duration.ofMillis(5000));

        pathLink.setTransferDuration(transferDuration);

        pathLinkRepository.save(pathLink);

        PathLink actual = pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(pathLink.getNetexId());


        assertThat(actual.getTransferDuration()).isNotNull();
        assertThat(actual.getTransferDuration().getDefaultDuration()).isEqualTo(transferDuration.getDefaultDuration());
        assertThat(actual.getTransferDuration().getFrequentTravellerDuration()).isEqualTo(transferDuration.getFrequentTravellerDuration());
        assertThat(actual.getTransferDuration().getOccasionalTravellerDuration()).isEqualTo(transferDuration.getOccasionalTravellerDuration());
        assertThat(actual.getTransferDuration().getMobilityRestrictedTravellerDuration()).isEqualTo(transferDuration.getMobilityRestrictedTravellerDuration());
    }

    @Test
    public void testKeyValueStructure() throws Exception {
        PathLink pathLink = new PathLink();
        List<String> ids = Arrays.asList("OPP:PathLink:123123", "TEL:PathLink:3251321");
        Value value = new Value(ids);
        pathLink.getKeyValues().put("ORIGINAL_ID", value);

        pathLinkRepository.save(pathLink);
        PathLink actual = pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(pathLink.getNetexId());

        assertThat(actual.getKeyValues().get("ORIGINAL_ID").getItems().containsAll(ids));
    }

    private StopPlace createAndSaveStop(String name) {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(name));
        stopPlaceRepository.save(stopPlace);
        return stopPlace;
    }
}