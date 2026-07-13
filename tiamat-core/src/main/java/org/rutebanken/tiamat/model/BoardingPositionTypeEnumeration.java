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

public enum BoardingPositionTypeEnumeration {

    UNKNOWN("unknown"),
    DOOR_FROM_AIRLINE_GATE("doorFromAirlineGate"),
    POSITION_ON_RAIL_PLATFORM("positionOnRailPlatform"),
    POSITION_ON_METRO_PLATFORM("positionOnMetroPlatform"),
    POSITION_AT_COACH_STOP("positionAtCoachStop"),
    POSITION_AT_BUS_STOP("positionAtBusStop"),
    BOAT_GANGWAY("boatGangway"),
    FERRY_GANGWAY("ferryGangway"),
    TELECABINEPLATFORM("telecabineplatform"),
    SET_DOWN_POINT("setDownPoint"),
    TAXI_BAY("taxiBay"),
    VEHICLE_LOADING_RAMP("vehicleLoadingRamp"),
    OTHER("other");
    private final String value;

    BoardingPositionTypeEnumeration(String v) {
        value = v;
    }

    public static BoardingPositionTypeEnumeration fromValue(String v) {
        for (BoardingPositionTypeEnumeration c : BoardingPositionTypeEnumeration.values()) {
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
