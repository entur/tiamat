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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class ParkingDeleterTest extends TiamatIntegrationTest {

    @Autowired
    private ParkingDeleter parkingDeleter;

    @Test
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

}