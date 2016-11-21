

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


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

    public String value() {
        return value;
    }

    public static BusSubmodeEnumeration fromValue(String v) {
        for (BusSubmodeEnumeration c: BusSubmodeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
