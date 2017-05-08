package org.rutebanken.tiamat.netex.id;

import com.google.common.collect.ImmutableList;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ValidPrefixList {

    public static final String ANY_PREFIX = "*";

    private static final Logger logger = LoggerFactory.getLogger(ValidPrefixList.class);
    private final Map<String, List<String>> validPrefixesPerType;

    @Autowired
    public ValidPrefixList(@Value("#{${netex.id.valid.prefix.list:{TopographicPlace:{'KVE','WOF','OSM'},TariffZone:{'*'}}}}") Map<String, List<String>> validPrefixesPerType) {
        for(String type : validPrefixesPerType.keySet()) {
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

        if(prefix.equals(NetexIdHelper.NSR)) {
            return true;
        }

        List<String> validPrefixes = validPrefixesPerType.get(type);
        if(validPrefixes == null) {
            return false;
        }
        if(validPrefixes.contains(prefix)) {
            return true;
        }
        if(validPrefixes.contains(ANY_PREFIX)) {
            return true;
        }
        return false;
    }
}
