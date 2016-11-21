

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum PurchaseMomentEnumeration {

    ON_RESERVATION("onReservation"),
    BEFORE_BOARDING("beforeBoarding"),
    ON_BOARDING("onBoarding"),
    AFTER_BOARDING("afterBoarding"),
    ON_CHECK_OUT("onCheckOut"),
    OTHER("other");
    private final String value;

    PurchaseMomentEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PurchaseMomentEnumeration fromValue(String v) {
        for (PurchaseMomentEnumeration c: PurchaseMomentEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
