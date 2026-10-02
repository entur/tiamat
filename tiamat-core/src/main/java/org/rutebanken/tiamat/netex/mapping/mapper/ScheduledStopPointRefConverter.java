package org.rutebanken.tiamat.netex.mapping.mapper;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Conversion between a fare zone member's ScheduledStopPointRef ({@code <prefix>:ScheduledStopPoint:S<n>})
 * and the StopPlace ref it stands for ({@code <prefix>:StopPlace:<n>}).
 * NeTEx forbids a StopPlaceRef inside Zone/members, so membership is carried as ScheduledStopPointRef.
 */
public final class ScheduledStopPointRefConverter {

    private ScheduledStopPointRefConverter() {
    }

    public static String toScheduledStopPointRef(String netexId) {
        if (netexId == null) {
            return null;
        }
        assertTwoColons(netexId);
        var idPrefix = netexId.substring(0, netexId.indexOf(':'));
        var id = netexId.substring(netexId.lastIndexOf(':') + 1).trim();
        return String.format("%s:ScheduledStopPoint:S%s", idPrefix, id);
    }

    public static String toStopPlaceRef(String netexId) {
        if (netexId == null) {
            return null;
        }
        assertTwoColons(netexId);
        var idParts = netexId.split(":");
        var refType = idParts[1];
        if (Objects.equals(refType, "StopPlace")) {
            return netexId;
        } else if (Objects.equals(refType, "ScheduledStopPoint")) {
            var id = idParts[2];
            if (id.startsWith("S")) {
                id = id.substring(1);
            }
            return String.format("%s:StopPlace:%s", idParts[0], id);
        }
        return null;
    }

    private static void assertTwoColons(String netexId) {
        if (StringUtils.countMatches(netexId, ":") != 2) {
            throw new IllegalArgumentException("Number of colons in ID is not two: " + netexId);
        }
    }
}
