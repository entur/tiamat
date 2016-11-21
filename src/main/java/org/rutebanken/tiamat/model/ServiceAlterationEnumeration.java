

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum ServiceAlterationEnumeration {

    EXTRA_JOURNEY("extraJourney"),
    CANCELLATION("cancellation"),
    PLANNED("planned");
    private final String value;

    ServiceAlterationEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ServiceAlterationEnumeration fromValue(String v) {
        for (ServiceAlterationEnumeration c: ServiceAlterationEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
