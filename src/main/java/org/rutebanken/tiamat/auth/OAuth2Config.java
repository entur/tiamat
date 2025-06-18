package org.rutebanken.tiamat.auth;

import org.entur.oauth2.AuthorizedWebClientBuilder;
import org.entur.oauth2.JwtRoleAssignmentExtractor;
import org.entur.oauth2.multiissuer.MultiIssuerAuthenticationManagerResolver;
import org.entur.oauth2.multiissuer.MultiIssuerAuthenticationManagerResolverBuilder;
import org.entur.oauth2.user.JwtUserInfoExtractor;
import org.entur.ror.permission.RemoteBabaRoleAssignmentExtractor;
import org.entur.ror.permission.RemoteBabaUserInfoExtractor;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.helper.organisation.user.UserInfoExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OAuth2Config {

    @ConditionalOnProperty(
            value = "tiamat.security.role.assignment.extractor",
            havingValue = "jwt",
            matchIfMissing = true
    )
    @Bean
    public UserInfoExtractor jwtUserInfoExtractor() {
        return new JwtUserInfoExtractor();
    }

    /**
     * Extract user info from the Baba user repository.
     *
     */
    @ConditionalOnProperty(
            value = "tiamat.security.role.assignment.extractor",
            havingValue = "baba"
    )
    @Bean
    public UserInfoExtractor babaUserInfoExtractor(
            WebClient webClient,
            @Value("${tiamat.user.permission.rest.service.url}") String url
    ) {
        return new RemoteBabaUserInfoExtractor(webClient, url);
    }

    /**
     * Extract role assignments from a JWT token.
     *
     */
    @ConditionalOnProperty(
            value = "tiamat.security.role.assignment.extractor",
            havingValue = "jwt",
            matchIfMissing = true
    )
    @Bean
    public RoleAssignmentExtractor jwtRoleAssignmentExtractor() {
        return new JwtRoleAssignmentExtractor();
    }

    /**
     * Extract role assignments from the Baba user repository.
     *
     */
    @ConditionalOnProperty(
            value = "tiamat.security.role.assignment.extractor",
            havingValue = "baba"
    )
    @Bean
    public RoleAssignmentExtractor babaRoleAssignmentExtractor(WebClient webClient ,
                                                               @Value("${tiamat.user.permission.rest.service.url}") String url) {
        return new RemoteBabaRoleAssignmentExtractor(webClient, url);
    }

    @Bean
    @Profile("!test")
    public MultiIssuerAuthenticationManagerResolver multiIssuerAuthenticationManagerResolver(
            @Value("${tiamat.oauth2.resourceserver.auth0.entur.internal.jwt.audience:}")
            String enturInternalAuth0Audience,
            @Value("${tiamat.oauth2.resourceserver.auth0.entur.internal.jwt.issuer-uri:}")
            String enturInternalAuth0Issuer,
            @Value("${tiamat.oauth2.resourceserver.auth0.entur.partner.jwt.audience:}")
            String enturPartnerAuth0Audience,
            @Value("${tiamat.oauth2.resourceserver.auth0.entur.partner.jwt.issuer-uri:}")
            String enturPartnerAuth0Issuer,
            @Value("${tiamat.oauth2.resourceserver.auth0.ror.jwt.audience:}")
            String rorAuth0Audience,
            @Value("${tiamat.oauth2.resourceserver.auth0.ror.jwt.issuer-uri:}")
            String rorAuth0Issuer,
            @Value("${tiamat.oauth2.resourceserver.auth0.ror.claim.namespace:}")
            String rorAuth0ClaimNamespace) {

        return new MultiIssuerAuthenticationManagerResolverBuilder()
                .withEnturInternalAuth0Issuer(enturInternalAuth0Issuer)
                .withEnturInternalAuth0Audience(enturInternalAuth0Audience)
                .withEnturPartnerAuth0Issuer(enturPartnerAuth0Issuer)
                .withEnturPartnerAuth0Audience(enturPartnerAuth0Audience)
                .withRorAuth0Issuer(rorAuth0Issuer)
                .withRorAuth0Audience(rorAuth0Audience)
                .withRorAuth0ClaimNamespace(rorAuth0ClaimNamespace)
                .build();
    }

    /**
     * Return a WebClient for authorized API calls.
     * The WebClient inserts a JWT bearer token in the Authorization HTTP header.
     * The JWT token is obtained from the configured Authorization Server.
     *
     * @param properties The spring.security.oauth2.client.registration.* properties
     * @param audience   The API audience, required for obtaining a token from Auth0
     * @return a WebClient for authorized API calls.
     */
    @Profile("!test")
    @Bean
    @ConditionalOnProperty(
            value = "tiamat.security.role.assignment.extractor",
            havingValue = "baba"
    )
    WebClient webClient(
            WebClient.Builder webClientBuilder,
            OAuth2ClientProperties properties,
            @Value("${tiamat.oauth2.client.audience}") String audience
    ) {
        return new AuthorizedWebClientBuilder(webClientBuilder)
                .withOAuth2ClientProperties(properties)
                .withAudience(audience)
                .withClientRegistrationId("internal")
                .build()
                .mutate()
                .defaultHeader("Et-Client-Name", "entur-tiamat")
                .build();
    }

}
