

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum ServiceJourneyPatternTypeEnumeration {

    PASSENGER("passenger"),

    GARAGE_RUN_OUT("garageRunOut"),

    GARAGE_RUN_IN("garageRunIn"),

    TURNING_MANOEUVRE("turningManoeuvre"),
    OTHER("other");
    private final String value;

    ServiceJourneyPatternTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ServiceJourneyPatternTypeEnumeration fromValue(String v) {
        for (ServiceJourneyPatternTypeEnumeration c: ServiceJourneyPatternTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
