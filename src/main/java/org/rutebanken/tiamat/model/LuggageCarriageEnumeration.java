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

public enum LuggageCarriageEnumeration {

    UNKNOWN("unknown"),
    NO_BAGGAGE_STORAGE("noBaggageStorage"),
    BAGGAGE_STORAGE("baggageStorage"),
    LUGGAGE_RACKS("luggageRacks"),
    EXTRA_LARGE_LUGGAGE_RACKS("extraLargeLuggageRacks"),
    BAGGAGE_VAN("baggageVan"),
    NO_CYCLES("noCycles"),
    CYCLES_ALLOWED("cyclesAllowed"),
    CYCLES_ALLOWED_IN_VAN("cyclesAllowedInVan"),
    CYCLES_ALLOWED_IN_CARRIAGE("cyclesAllowedInCarriage"),
    CYCLES_ALLOWED_WITH_RESERVATION("cyclesAllowedWithReservation"),
    VEHICLE_TRANSPORT("vehicleTransport");
    private final String value;

    LuggageCarriageEnumeration(String v) {
        value = v;
    }

    public static LuggageCarriageEnumeration fromValue(String v) {
        for (LuggageCarriageEnumeration c : LuggageCarriageEnumeration.values()) {
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
