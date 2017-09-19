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

public enum QuayTypeEnumeration {

    AIRLINE_GATE("airlineGate"),
    RAIL_PLATFORM("railPlatform"),
    METRO_PLATFORM("metroPlatform"),
    COACH_STOP("coachStop"),
    BUS_STOP("busStop"),
    BUS_BAY("busBay"),
    TRAM_PLATFORM("tramPlatform"),
    TRAM_STOP("tramStop"),
    BOAT_QUAY("boatQuay"),
    FERRY_LANDING("ferryLanding"),
    TELECABINE_PLATFORM("telecabinePlatform"),
    TAXI_STAND("taxiStand"),
    SET_DOWN_PLACE("setDownPlace"),
    VEHICLE_LOADING_PLACE("vehicleLoadingPlace"),
    OTHER("other");
    private final String value;

    QuayTypeEnumeration(String v) {
        value = v;
    }

    public static QuayTypeEnumeration fromValue(String v) {
        for (QuayTypeEnumeration c : QuayTypeEnumeration.values()) {
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
