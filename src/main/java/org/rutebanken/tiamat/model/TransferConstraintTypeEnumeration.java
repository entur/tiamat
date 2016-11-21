package org.rutebanken.tiamat.model;

public enum TransferConstraintTypeEnumeration {

    CAN_TRANSFER("canTransfer"),
    CANNOT_TRANSFER("cannotTransfer"),
    OTHER("other");
    private final String value;

    TransferConstraintTypeEnumeration(String v) {
        value = v;
    }

    public static TransferConstraintTypeEnumeration fromValue(String v) {
        for (TransferConstraintTypeEnumeration c : TransferConstraintTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public String value() {
        return value;
    }

}
