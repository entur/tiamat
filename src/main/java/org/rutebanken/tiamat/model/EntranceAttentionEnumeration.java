

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum EntranceAttentionEnumeration {

    NONE("none"),
    DOORBELL("doorbell"),
    HELP_POINT("helpPoint"),
    INTERCOM("intercom"),
    OTHER("other");
    private final String value;

    EntranceAttentionEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EntranceAttentionEnumeration fromValue(String v) {
        for (EntranceAttentionEnumeration c: EntranceAttentionEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
