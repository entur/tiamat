package org.rutebanken.tiamat.importer.filter;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.model.StopTypeEnumeration.AIRPORT;
import static org.rutebanken.tiamat.model.StopTypeEnumeration.ONSTREET_BUS;
import static org.rutebanken.tiamat.model.StopTypeEnumeration.TRAM_STATION;


public class StopPlaceTypeFilterTest {


    private StopPlaceTypeFilter stopPlaceTypeFilter = new StopPlaceTypeFilter();

    @Test
    public void filter() throws Exception {

        StopPlace keep = new StopPlace();
        keep.setNetexId("NSR:StopPlace:1");
        keep.setStopPlaceType(ONSTREET_BUS);

        StopPlace noKeep = new StopPlace();
        noKeep.setNetexId("NSR:StopPlace:2");
        noKeep.setStopPlaceType(TRAM_STATION);

        Set<StopTypeEnumeration> allowedTypes = Sets.newHashSet(ONSTREET_BUS);
        List<StopPlace> actual = stopPlaceTypeFilter.filter(Arrays.asList(keep, noKeep), allowedTypes, false);

        assertThat(actual)
                .extracting(IdentifiedEntity::getNetexId)
                .contains(keep.getNetexId())
                .doesNotContain(noKeep.getNetexId());
    }

    @Test
    public void filterInverse() throws Exception {

        StopPlace bus = new StopPlace();
        bus.setNetexId("NSR:StopPlace:12");
        bus.setStopPlaceType(ONSTREET_BUS);

        StopPlace air = new StopPlace();
        air.setNetexId("NSR:StopPlace:23");
        air.setStopPlaceType(AIRPORT);

        Set<StopTypeEnumeration> allowedTypes = Sets.newHashSet(AIRPORT);

        boolean negate = true;
        List<StopPlace> actual = stopPlaceTypeFilter.filter(Arrays.asList(bus, air), allowedTypes, negate);

        assertThat(actual)
                .extracting(IdentifiedEntity::getNetexId)
                .contains(bus.getNetexId())
                .doesNotContain(air.getNetexId());
    }

    @Test
    public void noTypesReturnsAll() throws Exception {

        StopPlace keep = new StopPlace();
        keep.setNetexId("NSR:StopPlace:1");
        keep.setStopPlaceType(ONSTREET_BUS);

        Set<StopTypeEnumeration> allowedTypes = Sets.newHashSet();
        List<StopPlace> actual = stopPlaceTypeFilter.filter(Arrays.asList(keep), allowedTypes, false);

        assertThat(actual)
                .extracting(IdentifiedEntity::getNetexId)
                .contains(keep.getNetexId());
    }

}