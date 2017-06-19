package org.rutebanken.tiamat.importer.finder;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.rutebanken.tiamat.PeriodicCacheLogger;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import static org.mockito.Mockito.*;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

public class StopPlaceFromOriginalIdFinderTest {

    @Test
    public void findShouldSearchForAllIdsInKeyVal() throws Exception {

        StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);
        StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder = new StopPlaceFromOriginalIdFinder(stopPlaceRepository, 0, 0, TimeUnit.DAYS, new PeriodicCacheLogger());

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
        StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder = new StopPlaceFromOriginalIdFinder(stopPlaceRepository, 0, 0, TimeUnit.DAYS, new PeriodicCacheLogger());

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("101L");

        List<StopPlace> actual = stopPlaceFromOriginalIdFinder.find(stopPlace);

        verify(stopPlaceRepository, never()).findFirstByKeyValues(anyString(), anySet());
        verify(stopPlaceRepository, never()).findOne(anyLong());
        assertThat(actual).isEmpty();
    }
}