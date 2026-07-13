package org.rutebanken.tiamat.importer.initial;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Transactional
public class StopPlaceParentCreatorTest extends TiamatIntegrationTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private StopPlaceParentCreator parentStopPlaceCreator;

    @Test
    public void createParentStopPlaceWithChildren() {
        StopPlace parentStop = new StopPlace();
        parentStop.setName(new EmbeddableMultilingualString("Stop A"));
        parentStop.setNetexId("NSR:StopPlace:01");
        parentStop.setCentroid(geometryFactory.createPoint(new Coordinate(9, 71)));

        Quay quay = new Quay();
        quay.setNetexId("NSR:Quay:11");
        quay.setVersion(1L);
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(9.2, 71.3)));

        Quay quay2 = new Quay();
        quay2.setNetexId("NSR:Quay:12");
        quay2.setVersion(1L);
        quay2.setCentroid(geometryFactory.createPoint(new Coordinate(9.3, 71.4)));

        StopPlace childStop = new StopPlace();
        childStop.setNetexId("NSR:StopPlace:11");
        childStop.setVersion(1L);
        childStop.setCentroid(geometryFactory.createPoint(new Coordinate(9.1, 71.2)));
        childStop.setQuays(new HashSet<>(List.of(quay)));

        StopPlace childStop2 = new StopPlace();
        childStop2.setNetexId("NSR:StopPlace:12");
        childStop2.setVersion(1L);
        childStop2.setCentroid(geometryFactory.createPoint(new Coordinate(9.3, 71.4)));
        childStop2.setQuays(new HashSet<>(List.of(quay2)));

        stopPlaceRepository.saveAll(asList(childStop, childStop2));

        Set<String> childIds = Set.of("NSR:StopPlace:11", "NSR:StopPlace:12");
        parentStopPlaceCreator.createParentStopWithChildren(parentStop, childIds);

        List<StopPlace> savedStopPlaces = stopPlaceRepository.findAll();
        assertThat(savedStopPlaces).hasSize(3);

        StopPlace savedParent = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parentStop.getNetexId());
        assertThat(savedParent).isNotNull();
        assertThat(savedParent.getVersion()).isEqualTo(1L);
        assertThat(savedParent.isParentStopPlace()).isEqualTo(true);

        StopPlace savedChild = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc("NSR:StopPlace:11");
        assertThat(savedChild).isNotNull();
        assertThat(savedChild.getVersion()).isEqualTo(2L);
        assertThat(savedChild.getParentSiteRef().getRef()).isEqualTo("NSR:StopPlace:01");

        StopPlace savedChild2 = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc("NSR:StopPlace:12");
        assertThat(savedChild2).isNotNull();
        assertThat(savedChild2.getVersion()).isEqualTo(2L);
        assertThat(savedChild2.getParentSiteRef().getRef()).isEqualTo("NSR:StopPlace:01");
    }
}
