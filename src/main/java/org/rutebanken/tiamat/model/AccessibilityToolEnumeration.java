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

public enum AccessibilityToolEnumeration {

    WHEELCHAIR("wheelchair"),
    WALKINGSTICK("walkingstick"),
    AUDIO_NAVIGATOR("audioNavigator"),
    VISUAL_NAVIGATOR("visualNavigator"),
    PASSENGER_CART("passengerCart"),
    PUSHCHAIR("pushchair"),
    UMBRELLA("umbrella"),
    BUGGY("buggy"),
    OTHER("other");
    private final String value;

    AccessibilityToolEnumeration(String v) {
        value = v;
    }

    public static AccessibilityToolEnumeration fromValue(String v) {
        for (AccessibilityToolEnumeration c : AccessibilityToolEnumeration.values()) {
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
