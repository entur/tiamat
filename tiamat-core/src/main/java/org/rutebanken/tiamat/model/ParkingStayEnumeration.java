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

public enum ParkingStayEnumeration {

    SHORT_STAY("shortStay"),
    MID_TERM("midTerm"),
    LONG_TERM("longTerm"),
    DROPOFF("dropoff"),
    UNLIMITED("unlimited"),
    OTHER("other"),
    ALL("all");
    private final String value;

    ParkingStayEnumeration(String v) {
        value = v;
    }

    public static ParkingStayEnumeration fromValue(String v) {
        for (ParkingStayEnumeration c : ParkingStayEnumeration.values()) {
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
