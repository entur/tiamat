package org.rutebanken.tiamat.ext.fintraffic.auth.model.oidc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record GroupMembership(String id,
                              String name,
                              String description,
                              boolean member,
                              String eligibleFrom,
                              String eligibleUntil,
                              @JsonProperty("custom_fields") Map<String, Object> customFields) {
}