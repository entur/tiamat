

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum FlexibleRouteTypeEnumeration {

    FLEXIBLE_AREAS_ONLY("flexibleAreasOnly"),
    HAIL_AND_RIDE_SECTIONS("hailAndRideSections"),
    MIXED("mixed"),
    FIXED("fixed"),
    OTHER("other");
    private final String value;

    FlexibleRouteTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FlexibleRouteTypeEnumeration fromValue(String v) {
        for (FlexibleRouteTypeEnumeration c: FlexibleRouteTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
