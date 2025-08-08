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

    public Set<String> getCodespaces() {
        if (customFields != null) {
            String codespaceField = customFields.getOrDefault(CUSTOM_FIELD_CODESPACE, "").toString();
            List<String> codespaces = Splitter.on(",")
                    .trimResults()
                    .omitEmptyStrings()
                    .splitToList(codespaceField);
            return new HashSet<>(codespaces);
        }
        return Set.of();
    }

}