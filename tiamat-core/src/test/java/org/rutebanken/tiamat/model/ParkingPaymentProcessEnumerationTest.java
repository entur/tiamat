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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The NeTEx model classes come from the schema, so they are the reference for this
 * enumeration. The mapper converts between the two types by constant name, which makes an
 * equal set of names the condition for a NeTEx round trip. The declaration order follows
 * the schema so that a reader can compare the two files directly.
 */
public class ParkingPaymentProcessEnumerationTest {

    private static List<String> names(Class<? extends Enum<?>> enumeration) {
        return Arrays.stream(enumeration.getEnumConstants())
                .map(Enum::name)
                .toList();
    }

    @Test
    public void declaresEveryNetexConstantInSchemaOrder() {
        assertThat(names(ParkingPaymentProcessEnumeration.class))
                .isEqualTo(names(org.rutebanken.netex.model.ParkingPaymentProcessEnumeration.class));
    }

    @Test
    public void fromValueResolvesEveryNetexValue() {
        for (org.rutebanken.netex.model.ParkingPaymentProcessEnumeration netexConstant :
                org.rutebanken.netex.model.ParkingPaymentProcessEnumeration.values()) {
            assertThat(ParkingPaymentProcessEnumeration.fromValue(netexConstant.value()).name())
                    .isEqualTo(netexConstant.name());
        }
    }
}
