package org.rutebanken.tiamat.ext.fintraffic.auth.model;

import java.util.Map;

public record ExternalPermissionGrant(String permissionId,
                                      String permissionGroupId,
                                      String permissionExternalId,
                                      String from, // ISO8601,
                                      String until, // ISO8601,
                                      String grantedById,
                                      String grantedByType,
                                      String grantedTime, // ISO8601
                                      String grantedTotype,
                                      String grantedToId,
                                      Map<String,Object> customFields) {
}
