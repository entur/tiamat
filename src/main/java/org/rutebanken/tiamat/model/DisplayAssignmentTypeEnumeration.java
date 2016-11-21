

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum DisplayAssignmentTypeEnumeration {

    ARRIVALS("arrivals"),
    DEPARTURES("departures"),
    ALL("all");
    private final String value;

    DisplayAssignmentTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DisplayAssignmentTypeEnumeration fromValue(String v) {
        for (DisplayAssignmentTypeEnumeration c: DisplayAssignmentTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
