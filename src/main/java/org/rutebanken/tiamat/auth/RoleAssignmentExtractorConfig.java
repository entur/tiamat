package org.rutebanken.tiamat.auth;

import org.rutebanken.helper.organisation.KeycloakRoleAssignmentExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleAssignmentExtractorConfig {

    @Bean
    public KeycloakRoleAssignmentExtractor getKeycloakRoleAssignmentExtractor() {
        return new KeycloakRoleAssignmentExtractor();
    }
}
