package org.rutebanken.tiamat.importer.initial;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Transactional
public class StopPlaceParentUpdaterTest extends TiamatIntegrationTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceParentUpdater parentUpdater;

    @Test
    public void updateParentWithChildren() {
        Point point = geometryFactory.createPoint(new Coordinate(9, 71));
        Point point2 = geometryFactory.createPoint(new Coordinate(9.1, 71.2));
        Point point3 = geometryFactory.createPoint(new Coordinate(9.2, 71.3));

        StopPlace parentStop = new StopPlace();
        parentStop.setName(new EmbeddableMultilingualString("Stop A"));
        parentStop.setNetexId("NSR:Stop:01");
        parentStop.setVersion(1L);
        parentStop.setCentroid(point);

        Quay quay = new Quay();
        quay.setNetexId("NSR:Quay:11");
        quay.setVersion(1L);
        quay.setCentroid(point3);

        StopPlace childStop = new StopPlace();
        childStop.setNetexId("NSR:Stop:11");
        childStop.setVersion(1L);
        childStop.setCentroid(point2);
        childStop.setQuays(new HashSet<>(List.of(quay)));

        stopPlaceRepository.saveAll(asList(parentStop, childStop));

        String netexId = "NSR:Stop:01";
        Set<String> childIds = Set.of("NSR:Stop:11");
        parentUpdater.updateParentWithChildren(netexId, childIds);

        List<StopPlace> savedStopPlaces = stopPlaceRepository.findAll();
        assertThat(savedStopPlaces).hasSize(2);

        StopPlace savedParent = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parentStop.getNetexId());
        assertThat(savedParent).isNotNull();
        assertThat(savedParent.getVersion()).isEqualTo(2L);
        assertThat(savedParent.isParentStopPlace()).isEqualTo(true);
        assertThat(savedParent.getChildren()).hasSize(1);

        StopPlace savedChild = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc("NSR:Stop:11");
        assertThat(savedChild).isNotNull();
        assertThat(savedChild.getVersion()).isEqualTo(2L);
        assertThat(savedChild.getParentSiteRef().getRef()).isEqualTo("NSR:Stop:01");
    }
}
