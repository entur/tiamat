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

package org.rutebanken.tiamat.rest.graphql.fetchers;

import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.model.ParkingCapacity;
import org.rutebanken.tiamat.model.ParkingProperties;

import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ParkingUpdaterTest {

    private static ParkingCapacity capacity(Integer spaces, Integer rechargeSpaces) {
        ParkingCapacity capacity = new ParkingCapacity();
        capacity.setNumberOfSpaces(spaces == null ? null : BigInteger.valueOf(spaces));
        capacity.setNumberOfSpacesWithRechargePoint(rechargeSpaces == null ? null : BigInteger.valueOf(rechargeSpaces));
        return capacity;
    }

    private static List<ParkingProperties> properties(ParkingCapacity... capacities) {
        ParkingProperties parkingProperties = new ParkingProperties();
        parkingProperties.setSpaces(List.of(capacities));
        return List.of(parkingProperties);
    }

    @Test
    public void elementWithSpacesOnlyCountsItsSpaces() {
        assertThat(ParkingUpdater.sumCapacity(properties(capacity(10, null)))).isEqualTo(10);
    }

    @Test
    public void elementWithRechargeSpacesOnlyCountsThem() {
        assertThat(ParkingUpdater.sumCapacity(properties(capacity(null, 5)))).isEqualTo(5);
    }

    @Test
    public void elementWithBothNumbersCountsSpacesOnly() {
        assertThat(ParkingUpdater.sumCapacity(properties(capacity(10, 4)))).isEqualTo(10);
    }

    @Test
    public void elementWithZeroSpacesAndRechargeSpacesCountsZero() {
        assertThat(ParkingUpdater.sumCapacity(properties(capacity(0, 5)))).isZero();
    }

    @Test
    public void elementWithNeitherNumberCountsZero() {
        assertThat(ParkingUpdater.sumCapacity(properties(capacity(null, null)))).isZero();
    }

    @Test
    public void rechargeOnlyElementAddsToElementWithSpaces() {
        assertThat(ParkingUpdater.sumCapacity(properties(capacity(null, 5), capacity(2, null)))).isEqualTo(7);
    }

    @Test
    public void propertiesWithoutSpacesCountZero() {
        assertThat(ParkingUpdater.sumCapacity(List.of(new ParkingProperties()))).isZero();
    }
}
