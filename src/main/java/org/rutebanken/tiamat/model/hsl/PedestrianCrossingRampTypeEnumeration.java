package org.rutebanken.tiamat.model.hsl;

public enum PedestrianCrossingRampTypeEnumeration {
    LR("LR"), // Ramp curb, Luiskattu reunatukiosuus
    RK4("RK4"), // Vertical curb, Pystysuora reunatukiosuus
    RK4_LR("RK4_LR"),
    OTHER("other");
    private final String value;

    PedestrianCrossingRampTypeEnumeration(String v) {
        value = v;
    }

    public static PedestrianCrossingRampTypeEnumeration fromValue(String value) {
        for (PedestrianCrossingRampTypeEnumeration enumeration : PedestrianCrossingRampTypeEnumeration.values()) {
            if (enumeration.value.equals(value)) {
                return enumeration;
            }
        }
        throw new IllegalArgumentException(value);
    }

    public String value() {
        return value;
    }

}
