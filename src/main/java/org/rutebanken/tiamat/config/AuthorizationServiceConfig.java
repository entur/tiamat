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
 */

package org.rutebanken.tiamat.config;

import org.rutebanken.helper.organisation.DataScopedAuthorizationService;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.auth.DefaultAuthorizationService;
import org.rutebanken.tiamat.auth.TiamatEntityResolver;
import org.rutebanken.tiamat.auth.check.TiamatOriganisationChecker;
import org.rutebanken.tiamat.auth.check.TopographicPlaceChecker;
import org.rutebanken.tiamat.service.groupofstopplaces.GroupOfStopPlacesMembersResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SUBMODE;

@Configuration
public class AuthorizationServiceConfig {


    @Bean
    public AuthorizationService authorizationService(DataScopedAuthorizationService dataScopedAuthorizationService,
                                                     @Value("${authorization.enabled:true}") boolean authorizationEnabled,
                                                     RoleAssignmentExtractor roleAssignmentExtractor,
                                                     TopographicPlaceChecker topographicPlaceChecker,
                                                     GroupOfStopPlacesMembersResolver groupOfStopPlacesMembersResolver) {
        return new DefaultAuthorizationService(dataScopedAuthorizationService,
                authorizationEnabled,
                roleAssignmentExtractor,
                topographicPlaceChecker,
                groupOfStopPlacesMembersResolver);
    }

    @Bean
    public DataScopedAuthorizationService dataScopedAuthorizationService(RoleAssignmentExtractor roleAssignmentExtractor,
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
