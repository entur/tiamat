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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ValidPrefixList {

    public static final String ANY_PREFIX = "*";

    @Value("${netex.validPrefix}")
    public static final String VALID_NETEX_PREFIX = "NSR";


    private static final Logger logger = LoggerFactory.getLogger(ValidPrefixList.class);
    private final Map<String, List<String>> validPrefixesPerType;

    @Autowired
    public ValidPrefixList(@Value("#{${netex.id.valid.prefix.list:{TopographicPlace:{'KVE','WOF','OSM'},TariffZone:{'*'}}}}") Map<String, List<String>> validPrefixesPerType) {
        for (String type : validPrefixesPerType.keySet()) {
            List<String> validPrefixesForType = validPrefixesPerType.get(type);
            logger.info("Loaded valid prefixes for {}: {} ", type, validPrefixesForType);
        }

        this.validPrefixesPerType = validPrefixesPerType;
    }

    public List<String> get(Class clazz) {
        logger.trace("Looking for valid prefixes for type: {}", clazz);
        return validPrefixesPerType.get(clazz.getSimpleName());
    }

    public boolean isValidPrefixForType(String prefix, Class clazz) {
        return isValidPrefixForType(prefix, clazz.getSimpleName());
    }

    public boolean isValidPrefixForType(String prefix, String type) {

        if (prefix.equals(VALID_NETEX_PREFIX)) {
            return true;
        }

        List<String> validPrefixes = validPrefixesPerType.get(type);
        if (validPrefixes == null) {
            return false;
        }
        if (validPrefixes.contains(prefix)) {
            return true;
        }
        if (validPrefixes.contains(ANY_PREFIX)) {
            return true;
        }
        return false;
    }
}
