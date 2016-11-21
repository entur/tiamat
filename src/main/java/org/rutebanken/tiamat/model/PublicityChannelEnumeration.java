

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum PublicityChannelEnumeration {


    ALL("all"),

    PRINTED_MEDIA("printedMedia"),

    DYNAMIC_MEDIA("dynamicMedia"),
    NONE("none");
    private final String value;

    PublicityChannelEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PublicityChannelEnumeration fromValue(String v) {
        for (PublicityChannelEnumeration c: PublicityChannelEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
