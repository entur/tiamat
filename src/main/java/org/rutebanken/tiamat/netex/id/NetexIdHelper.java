package org.rutebanken.tiamat.netex.id;

import com.google.common.base.Strings;
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

    private static final Logger logger = LoggerFactory.getLogger(NetexIdHelper.class);

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
            logger.debug("The netexId: {} does not start with {}", netexId, NSR);
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
    public static long extractIdPostfixNumeric(String netexId) {
        try {
            return Long.valueOf(extractIdPostfix(netexId));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Cannot parse NeTEx ID postfix into numeric valueID: '" + netexId +"'");
        }
    }

    public static String extractIdPostfix(String netexId) {
        return netexId.substring(netexId.lastIndexOf(':') + 1).trim();
    }

    public static String extractIdType(String netexId) {
        try {
            return netexId.substring(netexId.indexOf(':') + 1, netexId.lastIndexOf(':'));
        } catch (StringIndexOutOfBoundsException e) {

            throw new StringIndexOutOfBoundsException("Cannot extract ID type for netexId: "+ netexId);

        }
    }

    public static String extractIdPrefix(String netexId) {
        if(StringUtils.countMatches(netexId, ":") != 2) {
            throw new IllegalArgumentException("Number of colons in ID is not two: " + netexId);
        }

        return netexId.substring(0, netexId.indexOf(':'));
    }

    public static String stripLeadingZeros(String originalIdValue) {
        try {
            long numeric = NetexIdHelper.extractIdPostfixNumeric(originalIdValue);
            String type = NetexIdHelper.extractIdType(originalIdValue);
            String prefix = NetexIdHelper.extractIdPrefix(originalIdValue);
            if(numeric == 0L || Strings.isNullOrEmpty(type) || Strings.isNullOrEmpty(prefix)) {
                logger.warn("Cannot parse original ID '{}' into preifx:type:number. Keeping value as is", originalIdValue);
            }

            logger.debug("Extracted prefix: {}, type: {} and numeric value: {}", prefix, type, numeric);
            return prefix +":"+type+":"+String.valueOf(numeric);

        } catch (IllegalArgumentException e) {
            logger.debug("Cannot strip leading zeros from numeric ID in {}. Returning value as is. Message: {}", originalIdValue, e.getMessage());
            return originalIdValue;
        }
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
