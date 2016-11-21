

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


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

    public String value() {
        return value;
    }

    public static TrainElementTypeEnumeration fromValue(String v) {
        for (TrainElementTypeEnumeration c: TrainElementTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
