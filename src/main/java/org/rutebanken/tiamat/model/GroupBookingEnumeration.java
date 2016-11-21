

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum GroupBookingEnumeration {

    GROUPS_ALLOWED("groupsAllowed"),
    GROUPS_NOT_ALLOWED("groupsNotAllowed"),
    GROUPS_ALLOWED_WITH_RESERVATION("groupsAllowedWithReservation"),
    GROUP_BOOKINGS_RESTRICTED("groupBookingsRestricted"),
    UNKNOWN("unknown");
    private final String value;

    GroupBookingEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static GroupBookingEnumeration fromValue(String v) {
        for (GroupBookingEnumeration c: GroupBookingEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
