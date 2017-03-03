package org.rutebanken.tiamat.importer.finder;

import org.junit.Test;
import org.rutebanken.tiamat.importer.finder.StopPlaceFromOriginalIdFinder;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

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
        StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder = new StopPlaceFromOriginalIdFinder(stopPlaceRepository, 0, 0, TimeUnit.DAYS);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("100L");
        stopPlace.getOrCreateValues(ORIGINAL_ID_KEY).add("some-original-id");
        stopPlace.getOrCreateValues(ORIGINAL_ID_KEY).add("another-original-id");
        stopPlace.getOrCreateValues(ORIGINAL_ID_KEY).add("the-one-original-id");

        when(stopPlaceRepository.findByNetexId(stopPlace.getNetexId())).thenReturn(stopPlace);
        when(stopPlaceRepository
                .findByKeyValue(ORIGINAL_ID_KEY, stopPlace.getKeyValues().get(ORIGINAL_ID_KEY).getItems()))
                .thenReturn(stopPlace.getNetexId());

        StopPlace actual = stopPlaceFromOriginalIdFinder.find(stopPlace);
        assertThat(actual).isNotNull();
    }

    @Test
    public void findShouldHandleEmptyValues() throws Exception {

        StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);
        StopPlaceFromOriginalIdFinder stopPlaceFromOriginalIdFinder = new StopPlaceFromOriginalIdFinder(stopPlaceRepository, 0, 0, TimeUnit.DAYS);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("101L");

        StopPlace actual = stopPlaceFromOriginalIdFinder.find(stopPlace);

        verify(stopPlaceRepository, never()).findByKeyValue(anyString(), anySet());
        verify(stopPlaceRepository, never()).findOne(anyLong());
        assertThat(actual).isNull();
    }
}