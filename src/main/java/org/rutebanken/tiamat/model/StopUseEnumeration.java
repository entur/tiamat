

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum StopUseEnumeration {


    ACCESS("access"),

    INTERCHANGE_ONLY("interchangeOnly"),

    PASSTHROUGH("passthrough"),
    NO_BOARDING_OR_ALIGHTING("noBoardingOrAlighting");
    private final String value;

    StopUseEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StopUseEnumeration fromValue(String v) {
        for (StopUseEnumeration c: StopUseEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
