package org.rutebanken.tiamat.ext.fintraffic.auth.model.oidc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ExternalPermissions(@JsonProperty("active") List<Permission> active) {
}
