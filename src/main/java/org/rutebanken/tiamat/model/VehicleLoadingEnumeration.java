

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum VehicleLoadingEnumeration {

    NONE("none"),
    LOADING("loading"),
    UNLOADING("unloading"),
    ADDITIONAL_LOADING("additionalLoading"),
    ADDITIONA_UNLOADING("additionaUnloading"),
    UNKNOWN("unknown");
    private final String value;

    VehicleLoadingEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VehicleLoadingEnumeration fromValue(String v) {
        for (VehicleLoadingEnumeration c: VehicleLoadingEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
