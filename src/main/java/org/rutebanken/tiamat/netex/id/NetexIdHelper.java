package org.rutebanken.tiamat.netex.id;

import org.apache.commons.lang.StringUtils;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Random;

public class NetexIdHelper {

    private static final Logger logger = LoggerFactory.getLogger(NetexIdMapper.class);

    // TODO: make it configurable, maybe in ValidPrefixList
    public static final String NSR = "NSR";

    public static String getNetexId(String type, String id) {
        return NSR + ":" + type + ":" + id;
    }

    /**
     * Creates random NSR-ID.
     * TODO: Move to test
     */
    public static String generateRandomizedNetexId(IdentifiedEntity identifiedEntity) {
        return getNetexId(determineIdType(identifiedEntity), String.valueOf(new Random().nextInt()));
    }

    public static boolean isNsrId(String netexId) {
        if(!netexId.contains(NSR)) {
            logger.warn("The netexId: {} does not start with {}", netexId, NSR);
            return false;
        }

        if(StringUtils.countMatches(netexId, ":") != 2) {
            logger.warn("Expected number of colons is two. {}", netexId);
            return false;
        }

        return true;
    }

    /**
     *
     * @param netexId Id with long value after last colon.
     * @return long value
     */
    public static long extractIdPostfix(String netexId) {
        try {
            return Long.valueOf(netexId.substring(netexId.lastIndexOf(':') + 1));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Cannot parse NeTEx ID into internal ID: '" + netexId +"'");
        }
    }

    public static String extractIdPrefix(String netexId) {
        if(StringUtils.countMatches(netexId, ":") != 2) {
            throw new IllegalArgumentException("Number of colons in ID is not two: " + netexId);
        }

        return netexId.substring(0, netexId.indexOf(':'));
    }

    public static Optional<String> getOptionalTiamatId(String netexId) {
        if (isNsrId(netexId)) {
            logger.debug("Detected tiamat ID from {}", netexId);
            return Optional.of(netexId);
        } else {
            return Optional.empty();
        }
    }

    private static String determineIdType(IdentifiedEntity identifiedEntity) {

        if(identifiedEntity instanceof StopPlace) {
            return "StopPlace";
        } else if (identifiedEntity instanceof Quay){
            return "Quay";
        } else if (identifiedEntity instanceof SiteFrame) {
            return "SiteFrame";
        } else if (identifiedEntity instanceof PathLinkEnd) {
            return "PathLinkEnd";
        } else {
            return identifiedEntity.getClass().getSimpleName();
        }
    }
}
