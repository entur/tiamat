

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum StopPlaceComponentTypeEnumeration {

    QUAY("quay"),
    ACCESS_SPACE("accessSpace"),
    ENTRANCE("entrance"),
    BOARDING_POSITION("boardingPosition"),
    STOPPING_PLACE("stoppingPlace");
    private final String value;

    StopPlaceComponentTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StopPlaceComponentTypeEnumeration fromValue(String v) {
        for (StopPlaceComponentTypeEnumeration c: StopPlaceComponentTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
