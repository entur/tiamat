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

import org.junit.Before;
import org.junit.Test;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class ParkingParentSiteRefValidatorTest {

    private StopPlaceRepository stopPlaceRepository;
    private ParkingParentSiteRefValidator validator;

    @Before
    public void setUp() {
        stopPlaceRepository = mock(StopPlaceRepository.class);
        validator = new ParkingParentSiteRefValidator(stopPlaceRepository);
    }

    @Test
    public void parkingWithoutParentSiteRefIsValid() {
        assertThatCode(() -> validator.validate(new Parking())).doesNotThrowAnyException();
        verifyNoInteractions(stopPlaceRepository);
    }

    @Test
    public void parkingWithEmptyParentSiteRefIsValid() {
        Parking parking = new Parking();
        parking.setParentSiteRef(new SiteRefStructure());

        assertThatCode(() -> validator.validate(parking)).doesNotThrowAnyException();
        verifyNoInteractions(stopPlaceRepository);
    }

    @Test
    public void parkingWithResolvableParentSiteRefIsValid() {
        Parking parking = parkingWithParentRef("NSR:StopPlace:1");
        when(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc("NSR:StopPlace:1"))
                .thenReturn(new StopPlace());

        assertThatCode(() -> validator.validate(parking)).doesNotThrowAnyException();
    }

    @Test
    public void parkingWithDanglingParentSiteRefIsRejected() {
        Parking parking = parkingWithParentRef("NSR:StopPlace:doesnotexist");
        when(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc("NSR:StopPlace:doesnotexist"))
                .thenReturn(null);

        assertThatThrownBy(() -> validator.validate(parking))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("NSR:StopPlace:doesnotexist")
                .hasMessageContaining("no stop place exists");
    }

    /**
     * The check must not depend on an AuthorizationService being present - that dependency is
     * exactly what made the behaviour differ between authorization profiles.
     */
    @Test
    public void validationDependsOnlyOnTheStopPlaceRepository() {
        assertThat(ParkingParentSiteRefValidator.class.getDeclaredFields())
                .allMatch(field -> field.getType().equals(StopPlaceRepository.class));
    }

    private Parking parkingWithParentRef(String ref) {
        Parking parking = new Parking();
        parking.setNetexId("NSR:Parking:1");
        SiteRefStructure parentSiteRef = new SiteRefStructure();
        parentSiteRef.setRef(ref);
        parking.setParentSiteRef(parentSiteRef);
        return parking;
    }
}
