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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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

    private Optional<String> getSubject() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken token
                && (token.getPrincipal() instanceof Jwt jwt)) {
            return Optional.of(jwt.getSubject());
        }
        return Optional.empty();
    }

    @Bean
    @Primary
    public UsernameFetcher usernameFetcher(UserInfoExtractor userInfoExtractor) {
        return new UsernameFetcher(userInfoExtractor) {
            @Override
            public String getUserNameForAuthenticatedUser() {
                return getSubject().orElse("<unknown user>");
            }
        };
    }
}
