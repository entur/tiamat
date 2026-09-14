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
import static org.assertj.core.api.Assertions.assertThatThrownBy;


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
     * An orphaned parking cannot be authorized under an authorization profile that resolves a
     * parking to its parent stop place, so deletion is refused with an actionable message rather
     * than failing deep inside the authorization stack. Clearing the dangling parentSiteRef turns
     * the parking into a standalone parking, which this class deletes normally - see
     * deleteParkingWithoutParentSiteRef below. Note that the standalone case also threw before this
     * change, so clearing the reference is only a remedy in combination with it.
     */
    @Test
    @Transactional
    public void deletingOrphanedParkingIsRefusedWithAnActionableMessage() throws Exception {

        Parking orphan = new Parking();
        orphan.setVersion(1L);
        orphan.setParentSiteRef(new SiteRefStructure("NSR:StopPlace:doesnotexist"));

        parkingRepository.save(orphan);

        assertThatThrownBy(() -> parkingDeleter.deleteParking(orphan.getNetexId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("NSR:StopPlace:doesnotexist")
                .hasMessageContaining("no longer exists")
                .hasMessageContaining("cleared in the database");
    }

    @Test
    @Transactional
    public void deleteParkingWithoutParentSiteRef() throws Exception {

        Parking standalone = new Parking();
        standalone.setVersion(1L);

        parkingRepository.save(standalone);

        assertThat(parkingDeleter.deleteParking(standalone.getNetexId())).isTrue();
        assertThat(parkingRepository.findByNetexId(standalone.getNetexId())).isEmpty();
    }

}