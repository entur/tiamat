

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum LimitedUseTypeEnumeration {


    INTERCHANGE_ONLY("interchangeOnly"),

    NO_DIRECT_ROAD_ACCESS("noDirectRoadAccess"),

    LONG_WALK_TO_ACCESS("longWalkToAccess"),

    ISOLATED("isolated"),

    LIMITED_SERVICE("limitedService"),
    OTHER("other");
    private final String value;

    LimitedUseTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LimitedUseTypeEnumeration fromValue(String v) {
        for (LimitedUseTypeEnumeration c: LimitedUseTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
