/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.model;

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

    public static ParkingVehicleEnumeration fromValue(String v) {
        for (ParkingVehicleEnumeration c : ParkingVehicleEnumeration.values()) {
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
