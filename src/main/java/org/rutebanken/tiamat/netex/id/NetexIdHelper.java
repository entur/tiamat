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

package org.rutebanken.tiamat.netex.id;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class NetexIdHelper {

    private static final Logger logger = LoggerFactory.getLogger(NetexIdHelper.class);

    private static Pattern NETEX_ID_PATTERN = Pattern.compile("\\w{3}:\\w{3,}:\\w+");

    private final ValidPrefixList validPrefixList;

    @Autowired
    public NetexIdHelper(ValidPrefixList validPrefixList) {
        this.validPrefixList = validPrefixList;
    }

    public String getNetexId(String type, long id) {
        return validPrefixList.getValidNetexPrefix() + ":" + type + ":" + id;
    }

    public String getNetexId(IdentifiedEntity identifiedEntity, long id) {
        String type = determineIdType(identifiedEntity);
        return getNetexId(type, id);
    }

    public boolean isNsrId(String netexId) {
        if(!netexId.contains(validPrefixList.getValidNetexPrefix())) {
            logger.debug("The netexId: {} does not start with {}", netexId, validPrefixList.getValidNetexPrefix());
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
    public long extractIdPostfixNumeric(String netexId) {
        try {
            return Long.valueOf(extractIdPostfix(netexId));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Cannot parse NeTEx ID postfix into numeric valueID: '" + netexId +"'");
        }
    }

    public String extractIdPostfix(String netexId) {
        return netexId.substring(netexId.lastIndexOf(':') + 1).trim();
    }

    public String extractIdType(String netexId) {
        try {
            return netexId.substring(netexId.indexOf(':') + 1, netexId.lastIndexOf(':'));
        } catch (StringIndexOutOfBoundsException e) {

            throw new StringIndexOutOfBoundsException("Cannot extract ID type for netexId: "+ netexId);

        }
    }

    public String extractIdPrefix(String netexId) {
        if(StringUtils.countMatches(netexId, ":") != 2) {
            throw new IllegalArgumentException("Number of colons in ID is not two: " + netexId);
        }

        return netexId.substring(0, netexId.indexOf(':'));
    }

    public String stripLeadingZeros(String originalIdValue) {
        try {
            long numeric = extractIdPostfixNumeric(originalIdValue);
            String type = extractIdType(originalIdValue);
            String prefix = extractIdPrefix(originalIdValue);
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

    public static boolean isNetexId(String string) {
        return NETEX_ID_PATTERN.matcher(string).matches();
    }

    public static String determineIdType(IdentifiedEntity identifiedEntity) {

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
