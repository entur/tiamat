

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum TaxiSubmodeEnumeration {

    UNKNOWN("unknown"),
    UNDEFINED("undefined"),
    COMMUNAL_TAXI("communalTaxi"),
    WATER_TAXI("waterTaxi"),
    RAIL_TAXI("railTaxi"),
    BIKE_TAXI("bikeTaxi"),
    BLACK_CAB("blackCab"),
    MINI_CAB("miniCab"),
    ALL_TAXI_SERVICES("allTaxiServices");
    private final String value;

    TaxiSubmodeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TaxiSubmodeEnumeration fromValue(String v) {
        for (TaxiSubmodeEnumeration c: TaxiSubmodeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
