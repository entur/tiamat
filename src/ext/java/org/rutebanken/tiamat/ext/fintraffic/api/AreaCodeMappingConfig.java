package org.rutebanken.tiamat.ext.fintraffic.api;

import jakarta.annotation.PostConstruct;
import org.rutebanken.tiamat.ext.fintraffic.FintrafficConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ConfigurationProperties(prefix = "ext.fintraffic")
public class AreaCodeMappingConfig {

    private static final Logger logger = LoggerFactory.getLogger(AreaCodeMappingConfig.class);

    private Map<String, String> areaCodes = new HashMap<>();

    // Spring Boot relaxed binding strips non-ASCII chars from @ConfigurationProperties map keys,
    // so Nordic TVV codes must use ASCII keys in properties with display-name overrides here.
    private Map<String, String> areaCodeDisplayNames = new HashMap<>();

    // Built at startup: "499" → {"LFT"}, "091" → {"HSL", "TFC"}, etc.
    private Map<String, Set<String>> reverseIndex = Collections.emptyMap();

    @PostConstruct
    void buildReverseIndex() {
        if (areaCodes.isEmpty()) {
            logger.warn("No ext.fintraffic.areaCodes mappings configured — all area code lookups will return empty results");
        }
        Map<String, Set<String>> index = new HashMap<>();
        areaCodes.forEach((tvvCode, codesStr) -> {
            String uppercaseTvvCode = areaCodeDisplayNames.getOrDefault(tvvCode, tvvCode.toUpperCase());
            if (!FintrafficConstants.isValidAreaCode(uppercaseTvvCode)) {
                throw new IllegalArgumentException(
                        "Invalid TVV area code key in ext.fintraffic.area-codes: '" + tvvCode
                                + "' (resolved to '" + uppercaseTvvCode + "')"
                );
            }
            for (String municipalityCode : codesStr.split(",")) {
                String trimmed = municipalityCode.trim();
                if (!trimmed.isEmpty()) {
                    if (!FintrafficConstants.isValidMunicipalityCode(trimmed)) {
                        throw new IllegalArgumentException(
                                "Invalid municipality code in ext.fintraffic.areaCodes." + tvvCode + ": '" + trimmed + "'"
                        );
                    }
                    index.computeIfAbsent(trimmed, k -> new HashSet<>()).add(uppercaseTvvCode);
                }
            }
        });
        // Make value sets immutable to prevent external modification
        index.replaceAll((k, v) -> Set.copyOf(v));
        this.reverseIndex = Collections.unmodifiableMap(index);
    }

    public Set<String> getAreaCodesForMunicipalityCode(String municipalityCode) {
        return reverseIndex.getOrDefault(municipalityCode, Set.of());
    }

    public void setAreaCodes(Map<String, String> areaCodes) {
        this.areaCodes = areaCodes;
    }

    public Map<String, String> getAreaCodes() {
        return areaCodes;
    }

    public void setAreaCodeDisplayNames(Map<String, String> areaCodeDisplayNames) {
        this.areaCodeDisplayNames = areaCodeDisplayNames;
    }

    public Map<String, String> getAreaCodeDisplayNames() {
        return areaCodeDisplayNames;
    }
}
