/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 */

package org.rutebanken.tiamat.config;


import org.entur.oauth2.JwtRoleAssignmentExtractor;
import org.entur.oauth2.RoRJwtDecoderBuilder;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtDecoder;


/**
 * Configure Spring Beans for OAuth2 resource server and OAuth2 client security.
 */
@Configuration
public class OAuth2Config {

    /**
     * Extract role assignments from a JWT token.
     *
     * @return JwtRoleAssignmentExtractor
     */
    @Bean
    public RoleAssignmentExtractor roleAssignmentExtractor() {
        return new JwtRoleAssignmentExtractor();
    }

    /**
     * Build a @{@link JwtDecoder} for RoR Auth0 domain.
     *
     * @return a @{@link JwtDecoder} for Auth0.
     */
    // TODO: replace with proper authentication
    //@Bean
    //@Profile("!test")
    public JwtDecoder rorAuth0JwtDecoder(/*OAuth2ResourceServerProperties properties,*/
                                                                                             @Value("${tiamat.oauth2.resourceserver.auth0.ror.jwt.audience}") String rorAuth0Audience,
                                         @Value("${tiamat.oauth2.resourceserver.auth0.ror.claim.namespace}") String rorAuth0ClaimNamespace) {

        //String rorAuth0Issuer = properties.getJwt().getIssuerUri();
        return new RoRJwtDecoderBuilder()//.withIssuer(rorAuth0Issuer)
                .withIssuer("http://localhost")
                .withAudience(rorAuth0Audience)
                .withAuth0ClaimNamespace(rorAuth0ClaimNamespace)
                .build();
    }
}

