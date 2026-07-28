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

package org.rutebanken.tiamat.auth;

import org.junit.Before;
import org.junit.Test;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TiamatEntityResolverTest {

    private StopPlaceRepository stopPlaceRepository;
    private TiamatEntityResolver tiamatEntityResolver;

    @Before
    public void setUp() {
        stopPlaceRepository = mock(StopPlaceRepository.class);
        tiamatEntityResolver = new TiamatEntityResolver();
        ReflectionTestUtils.setField(tiamatEntityResolver, "stopPlaceRepository", stopPlaceRepository);
    }

    @Test
    public void resolvesParkingToItsParentStopPlaceWhenParentExists() {
        StopPlace stopPlace = new StopPlace();
        Parking parking = new Parking();
        parking.setParentSiteRef(new SiteRefStructure("NSR:StopPlace:1"));

        when(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc("NSR:StopPlace:1")).thenReturn(stopPlace);

        Object resolved = tiamatEntityResolver.resolveCorrectEntity(parking);

        assertThat(resolved).isSameAs(stopPlace);
    }

    /**
     * A parking's parentSiteRef can point at a stop place that has since
     * been deleted. Authorization must fall back to checking the parking
     * itself instead of throwing, so that the parking can still be
     * edited/deleted.
     */
    @Test
    public void fallsBackToParkingItselfWhenParentStopPlaceNoLongerExists() {
        Parking parking = new Parking();
        parking.setParentSiteRef(new SiteRefStructure("NSR:StopPlace:doesNotExist"));

        when(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc("NSR:StopPlace:doesNotExist")).thenReturn(null);

        Object resolved = tiamatEntityResolver.resolveCorrectEntity(parking);

        assertThat(resolved).isSameAs(parking);
    }

    @Test
    public void fallsBackToParkingItselfWhenNoParentSiteRef() {
        Parking parking = new Parking();

        Object resolved = tiamatEntityResolver.resolveCorrectEntity(parking);

        assertThat(resolved).isSameAs(parking);
    }
}
