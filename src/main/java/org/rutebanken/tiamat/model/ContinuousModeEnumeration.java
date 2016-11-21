

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum ContinuousModeEnumeration {

    WALK("walk"),
    CAR("car"),
    TAXI("taxi"),
    CYCLE("cycle"),
    DRT("drt"),
    MOVING_WALKWAY("movingWalkway"),
    THROUGH("through");
    private final String value;

    ContinuousModeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ContinuousModeEnumeration fromValue(String v) {
        for (ContinuousModeEnumeration c: ContinuousModeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
