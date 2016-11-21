

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum BoardingPermissionEnumeration {

    NORMAL("normal"),
    EARLY_BOARDING_POSSIBLE_BEFORE_DEPARTURE("earlyBoardingPossibleBeforeDeparture"),
    LATE_ALIGHTING_POSSIBLE_AFTER_ARRIVAL("lateAlightingPossibleAfterArrival"),
    OVERNIGHT_STAY_ONBOARD_ALLOWED("overnightStayOnboardAllowed");
    private final String value;

    BoardingPermissionEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BoardingPermissionEnumeration fromValue(String v) {
        for (BoardingPermissionEnumeration c: BoardingPermissionEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
