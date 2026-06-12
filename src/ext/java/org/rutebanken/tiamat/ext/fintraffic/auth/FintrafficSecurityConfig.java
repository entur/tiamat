package org.rutebanken.tiamat.ext.fintraffic.auth;

import org.rutebanken.helper.organisation.user.UserInfoExtractor;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
            @Value("${tiamat.ext.fintraffic.auth.codespace-authorization.enabled:true}") boolean codespaceAuthEnabled,
            @Value("${tiamat.ext.fintraffic.auth.municipality-authorization.enabled:false}") boolean municipalityAuthEnabled,
            @Value("${tiamat.ext.fintraffic.auth.multiModal.disabled:false}") boolean multiModalStopPlaceSupportDisabled,
            TopographicPlaceRepository topographicPlaceRepository
    ) {
        TrivoreAuthorizations trivoreAuthorizations = new TrivoreAuthorizations(prepareWebClient(webClientBuilder), oidcServerUri, clientId, clientSecret);
        return new FintrafficAuthorizationService(
                trivoreAuthorizations,
                topographicPlaceRepository,
                codespaceAuthEnabled,
                municipalityAuthEnabled,
                multiModalStopPlaceSupportDisabled
        );
    }

    private WebClient prepareWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .defaultHeader("User-Agent", "Entur Tiamat/" + LocalDate.now().format(DateTimeFormatter.ISO_DATE))
                .build();
    }

    @Bean
    @Primary
    public UsernameFetcher usernameFetcher(UserInfoExtractor userInfoExtractor) {
        return new UsernameFetcher(userInfoExtractor) {
            @Override
            public String getUserNameForAuthenticatedUser() {
                return TrivoreAuthorizations.getCurrentSubject();
            }
        };
    }
}
