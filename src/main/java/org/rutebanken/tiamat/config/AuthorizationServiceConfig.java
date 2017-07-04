package org.rutebanken.tiamat.config;

import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.tiamat.auth.check.TiamatOriganisationChecker;
import org.rutebanken.tiamat.auth.TiamatEntityResolver;
import org.rutebanken.tiamat.auth.check.TopographicPlaceChecker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthorizationServiceConfig {


    @Bean
    public ReflectionAuthorizationService getAuthorizationService(RoleAssignmentExtractor roleAssignmentExtractor,
                                                                  @Value("${authorization.enabled:true}") boolean authorizationEnabled,
                                                                  TiamatOriganisationChecker tiamatOriganisationChecker,
                                                                  TopographicPlaceChecker topographicPlaceChecker,
                                                                  TiamatEntityResolver tiamatEntityResolver) {
        return new ReflectionAuthorizationService(roleAssignmentExtractor, authorizationEnabled, tiamatOriganisationChecker, topographicPlaceChecker, tiamatEntityResolver);
    }

}
