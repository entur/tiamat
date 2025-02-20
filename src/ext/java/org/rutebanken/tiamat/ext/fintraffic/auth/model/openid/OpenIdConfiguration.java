package org.rutebanken.tiamat.ext.fintraffic.auth.model.openid;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenIdConfiguration(@JsonProperty("userinfo_endpoint") String userInfoEndpoint) {

}
/*
@JsonCreator
  public EntraTokenResponse(
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("expires_in") long expiresIn,
    @JsonProperty("access_token") String accessToken
  ) {
    this.tokenType = tokenType;
    this.validUntil = LocalDateTime.now().plusSeconds(expiresIn);
    this.accessToken = accessToken;
  }
 */