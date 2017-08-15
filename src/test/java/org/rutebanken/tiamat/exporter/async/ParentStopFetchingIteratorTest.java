package org.rutebanken.tiamat.exporter.async;

import org.junit.Test;
import org.onebusaway.gtfs.model.Stop;
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
}