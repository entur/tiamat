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

package org.rutebanken.tiamat.model;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class ParkingEntranceForVehiclesTest {

    @Test
    public void accessModesListRoundTripsKnownValues() {
        ParkingEntranceForVehicles entrance = new ParkingEntranceForVehicles();
        entrance.setAccessModesList(List.of(AccessModeEnumeration.FOOT, AccessModeEnumeration.BICYCLE));

        assertThat(entrance.getAccessModes()).isEqualTo("foot bicycle");
        assertThat(entrance.getAccessModesList())
                .containsExactly(AccessModeEnumeration.FOOT, AccessModeEnumeration.BICYCLE);
    }

    @Test
    public void accessModesListIsEmptyWhenNotSet() {
        ParkingEntranceForVehicles entrance = new ParkingEntranceForVehicles();

        assertThat(entrance.getAccessModesList()).isEmpty();
    }

    /**
     * A stored token that no longer maps to a known enum value - for example after an
     * {@link AccessModeEnumeration} value is renamed or removed - must not make every read of
     * the entrance throw. The unknown token is skipped and the known ones still come back.
     */
    @Test
    public void accessModesListSkipsUnknownStoredTokens() {
        ParkingEntranceForVehicles entrance = new ParkingEntranceForVehicles();
        entrance.setAccessModes("foot unicycle bicycle");

        assertThatCode(entrance::getAccessModesList).doesNotThrowAnyException();
        assertThat(entrance.getAccessModesList())
                .containsExactly(AccessModeEnumeration.FOOT, AccessModeEnumeration.BICYCLE);
    }
}
