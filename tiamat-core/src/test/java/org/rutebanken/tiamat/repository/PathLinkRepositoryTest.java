/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.repository;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.AddressablePlaceRefStructure;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

;


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