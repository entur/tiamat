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

public enum WaterSubmodeEnumeration {

    UNKNOWN("unknown"),
    UNDEFINED("undefined"),
    INTERNATIONAL_CAR_FERRY("internationalCarFerry"),
    NATIONAL_CAR_FERRY("nationalCarFerry"),
    REGIONAL_CAR_FERRY("regionalCarFerry"),
    LOCAL_CAR_FERRY("localCarFerry"),
    INTERNATIONAL_PASSENGER_FERRY("internationalPassengerFerry"),
    NATIONAL_PASSENGER_FERRY("nationalPassengerFerry"),
    REGIONAL_PASSENGER_FERRY("regionalPassengerFerry"),
    LOCAL_PASSENGER_FERRY("localPassengerFerry"),
    POST_BOAT("postBoat"),
    TRAIN_FERRY("trainFerry"),
    ROAD_FERRY_LINK("roadFerryLink"),
    AIRPORT_BOAT_LINK("airportBoatLink"),
    HIGH_SPEED_VEHICLE_SERVICE("highSpeedVehicleService"),
    HIGH_SPEED_PASSENGER_SERVICE("highSpeedPassengerService"),
    SIGHTSEEING_SERVICE("sightseeingService"),
    SCHOOL_BOAT("schoolBoat"),
    CABLE_FERRY("cableFerry"),
    RIVER_BUS("riverBus"),
    SCHEDULED_FERRY("scheduledFerry"),
    SHUTTLE_FERRY_SERVICE("shuttleFerryService");
    private final String value;

    WaterSubmodeEnumeration(String v) {
        value = v;
    }

    public static WaterSubmodeEnumeration fromValue(String v) {
        for (WaterSubmodeEnumeration c : WaterSubmodeEnumeration.values()) {
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
