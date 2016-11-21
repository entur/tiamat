

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


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

    public String value() {
        return value;
    }

    public static LuggageCarriageEnumeration fromValue(String v) {
        for (LuggageCarriageEnumeration c: LuggageCarriageEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
