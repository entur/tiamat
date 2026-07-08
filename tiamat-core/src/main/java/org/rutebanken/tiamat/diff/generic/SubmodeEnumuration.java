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


public enum SubmodeEnumuration {
    RAIL_REPLACEMENT_BUS("railReplacementBus");
    private final String value;

    SubmodeEnumuration(String v) {
        value = v;
    }

    public static SubmodeEnumuration fromValue(String value) {
        for (SubmodeEnumuration stopPlaceType : SubmodeEnumuration.values()) {
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
