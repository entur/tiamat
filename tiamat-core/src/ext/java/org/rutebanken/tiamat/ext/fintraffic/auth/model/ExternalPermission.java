package org.rutebanken.tiamat.ext.fintraffic.auth.model;

import java.util.Map;

public record ExternalPermission(String id,
                                 String externalId,
                                 String permissionGroupId,
                                 String name,
                                 String description,
                                 Map<String, String> meta,
                                 Map<String, Object> customFields) {
}
