package org.rutebanken.tiamat.repository;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class PathLinkRepositoryTest extends TiamatIntegrationTest {

    @Test
    public void findPathLinksFromStopPlacePrimaryIds() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlaceRepository.save(stopPlace);

        StopPlace stopPlace2 = new StopPlace();
        stopPlace2.setVersion(2L);
        stopPlaceRepository.save(stopPlace2);

        stopPlaceRepository.flush();

        PathLink pathLink = new PathLink(new PathLinkEnd(new AddressablePlaceRefStructure(stopPlace)), new PathLinkEnd(new AddressablePlaceRefStructure(stopPlace2)));

        pathLinkRepository.save(pathLink);
        pathLinkRepository.flush();
        System.out.println("saved "+pathLink);

        Set<Long> stopPlaceIds = Sets.newSet(stopPlace.getId(), stopPlace2.getId());
        List<PathLink> pathLinkList = pathLinkRepository.findByStopPlaceIds(stopPlaceIds);

        assertThat(pathLinkList).extracting("id").contains(pathLink.getId());

    }

    @Test
    public void findPathLinksFromStopPlacePrimaryIdsWhenRefsPointsToQuays() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);

        Quay quay = new Quay();
        quay.setVersion(2L);
        stopPlace.getQuays().add(quay);

        StopPlace stopPlace2 = new StopPlace();
        stopPlace2.setVersion(2L);

        Quay quay2 = new Quay();
        quay2.setVersion(3L);
        stopPlace2.getQuays().add(quay2);

        stopPlaceRepository.save(stopPlace);
        stopPlaceRepository.save(stopPlace2);

        PathLink pathLink = new PathLink(new PathLinkEnd(new AddressablePlaceRefStructure(quay)), new PathLinkEnd(new AddressablePlaceRefStructure(stopPlace2)));

        pathLinkRepository.save(pathLink);
        pathLinkRepository.flush();

        Set<Long> stopPlaceIds = Sets.newSet(stopPlace.getId(), stopPlace2.getId());
        List<PathLink> pathLinkList = pathLinkRepository.findByStopPlaceIds(stopPlaceIds);

        assertThat(pathLinkList).extracting("id").contains(pathLink.getId());
    }
}