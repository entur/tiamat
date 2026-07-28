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

package org.rutebanken.tiamat.service.parking;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class ParkingDeleterTest extends TiamatIntegrationTest {

    @Autowired
    private ParkingDeleter parkingDeleter;

    @Test
    @Transactional
    public void deleteParking() throws Exception {


        StopPlace stopPlace = stopPlaceRepository.save(new StopPlace());

        Parking v1 = new Parking();
        v1.setVersion(1L);
        v1.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));

        parkingRepository.save(v1);

        Parking v2 = new Parking();
        v2.setVersion(2L);
        v2.setNetexId(v1.getNetexId());
        v2.setParentSiteRef(new SiteRefStructure(stopPlace.getNetexId()));

        parkingRepository.save(v2);

        boolean result = parkingDeleter.deleteParking(v1.getNetexId());
        assertThat(result).isTrue();

        List<Parking> parkings = parkingRepository.findByNetexId(v1.getNetexId());
        assertThat(parkings).isEmpty();
    }

    /**
     * A parking's parentSiteRef can point at a stop place that has since
     * been deleted (e.g. the parent was deleted without deleting its child
     * parking first). Deletion must fall back to checking permission on the
     * parking itself rather than refusing to delete it outright.
     */
    @Test
    @Transactional
    public void deleteParkingWithUnresolvableParent() throws Exception {

        Parking v1 = new Parking();
        v1.setVersion(1L);
        v1.setParentSiteRef(new SiteRefStructure("NSR:StopPlace:999999999"));

        parkingRepository.save(v1);

        boolean result = parkingDeleter.deleteParking(v1.getNetexId());
        assertThat(result).isTrue();

        List<Parking> parkings = parkingRepository.findByNetexId(v1.getNetexId());
        assertThat(parkings).isEmpty();
    }

    /**
     * A parking with no parentSiteRef at all (never had a parent) must also
     * fall back to checking permission on the parking itself, rather than
     * refusing to delete it.
     */
    @Test
    @Transactional
    public void deleteParkingWithNoParentSiteRef() throws Exception {

        Parking v1 = new Parking();
        v1.setVersion(1L);

        parkingRepository.save(v1);

        boolean result = parkingDeleter.deleteParking(v1.getNetexId());
        assertThat(result).isTrue();

        List<Parking> parkings = parkingRepository.findByNetexId(v1.getNetexId());
        assertThat(parkings).isEmpty();
    }

}