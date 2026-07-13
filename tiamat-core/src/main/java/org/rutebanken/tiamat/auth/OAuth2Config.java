package org.rutebanken.tiamat.auth;

import org.entur.oauth2.JwtRoleAssignmentExtractor;
import org.entur.oauth2.user.DefaultJwtUserInfoExtractor;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.helper.organisation.user.UserInfoExtractor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2Config {

    @ConditionalOnProperty(
            value = "tiamat.security.role.assignment.extractor",
            havingValue = "jwt",
            matchIfMissing = true
    )
    @Bean
    public UserInfoExtractor jwtUserInfoExtractor() {
        return new DefaultJwtUserInfoExtractor();
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
}
