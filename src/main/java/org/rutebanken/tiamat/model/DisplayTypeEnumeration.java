package org.rutebanken.tiamat.model;

public enum DisplayTypeEnumeration {
    ELECTRIC_TFT("electricTFT"),
    BATTERY_ONE_ROW("batteryOneRow"),
    BATTERY_MULTI_ROW("batteryMultiRow"),
    BATTERY_E_INK("batteryEInk"),
    CHARGEABLE_E_INK("chargeableEInk"),
    NONE("none");

    private final String value;

    DisplayTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DisplayTypeEnumeration fromValue(String v) {

        for (DisplayTypeEnumeration c : DisplayTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v + " is not a valid value of DisplayTypeEnumeration");
    }
}
