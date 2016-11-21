

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum RelationToVehicleEnumeration {

    FRONT_LEFT("frontLeft"),
    FRONT_RIGHT("frontRight"),
    BACK_RIGHT("backRight"),
    DRIVER_LEFT("driverLeft"),
    DRIVER_RIGHT("driverRight");
    private final String value;

    RelationToVehicleEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RelationToVehicleEnumeration fromValue(String v) {
        for (RelationToVehicleEnumeration c: RelationToVehicleEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
