package org.rutebanken.tiamat.auth;

import org.entur.oauth2.JwtRoleAssignmentExtractor;
import org.entur.oauth2.multiissuer.MultiIssuerAuthenticationManagerResolver;
import org.entur.oauth2.multiissuer.MultiIssuerAuthenticationManagerResolverBuilder;
import org.entur.oauth2.user.JwtUserInfoExtractor;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.helper.organisation.user.UserInfoExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class OAuth2Config {

    @Bean
    public UserInfoExtractor userInfoExtractor() {
        return new JwtUserInfoExtractor();
    }

    /**
     * Extract role assignments from a JWT token.
     *
     */
    @Bean
    public RoleAssignmentExtractor roleAssignmentExtractor() {
        return new JwtRoleAssignmentExtractor();
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

}
