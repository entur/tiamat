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

public enum BusSubmodeEnumeration {

    UNKNOWN("unknown"),
    UNDEFINED("undefined"),
    LOCAL_BUS("localBus"),
    REGIONAL_BUS("regionalBus"),
    EXPRESS_BUS("expressBus"),
    NIGHT_BUS("nightBus"),
    POST_BUS("postBus"),
    SPECIAL_NEEDS_BUS("specialNeedsBus"),
    MOBILITY_BUS("mobilityBus"),
    MOBILITY_BUS_FOR_REGISTERED_DISABLED("mobilityBusForRegisteredDisabled"),
    SIGHTSEEING_BUS("sightseeingBus"),
    SHUTTLE_BUS("shuttleBus"),
    HIGH_FREQUENCY_BUS("highFrequencyBus"),
    DEDICATED_LANE_BUS("dedicatedLaneBus"),
    SCHOOL_BUS("schoolBus"),
    SCHOOL_AND_PUBLIC_SERVICE_BUS("schoolAndPublicServiceBus"),
    RAIL_REPLACEMENT_BUS("railReplacementBus"),
    DEMAND_AND_RESPONSE_BUS("demandAndResponseBus"),
    AIRPORT_LINK_BUS("airportLinkBus");
    private final String value;

    BusSubmodeEnumeration(String v) {
        value = v;
    }

    public static BusSubmodeEnumeration fromValue(String v) {
        for (BusSubmodeEnumeration c : BusSubmodeEnumeration.values()) {
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
