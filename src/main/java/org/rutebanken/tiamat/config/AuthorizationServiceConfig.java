package org.rutebanken.tiamat.config;

import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.tiamat.auth.check.TiamatOriganisationChecker;
import org.rutebanken.tiamat.auth.TiamatEntityResolver;
import org.rutebanken.tiamat.auth.check.TopographicPlaceChecker;
import org.rutebanken.tiamat.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SUBMODE;

@Configuration
public class AuthorizationServiceConfig {


    @Bean
    public ReflectionAuthorizationService getAuthorizationService(RoleAssignmentExtractor roleAssignmentExtractor,
                                                                  @Value("${authorization.enabled:true}") boolean authorizationEnabled,
                                                                  TiamatOriganisationChecker tiamatOriganisationChecker,
                                                                  TopographicPlaceChecker topographicPlaceChecker,
                                                                  TiamatEntityResolver tiamatEntityResolver) {

        // Should be made configurable
        Map<String, List<String>> fieldMappings = new HashMap<>();
        fieldMappings.put(SUBMODE.toLowerCase(), Arrays.asList("airSubmode", "busSubmode", "coachSubmode", "funicularSubmode", "metroSubmode", "tramSubmode", "telecabinSubmode", "railSubmode", "waterSubmode"));


        return new ReflectionAuthorizationService(roleAssignmentExtractor, authorizationEnabled, tiamatOriganisationChecker, topographicPlaceChecker, tiamatEntityResolver, fieldMappings);
    }

}
