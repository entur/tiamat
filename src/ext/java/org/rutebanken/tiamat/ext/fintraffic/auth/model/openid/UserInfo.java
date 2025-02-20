package org.rutebanken.tiamat.ext.fintraffic.auth.model.openid;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rutebanken.tiamat.ext.fintraffic.auth.model.oidc.ExternalPermissions;

public record UserInfo(@JsonProperty("https://oneportal.trivore.com/claims/active_external_permissions") ExternalPermissions externalPermissions) {
}
