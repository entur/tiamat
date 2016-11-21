

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum FunicularSubmodeEnumeration {

    UNKNOWN("unknown"),
    FUNICULAR("funicular"),
    STREET_CABLE_CAR("streetCableCar"),
    ALL_FUNICULAR_SERVICES("allFunicularServices"),
    UNDEFINED_FUNICULAR("undefinedFunicular");
    private final String value;

    FunicularSubmodeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FunicularSubmodeEnumeration fromValue(String v) {
        for (FunicularSubmodeEnumeration c: FunicularSubmodeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
