package org.rutebanken.tiamat.repository;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.exporter.params.ParkingSearch;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ParkingRepositoryImplTest extends TiamatIntegrationTest {

    @Transactional
    @Test
    public void distinctResultWhenMultipleParkings() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlaceRepository.save(stopPlace);

        Parking parking1 = new Parking();
        parking1.setVersion(1L);
        parking1.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId(), null));
        parkingRepository.save(parking1);

        Parking parking2 = new Parking();
        parking2.setNetexId(parking1.getNetexId());
        parking2.setVersion(2L);
        parking2.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId(), String.valueOf(stopPlace.getVersion())));
        parkingRepository.save(parking2);

        Parking parking3 = new Parking();
        parking3.setNetexId(parking1.getNetexId());
        parking3.setVersion(3L);
        parking3.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId(), null));
        parkingRepository.save(parking3);


        parkingRepository.flush();

        Set<Long> stopPlaceIds = Sets.newHashSet(stopPlace.getId());
        int count = parkingRepository.countResult(stopPlaceIds);
        assertThat(count).isEqualTo(1);

        Iterator<Parking> parkingIterator = parkingRepository.scrollParkings(stopPlaceIds);
        assertThat(parkingIterator.hasNext()).as("parking iterator").isTrue();
        assertThat(parkingIterator.next().getId()).as("parking id").isEqualTo(parking3.getId());
    }

}