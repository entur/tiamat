

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum PublicUseEnumeration {

    ALL("all"),
    DISABLED_PUBLIC_ONLY("disabledPublicOnly"),
    AUTHORISED_PUBLIC_ONLY("authorisedPublicOnly"),
    STAFF_ONLY("staffOnly"),
    PUBLIC_ONLY("publicOnly");
    private final String value;

    PublicUseEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PublicUseEnumeration fromValue(String v) {
        for (PublicUseEnumeration c: PublicUseEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
