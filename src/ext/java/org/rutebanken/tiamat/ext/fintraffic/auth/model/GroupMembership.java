package org.rutebanken.tiamat.ext.fintraffic.auth.model;

import org.rutebanken.tiamat.ext.fintraffic.FintrafficConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record GroupMembership(String id,
                              String name,
                              String description,
                              boolean member,
                              String eligibleFrom,
                              String eligibleUntil,
                              Map<String, Object> customFields) {

    private static final Logger logger = LoggerFactory.getLogger(GroupMembership.class);

    /**
     * Metadata field which contains list of NeTEx codespaces members of the group are allowed to access.
     */
    private static final String CUSTOM_FIELD_CODESPACE = "codespaces";

    /**
     * Metadata field which contains list of municipality codes members of the group are allowed to access.
     */
    private static final String CUSTOM_FIELD_MUNICIPALITY_CODES = "municipalityCodes";

    public Set<String> getCodespaces() {
        return validateValues(splitCustomField(CUSTOM_FIELD_CODESPACE), CUSTOM_FIELD_CODESPACE, FintrafficConstants::isValidAreaCode)
                .collect(Collectors.toSet());
    }

    public Set<String> getMunicipalityCodes() {
        return validateValues(splitCustomField(CUSTOM_FIELD_MUNICIPALITY_CODES), CUSTOM_FIELD_MUNICIPALITY_CODES, FintrafficConstants::isValidMunicipalityCode)
                .collect(Collectors.toSet());
    }

    private Stream<String> splitCustomField(String fieldName) {
        if (customFields == null) {
            return Stream.empty();
        }
        String fieldValue = customFields.getOrDefault(fieldName, "").toString();
        return Arrays.stream(fieldValue.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty());
    }

    private Stream<String> validateValues(Stream<String> values, String fieldName, Predicate<String> validator) {
        return values.filter(value -> {
            if (validator.test(value)) {
                return true;
            }
            logger.warn("Skipping invalid value '{}' in field '{}' of group [id={}, name={}]",
                    value, fieldName, id, name);
            return false;
        });
    }

}