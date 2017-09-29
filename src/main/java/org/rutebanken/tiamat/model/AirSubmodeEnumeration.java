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

public enum AirSubmodeEnumeration {

    UNKNOWN("unknown"),
    UNDEFINED("undefined"),
    INTERNATIONAL_FLIGHT("internationalFlight"),
    DOMESTIC_FLIGHT("domesticFlight"),
    INTERCONTINENTAL_FLIGHT("intercontinentalFlight"),
    DOMESTIC_SCHEDULED_FLIGHT("domesticScheduledFlight"),
    SHUTTLE_FLIGHT("shuttleFlight"),
    INTERCONTINENTAL_CHARTER_FLIGHT("intercontinentalCharterFlight"),
    INTERNATIONAL_CHARTER_FLIGHT("internationalCharterFlight"),
    ROUND_TRIP_CHARTER_FLIGHT("roundTripCharterFlight"),
    SIGHTSEEING_FLIGHT("sightseeingFlight"),
    HELICOPTER_SERVICE("helicopterService"),
    DOMESTIC_CHARTER_FLIGHT("domesticCharterFlight"),
    SCHENGEN_AREA_FLIGHT("SchengenAreaFlight"),
    AIRSHIP_SERVICE("airshipService"),
    SHORT_HAUL_INTERNATIONAL_FLIGHT("shortHaulInternationalFlight"),
    CANAL_BARGE("canalBarge");
    private final String value;

    AirSubmodeEnumeration(String v) {
        value = v;
    }

    public static AirSubmodeEnumeration fromValue(String v) {
        for (AirSubmodeEnumeration c : AirSubmodeEnumeration.values()) {
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
