package org.rutebanken.tiamat.ext.fintraffic.auth.model.oidc;

public record Permission(String permissionId,
                         String permissionGroupId,
                         String grantedById,
                         String grantedByType,
                         String grantedTime,
                         String grantedToType,
                         String grantedToId) {
}
