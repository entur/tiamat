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

public enum ParkingLayoutEnumeration {

    COVERED("covered"),
    OPEN_SPACE("openSpace"),
    MULTISTOREY("multistorey"),
    UNDERGROUND("underground"),
    ROADSIDE("roadside"),
    UNDEFINED("undefined"),
    OTHER("other"),
    CYCLE_HIRE("cycleHire"),
    // JPA stores this enum by ordinal, so a new constant goes at the end. A constant in any
    // other position changes the meaning of the rows that are already stored. This is why the
    // order differs from the NeTEx enumeration, which places ON_PAVEMENT before CYCLE_HIRE.
    ON_PAVEMENT("onPavement");
    private final String value;

    ParkingLayoutEnumeration(String v) {
        value = v;
    }

    public static ParkingLayoutEnumeration fromValue(String v) {
        for (ParkingLayoutEnumeration c : ParkingLayoutEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public String value() {
        return value;
    }

}
