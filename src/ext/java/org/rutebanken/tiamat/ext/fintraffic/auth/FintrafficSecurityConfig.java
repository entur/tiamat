package org.rutebanken.tiamat.ext.fintraffic.auth;

import org.rutebanken.tiamat.auth.AuthorizationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@Profile("fintraffic")
@EnableMethodSecurity(prePostEnabled = true)
public class FintrafficSecurityConfig {

  @Bean
  AuthorizationService authorizationService(
    @Value("${tiamat.ext.fintraffic.security.oidc-server-uri}") String oidcServerUri,
    @Value("${tiamat.ext.fintraffic.security.client-id}") String clientId,
    @Value("${tiamat.ext.fintraffic.security.client-secret}") String clientSecret
  ) {
    TrivoreAuthorizations trivoreAuthorizations = new TrivoreAuthorizations(oidcServerUri, clientId, clientSecret);
    return new FintrafficAuthorizationService(trivoreAuthorizations);
  }
}