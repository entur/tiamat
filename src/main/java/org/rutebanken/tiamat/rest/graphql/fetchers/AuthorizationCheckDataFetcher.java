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

package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.authorization.AuthorizationResponse;
import org.rutebanken.tiamat.netex.id.TypeFromIdResolver;
import org.rutebanken.tiamat.repository.generic.GenericEntityInVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;

@Component
public class AuthorizationCheckDataFetcher implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationCheckDataFetcher.class);

    @Autowired
    private GenericEntityInVersionRepository genericEntityInVersionRepository;

    @Autowired
    private TypeFromIdResolver typeFromIdResolver;

    @Autowired
    private ReflectionAuthorizationService authorizationService;

    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
        String id = dataFetchingEnvironment.getArgument(ID);

        Class clazz = typeFromIdResolver.resolveClassFromId(id);
        EntityInVersionStructure entityInVersionStructure = genericEntityInVersionRepository.findFirstByNetexIdOrderByVersionDesc(id, clazz);

        if (entityInVersionStructure == null) {
            throw new IllegalArgumentException("Cannot find entity with ID: " + id);
        }

        Set<String> roles = authorizationService.getRelevantRolesForEntity(entityInVersionStructure);

        logger.debug("User is authorized with roles {} for entity {}", roles, id);

        return new AuthorizationResponse(id, roles);
    }
}
