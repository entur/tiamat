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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guards the parking enumerations against drift from the NeTEx enumerations they mirror.
 * <p>
 * Orika maps an enum by name, through {@link Enum#valueOf}, and throws when the target enum
 * does not define the name. A missing constant therefore fails a whole NeTEx import with a
 * server error, not just the one attribute. Each Tiamat enum below must define every constant
 * of its NeTEx counterpart.
 * <p>
 * The test asserts one direction only. Tiamat may define a constant that NeTEx does not,
 * because that direction cannot fail an import.
 * <p>
 * A failure means a NeTEx version added a constant. Add the same constant to the Tiamat enum.
 * Append it to {@link ParkingLayoutEnumeration} and {@link ParkingReservationEnumeration},
 * because JPA stores those two by ordinal. A new constant in any other position changes the
 * meaning of the rows that are already stored.
 */
public class ParkingEnumerationParityTest {

    @Test
    public void parkingVehicleEnumerationDefinesEveryNetexConstant() {
        assertParity(org.rutebanken.netex.model.ParkingVehicleEnumeration.class, ParkingVehicleEnumeration.class);
    }

    @Test
    public void parkingUserEnumerationDefinesEveryNetexConstant() {
        assertParity(org.rutebanken.netex.model.ParkingUserEnumeration.class, ParkingUserEnumeration.class);
    }

    @Test
    public void parkingStayEnumerationDefinesEveryNetexConstant() {
        assertParity(org.rutebanken.netex.model.ParkingStayEnumeration.class, ParkingStayEnumeration.class);
    }

    @Test
    public void parkingLayoutEnumerationDefinesEveryNetexConstant() {
        assertParity(org.rutebanken.netex.model.ParkingLayoutEnumeration.class, ParkingLayoutEnumeration.class);
    }

    @Test
    public void parkingPaymentProcessEnumerationDefinesEveryNetexConstant() {
        assertParity(org.rutebanken.netex.model.ParkingPaymentProcessEnumeration.class,
                ParkingPaymentProcessEnumeration.class);
    }

    @Test
    public void parkingTypeEnumerationDefinesEveryNetexConstant() {
        assertParity(org.rutebanken.netex.model.ParkingTypeEnumeration.class, ParkingTypeEnumeration.class);
    }

    @Test
    public void parkingReservationEnumerationDefinesEveryNetexConstant() {
        assertParity(org.rutebanken.netex.model.ParkingReservationEnumeration.class,
                ParkingReservationEnumeration.class);
    }

    private static void assertParity(Class<? extends Enum<?>> netexType, Class<? extends Enum<?>> tiamatType) {
        Set<String> tiamatNames = names(tiamatType);
        List<String> missing = names(netexType).stream()
                .filter(name -> !tiamatNames.contains(name))
                .sorted()
                .collect(Collectors.toList());

        assertThat(missing)
                .as("%s must define every constant of %s, because Orika maps the two by name "
                                + "and throws on a name that the target does not define",
                        tiamatType.getSimpleName(), netexType.getName())
                .isEmpty();
    }

    private static Set<String> names(Class<? extends Enum<?>> type) {
        return Arrays.stream(type.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }
}
