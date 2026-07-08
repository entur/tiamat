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

public enum ParkingTypeEnumeration {

    PARK_AND_RIDE("parkAndRide"),
    LIFT_SHARE_PARKING("liftShareParking"),
    URBAN_PARKING("urbanParking"),
    AIRPORT_PARKING("airportParking"),
    TRAIN_STATION_PARKING("trainStationParking"),
    EXHIBITION_CENTRE_PARKING("exhibitionCentreParking"),
    RENTAL_CAR_PARKING("rentalCarParking"),
    SHOPPING_CENTRE_PARKING("shoppingCentreParking"),
    MOTORWAY_PARKING("motorwayParking"),
    ROADSIDE("roadside"),
    PARKING_ZONE("parkingZone"),
    UNDEFINED("undefined"),
    CYCLE_RENTAL("cycleRental"),
    OTHER("other");
    private final String value;

    ParkingTypeEnumeration(String v) {
        value = v;
    }

    public static ParkingTypeEnumeration fromValue(String v) {
        for (ParkingTypeEnumeration c : ParkingTypeEnumeration.values()) {
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
