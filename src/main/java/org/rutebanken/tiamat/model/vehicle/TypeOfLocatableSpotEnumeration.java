package org.rutebanken.tiamat.model.vehicle;

public enum TypeOfLocatableSpotEnumeration {
    SEAT("seat"),
    BED("bed"),
    STANDING_SPACE("standingSpace"),
    WHEELCHAIR_SPACE("wheelchairSpace"),
    PUSHCHAIR_SPACE("pushchairSpace"),
    LUGGAGE_SPACE("luggageSpace"),
    BICYCLE_SPACE("bicycleSpace"),
    VEHICLE_SPACE("vehicleSpace"),
    SPECIAL_SPACE("specialSpace"),
    OTHER("other");

    private final String value;

    private TypeOfLocatableSpotEnumeration(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static TypeOfLocatableSpotEnumeration fromValue(String v) {
        for (TypeOfLocatableSpotEnumeration c : values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}
