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

public enum ReservationEnumeration {

    RESERVATIONS_COMPULSORY("reservationsCompulsory"),
    RESERVATIONS_COMPULSORY_FOR_GROUPS("reservationsCompulsoryForGroups"),
    RESERVATIONS_COMPULSORY_FOR_FIRST_CLASS("reservationsCompulsoryForFirstClass"),
    RESERVATIONS_COMPULSORY_FROM_ORIGIN_STATION("reservationsCompulsoryFromOriginStation"),
    RESERVATIONS_RECOMMENDED("reservationsRecommended"),
    RESERVATIONS_POSSIBLE("reservationsPossible"),
    RESERVATIONS_POSSIBLE_ONLY_IN_FIRST_CLASS("reservationsPossibleOnlyInFirstClass"),
    RESERVATIONS_POSSIBLE_ONLY_IN_SECOND_CLASS("reservationsPossibleOnlyInSecondClass"),
    RESERVATIONS_POSSIBLE_FOR_CERTAIN_CLASSES("reservationsPossibleForCertainClasses"),
    GROUP_BOOKING_RESTRICTED("groupBookingRestricted"),
    NO_GROUPS_ALLOWED("noGroupsAllowed"),
    NO_RESERVATIONS_POSSIBLE("noReservationsPossible"),
    WHEELCHAIR_ONLY_RESERVATIONS("wheelchairOnlyReservations"),
    BICYCLE_RESERVATIONS_COMPULSORY("bicycleReservationsCompulsory"),
    RESERVATIONS_SUPPLEMENT_CHARGED("reservationsSupplementCharged"),
    UNKNOWN("unknown");
    private final String value;

    ReservationEnumeration(String v) {
        value = v;
    }

    public static ReservationEnumeration fromValue(String v) {
        for (ReservationEnumeration c : ReservationEnumeration.values()) {
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
