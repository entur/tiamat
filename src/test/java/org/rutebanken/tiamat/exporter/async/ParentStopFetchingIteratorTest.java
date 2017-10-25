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

package org.rutebanken.tiamat.exporter.async;

import org.junit.Test;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParentStopFetchingIteratorTest {

    private StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);


    @Test
    public void testParent() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:1");
        stopPlace.setVersion(1L);

        StopPlace parent = new StopPlace();
        parent.setNetexId("NSR:StopPlace:2");
        parent.setVersion(1L);

        stopPlace.setParentSiteRef(new SiteRefStructure(parent.getNetexId(), String.valueOf(parent.getVersion())));

        when(stopPlaceRepository.findFirstByNetexIdAndVersion(parent.getNetexId(), parent.getVersion())).thenReturn(parent);

        List<StopPlace> stopPlaces = Arrays.asList(stopPlace);

        ParentStopFetchingIterator parentStopFetchingIterator = new ParentStopFetchingIterator(stopPlaces.iterator(), stopPlaceRepository);

        assertThat(parentStopFetchingIterator.hasNext()).isTrue();

        StopPlace actual = parentStopFetchingIterator.next();
        assertThat(actual.getNetexId()).isEqualTo(stopPlace.getNetexId());

        assertThat(parentStopFetchingIterator.hasNext()).isTrue();

        StopPlace actualParent = parentStopFetchingIterator.next();
        assertThat(actualParent.getNetexId()).isEqualTo(parent.getNetexId());
    }

    @Test
    public void testParentAvoidDuplicatedParents() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:1");
        stopPlace.setVersion(1L);

        StopPlace stopPlace2 = new StopPlace();
        stopPlace2.setNetexId("NSR:StopPlace:3");
        stopPlace2.setVersion(1L);

        StopPlace parent = new StopPlace();
        parent.setNetexId("NSR:StopPlace:2");
        parent.setVersion(1L);

        stopPlace.setParentSiteRef(new SiteRefStructure(parent.getNetexId(), String.valueOf(parent.getVersion())));
        stopPlace2.setParentSiteRef(new SiteRefStructure(parent.getNetexId(), String.valueOf(parent.getVersion())));

        when(stopPlaceRepository.findFirstByNetexIdAndVersion(parent.getNetexId(), parent.getVersion())).thenReturn(parent);

        List<StopPlace> stopPlaces = Arrays.asList(stopPlace, stopPlace2);

        ParentStopFetchingIterator parentStopFetchingIterator = new ParentStopFetchingIterator(stopPlaces.iterator(), stopPlaceRepository);

        assertThat(parentStopFetchingIterator.hasNext()).isTrue();

        StopPlace actual = parentStopFetchingIterator.next();
        assertThat(actual.getNetexId()).isEqualTo(stopPlace.getNetexId());

        assertThat(parentStopFetchingIterator.hasNext()).isTrue();

        StopPlace actualParent = parentStopFetchingIterator.next();
        assertThat(actualParent.getNetexId()).isEqualTo(parent.getNetexId());

        StopPlace actual2 = parentStopFetchingIterator.next();
        assertThat(actual2.getNetexId()).isEqualTo(stopPlace2.getNetexId());

        assertThat(parentStopFetchingIterator.hasNext()).isFalse();

    }
}