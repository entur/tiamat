package org.rutebanken.tiamat.ext.fintraffic.auth;

import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
@Profile("fintraffic")
@EnableMethodSecurity(prePostEnabled = true)
public class FintrafficSecurityConfig {

    @Bean
    AuthorizationService authorizationService(
            WebClient.Builder webClientBuilder,
            @Value("${tiamat.ext.fintraffic.security.oidc-server-uri}") String oidcServerUri,
            @Value("${tiamat.ext.fintraffic.security.client-id}") String clientId,
            @Value("${tiamat.ext.fintraffic.security.client-secret}") String clientSecret,
            @Value("${tiamat.ext.fintraffic.security.enable-codespace-filtering:false}") boolean enableCodespaceFiltering,
            TopographicPlaceRepository topographicPlaceRepository
    ) {
        TrivoreAuthorizations trivoreAuthorizations = new TrivoreAuthorizations(prepareWebClient(webClientBuilder), oidcServerUri, clientId, clientSecret, enableCodespaceFiltering);
        return new FintrafficAuthorizationService(trivoreAuthorizations, topographicPlaceRepository);
    }

    private WebClient prepareWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .defaultHeader("User-Agent", "Entur Tiamat/" + LocalDate.now().format(DateTimeFormatter.ISO_DATE))
                .build();
    }
}