package org.rutebanken.tiamat.model;

public enum VehicleLoadingEnumeration {

    NONE("none"),
    LOADING("loading"),
    UNLOADING("unloading"),
    ADDITIONAL_LOADING("additionalLoading"),
    ADDITIONA_UNLOADING("additionaUnloading"),
    UNKNOWN("unknown");
    private final String value;

    VehicleLoadingEnumeration(String v) {
        value = v;
    }

    public static VehicleLoadingEnumeration fromValue(String v) {
        for (VehicleLoadingEnumeration c : VehicleLoadingEnumeration.values()) {
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
