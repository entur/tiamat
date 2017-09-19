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

public enum AllModesEnumeration {

    ALL("all"),
    UNKNOWN("unknown"),
    AIR("air"),
    BUS("bus"),
    TROLLEY_BUS("trolleyBus"),
    TRAM("tram"),
    COACH("coach"),
    RAIL("rail"),
    INTERCITY_RAIL("intercityRail"),
    URBAN_RAIL("urbanRail"),
    METRO("metro"),
    WATER("water"),
    CABLEWAY("cableway"),
    FUNICULAR("funicular"),
    TAXI("taxi"),

    SELF_DRIVE("selfDrive"),
    FOOT("foot"),
    BICYCLE("bicycle"),
    MOTORCYCLE("motorcycle"),
    CAR("car"),
    SHUTTLE("shuttle");
    private final String value;

    AllModesEnumeration(String v) {
        value = v;
    }

    public static AllModesEnumeration fromValue(String v) {
        for (AllModesEnumeration c : AllModesEnumeration.values()) {
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
