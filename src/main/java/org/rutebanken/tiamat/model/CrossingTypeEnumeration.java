

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum CrossingTypeEnumeration {

    LEVEL_CROSSING("levelCrossing"),
    BARROW_CROSSING("barrowCrossing"),
    ROAD_CROSSING("roadCrossing"),
    ROAD_CROSSING_WITH_ISLAND("roadCrossingWithIsland"),
    OTHER("other");
    private final String value;

    CrossingTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CrossingTypeEnumeration fromValue(String v) {
        for (CrossingTypeEnumeration c: CrossingTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
