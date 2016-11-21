

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum ParkingLayoutEnumeration {

    COVERED("covered"),
    OPEN_SPACE("openSpace"),
    MULTISTOREY("multistorey"),
    UNDERGROUND("underground"),
    ROADSIDE("roadside"),
    UNDEFINED("undefined"),
    OTHER("other"),
    CYCLE_HIRE("cycleHire");
    private final String value;

    ParkingLayoutEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ParkingLayoutEnumeration fromValue(String v) {
        for (ParkingLayoutEnumeration c: ParkingLayoutEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
