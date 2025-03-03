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

package org.rutebanken.tiamat.diff.generic;

/**
 * This class is used in roles and authentication.
 */


public enum StopPlaceTypeSubmodeEnumuration {
    ONSTREET_BUS("onstreetBus"),
    ONSTREET_TRAM("onstreetTram"),
    AIRPORT("airport"),
    RAIL_STATION("railStation"),
    METRO_STATION("metroStation"),
    BUS_STATION("busStation"),
    COACH_STATION("coachStation"),
    TRAM_STATION("tramStation"),
    HARBOUR_PORT("harbourPort"),
    FERRY_PORT("ferryPort"),
    FERRY_STOP("ferryStop"),
    LIFT_STATION("liftStation"),
    VEHICLE_RAIL_INTERCHANGE("vehicleRailInterchange"),
    OTHER("other"),
    RAIL_REPLACEMENT_BUS("railReplacementBus"),
    ALL("all");

    private final String value;

    StopPlaceTypeSubmodeEnumuration(String v) {
        value = v;
    }

    public static StopPlaceTypeSubmodeEnumuration fromValue(String value) {
        if(value.equals("*")) {
            return ALL;
        }
        for (StopPlaceTypeSubmodeEnumuration stopPlaceType : StopPlaceTypeSubmodeEnumuration.values()) {
            if (stopPlaceType.value.equals(value)) {
                return stopPlaceType;
            }
        }
        throw new IllegalArgumentException(value);
    }

    public String value() {
        return value;
    }
}
