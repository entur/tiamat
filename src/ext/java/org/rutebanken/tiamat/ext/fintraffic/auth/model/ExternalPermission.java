package org.rutebanken.tiamat.ext.fintraffic.auth.model;

import java.util.Map;

public record ExternalPermission(String id,
                                 Map<String,String> meta,
                                 String permissionGroupId,
                                 String name,
                                 String description) {
}