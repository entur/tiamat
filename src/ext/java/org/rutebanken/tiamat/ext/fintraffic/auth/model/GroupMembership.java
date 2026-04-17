package org.rutebanken.tiamat.ext.fintraffic.auth.model;

import com.google.common.base.Splitter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record GroupMembership(String id,
                              String name,
                              String description,
                              boolean member,
                              String eligibleFrom,
                              String eligibleUntil,
                              Map<String, Object> customFields) {


    /**
     * Metadata field which contains list of NeTEx codespaces members of the group are allowed to access.
     */
    private static final String CUSTOM_FIELD_CODESPACE = "codespaces";

    /**
     * Metadata field which contains list of municipality codes members of the group are allowed to access.
     */
    private static final String CUSTOM_FIELD_MUNICIPALITY_CODES = "municipalityCodes";

    public Set<String> getCodespaces() {
        return splitCustomField(CUSTOM_FIELD_CODESPACE);
    }

    public Set<String> getMunicipalityCodes() {
        return splitCustomField(CUSTOM_FIELD_MUNICIPALITY_CODES);
    }

    private Set<String> splitCustomField(String fieldName) {
        if (customFields != null) {
            String fieldValue = customFields.getOrDefault(fieldName, "").toString();
            List<String> values = Splitter.on(",")
                    .trimResults()
                    .omitEmptyStrings()
                    .splitToList(fieldValue);
            return new HashSet<>(values);
        }
        return Set.of();
    }

}