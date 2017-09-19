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

public enum CouchetteFacilityEnumeration {

    UNKNOWN("unknown"),
    T_2("T2"),
    T_3("T3"),
    C_1("C1"),
    C_2("C2"),
    C_4("C4"),

    C_5("C5"),
    C_6("C6"),
    WHEELCHAIR("wheelchair"),
    OTHER("other");
    private final String value;

    CouchetteFacilityEnumeration(String v) {
        value = v;
    }

    public static CouchetteFacilityEnumeration fromValue(String v) {
        for (CouchetteFacilityEnumeration c : CouchetteFacilityEnumeration.values()) {
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
