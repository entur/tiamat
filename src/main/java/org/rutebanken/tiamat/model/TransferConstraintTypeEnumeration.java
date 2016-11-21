

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum TransferConstraintTypeEnumeration {

    CAN_TRANSFER("canTransfer"),
    CANNOT_TRANSFER("cannotTransfer"),
    OTHER("other");
    private final String value;

    TransferConstraintTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TransferConstraintTypeEnumeration fromValue(String v) {
        for (TransferConstraintTypeEnumeration c: TransferConstraintTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
