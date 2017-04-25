package org.rutebanken.tiamat.netex.id;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ValidPrefixList {

    private static final Logger logger = LoggerFactory.getLogger(ValidPrefixList.class);

    private final List<String> validPrefixForClaiming;

    @Autowired
    public ValidPrefixList(@Value("${netex.id.valid.prefix.list:NSR,KVE,WOF}") String[] list) {
        this.validPrefixForClaiming = ImmutableList.copyOf(list);
        logger.info("Valid prefixes for claiming explicit IDs: {}", validPrefixForClaiming);
    }

    public List<String> get() {
        return validPrefixForClaiming;
    }
}
