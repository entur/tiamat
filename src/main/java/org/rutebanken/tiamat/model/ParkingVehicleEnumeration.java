

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum ParkingVehicleEnumeration {

    PEDAL_CYCLE("pedalCycle"),
    MOPED("moped"),
    MOTORCYCLE("motorcycle"),
    MOTORCYCLE_WITH_SIDECAR("motorcycleWithSidecar"),
    MOTOR_SCOOTER("motorScooter"),
    TWO_WHEELED_VEHICLE("twoWheeledVehicle"),
    THREE_WHEELED_VEHICLE("threeWheeledVehicle"),
    CAR("car"),
    SMALL_CAR("smallCar"),
    PASSENGER_CAR("passengerCar"),
    LARGE_CAR("largeCar"),
    FOUR_WHEEL_DRIVE("fourWheelDrive"),
    TAXI("taxi"),
    CAMPER_CAR("camperCar"),
    CAR_WITH_TRAILER("carWithTrailer"),
    CAR_WITH_CARAVAN("carWithCaravan"),
    MINIBUS("minibus"),
    BUS("bus"),
    VAN("van"),
    LARGE_VAN("largeVan"),
    HIGH_SIDED_VEHICLE("highSidedVehicle"),
    LIGHT_GOODS_VEHICLE("lightGoodsVehicle"),
    HEAVY_GOODS_VEHICLE("heavyGoodsVehicle"),
    TRUCK("truck"),
    AGRICULTURAL_VEHICLE("agriculturalVehicle"),
    TANKER("tanker"),
    TRAM("tram"),
    ARTICULATED_VEHICLE("articulatedVehicle"),
    VEHICLE_WITH_TRAILER("vehicleWithTrailer"),
    LIGHT_GOODS_VEHICLE_WITH_TRAILER("lightGoodsVehicleWithTrailer"),
    HEAVY_GOODS_VEHICLE_WITH_TRAILER("heavyGoodsVehicleWithTrailer"),
    UNDEFINED("undefined"),
    OTHER("other"),
    ALL_PASSENGER_VEHICLES("allPassengerVehicles"),
    ALL("all");
    private final String value;

    ParkingVehicleEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ParkingVehicleEnumeration fromValue(String v) {
        for (ParkingVehicleEnumeration c: ParkingVehicleEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
