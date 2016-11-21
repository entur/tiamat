

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum DeliveryVariantTypeEnumeration {

    PRINTED("printed"),
    TEXT_TO_SPEECH("textToSpeech"),
    WEB("web"),
    MOBILE("mobile"),
    OTHER("other");
    private final String value;

    DeliveryVariantTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DeliveryVariantTypeEnumeration fromValue(String v) {
        for (DeliveryVariantTypeEnumeration c: DeliveryVariantTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
