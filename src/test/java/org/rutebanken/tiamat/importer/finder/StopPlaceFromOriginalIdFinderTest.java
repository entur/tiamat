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

package org.rutebanken.tiamat.importer.finder;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.rutebanken.tiamat.general.PeriodicCacheLogger;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import static org.mockito.Mockito.*;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

public class StopPlaceFromOriginalIdFinderTest {

    private NetexIdHelper netexIdHelper = new NetexIdHelper(new ValidPrefixList("NSR", new HashMap<>()));

    @Test
    public void findShouldSearchForAllIdsInKeyVal() throws Exception {

        StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);
        StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder = new StopPlaceFromOriginalIdFinder(stopPlaceRepository, 0, 0, TimeUnit.DAYS, new PeriodicCacheLogger(), netexIdHelper);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("100L");
        stopPlace.getOrCreateValues(ORIGINAL_ID_KEY).add("some-original-id");
        stopPlace.getOrCreateValues(ORIGINAL_ID_KEY).add("another-original-id");
        stopPlace.getOrCreateValues(ORIGINAL_ID_KEY).add("the-one-original-id");

        when(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId())).thenReturn(stopPlace);
        when(stopPlaceRepository
                .findByKeyValues(anyString(), anySet()))
                .thenAnswer(invocationOnMock -> !Collections.disjoint((Collection<?>) invocationOnMock.getArguments()[1], stopPlace.getOriginalIds()) ? Sets.newHashSet(stopPlace.getNetexId()) : Sets.newHashSet());

        List<StopPlace> actual = stopPlaceFromOriginalIdFinder.find(stopPlace);
        assertThat(actual).hasSize(1);
    }

    @Test
    public void findShouldHandleEmptyValues() throws Exception {

        StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);
        StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder = new StopPlaceFromOriginalIdFinder(stopPlaceRepository, 0, 0, TimeUnit.DAYS, new PeriodicCacheLogger(), netexIdHelper);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("101L");

        List<StopPlace> actual = stopPlaceFromOriginalIdFinder.find(stopPlace);

        verify(stopPlaceRepository, never()).findFirstByKeyValues(anyString(), anySet());
        verify(stopPlaceRepository, never()).findOne(anyLong());
        assertThat(actual).isEmpty();
    }
}