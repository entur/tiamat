package org.rutebanken.tiamat.model;

public enum SelfDriveSubmodeEnumeration {

    UNKNOWN("unknown"),
    UNDEFINED("undefined"),
    HIRE_CAR("hireCar"),
    HIRE_VAN("hireVan"),
    HIRE_MOTORBIKE("hireMotorbike"),
    HIRE_CYCLE("hireCycle"),
    ALL_HIRE_VEHICLES("allHireVehicles");
    private final String value;

    SelfDriveSubmodeEnumeration(String v) {
        value = v;
    }

    public static SelfDriveSubmodeEnumeration fromValue(String v) {
        for (SelfDriveSubmodeEnumeration c : SelfDriveSubmodeEnumeration.values()) {
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
