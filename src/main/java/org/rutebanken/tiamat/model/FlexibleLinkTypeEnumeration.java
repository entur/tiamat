

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum FlexibleLinkTypeEnumeration {

    HAIL_AND_RIDE("hailAndRide"),
    ON_DEMAND("onDemand"),
    FIXED("fixed"),
    OTHER("other");
    private final String value;

    FlexibleLinkTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FlexibleLinkTypeEnumeration fromValue(String v) {
        for (FlexibleLinkTypeEnumeration c: FlexibleLinkTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
