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

public enum RailSubmodeEnumeration {

    UNKNOWN("unknown"),
    LOCAL("local"),

    HIGH_SPEED_RAIL("highSpeedRail"),

    SUBURBAN_RAILWAY("suburbanRailway"),

    REGIONAL_RAIL("regionalRail"),

    INTERREGIONAL_RAIL("interregionalRail"),

    LONG_DISTANCE("longDistance"),
    INTERMATIONAL("intermational"),
    SLEEPER_RAIL_SERVICE("sleeperRailService"),
    NIGHT_RAIL("nightRail"),

    CAR_TRANSPORT_RAIL_SERVICE("carTransportRailService"),

    TOURIST_RAILWAY("touristRailway"),
    RAIL_SHUTTLE("railShuttle"),
    REPLACEMENT_RAIL_SERVICE("replacementRailService"),
    SPECIAL_TRAIN("specialTrain"),
    CROSS_COUNTRY_RAIL("crossCountryRail"),

    RACK_AND_PINION_RAILWAY("rackAndPinionRailway");
    private final String value;

    RailSubmodeEnumeration(String v) {
        value = v;
    }

    public static RailSubmodeEnumeration fromValue(String v) {
        for (RailSubmodeEnumeration c : RailSubmodeEnumeration.values()) {
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
