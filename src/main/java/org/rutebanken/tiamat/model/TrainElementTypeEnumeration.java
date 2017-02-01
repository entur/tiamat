package org.rutebanken.tiamat.model;

public enum TrainElementTypeEnumeration {

    BUFFET_CAR("buffetCar"),
    CARRIAGE("carriage"),
    ENGINE("engine"),
    CAR_TRANSPORTER("carTransporter"),
    SLEEPER_CARRIAGE("sleeperCarriage"),
    LUGGAGE_VAN("luggageVan"),
    RESTAURANT_CARRIAGE("restaurantCarriage"),
    OTHER("other");
    private final String value;

    TrainElementTypeEnumeration(String v) {
        value = v;
    }

    public static TrainElementTypeEnumeration fromValue(String v) {
        for (TrainElementTypeEnumeration c : TrainElementTypeEnumeration.values()) {
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
