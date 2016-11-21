

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum TopographicPlaceTypeEnumeration {

    STATE("state"),

    PROVINCE("province"),
    REGION("region"),
    COUNTY("county"),
    AREA("area"),
    CONURBATION("conurbation"),

    CITY("city"),
    QUARTER("quarter"),

    SUBURB("suburb"),

    TOWN("town"),

    URBAN_CENTRE("urbanCentre"),
    DISTRICT("district"),
    PARISH("parish"),

    VILLAGE("village"),

    HAMLET("hamlet"),

    PLACE_OF_INTEREST("placeOfInterest"),

    OTHER("other"),

    UNRECORDED("unrecorded");
    private final String value;

    TopographicPlaceTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TopographicPlaceTypeEnumeration fromValue(String v) {
        for (TopographicPlaceTypeEnumeration c: TopographicPlaceTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
