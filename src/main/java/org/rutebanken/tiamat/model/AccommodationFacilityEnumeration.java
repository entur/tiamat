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

public enum AccommodationFacilityEnumeration {

    UNKNOWN("unknown"),
    SEATING("seating"),
    SLEEPER("sleeper"),
    SINGLE_SLEEPER("singleSleeper"),
    DOUBLE_SLEEPER("doubleSleeper"),
    SPECIAL_SLEEPER("specialSleeper"),
    COUCHETTE("couchette"),
    SINGLE_COUCHETTE("singleCouchette"),
    DOUBLE_COUCHETTE("doubleCouchette"),
    SPECIAL_SEATING("specialSeating"),
    RECLINING_SEATS("recliningSeats"),
    BABY_COMPARTMENT("babyCompartment"),
    FAMILY_CARRIAGE("familyCarriage"),
    RECREATION_AREA("recreationArea"),
    PANORAMA_COACH("panoramaCoach"),
    PULLMAN_COACH("pullmanCoach"),
    STANDING("standing");
    private final String value;

    AccommodationFacilityEnumeration(String v) {
        value = v;
    }

    public static AccommodationFacilityEnumeration fromValue(String v) {
        for (AccommodationFacilityEnumeration c : AccommodationFacilityEnumeration.values()) {
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
