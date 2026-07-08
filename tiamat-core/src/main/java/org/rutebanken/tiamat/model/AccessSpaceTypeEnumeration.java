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

public enum AccessSpaceTypeEnumeration {

    CONCOURSE("concourse"),
    BOOKING_HALL("bookingHall"),
    FORECOURT("forecourt"),
    UNDERPASS("underpass"),
    OVERPASS("overpass"),
    PASSAGE("passage"),
    PASSAGE_SECTION("passageSection"),
    LIFT("lift"),
    GALLERY("gallery"),
    GARAGE("garage"),
    SHOP("shop"),
    WAITING_ROOM("waitingRoom"),
    RESTAURANT("restaurant"),
    OTHER("other"),
    STAIRCASE("staircase"),
    WC("wc");
    private final String value;

    AccessSpaceTypeEnumeration(String v) {
        value = v;
    }

    public static AccessSpaceTypeEnumeration fromValue(String v) {
        for (AccessSpaceTypeEnumeration c : AccessSpaceTypeEnumeration.values()) {
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
