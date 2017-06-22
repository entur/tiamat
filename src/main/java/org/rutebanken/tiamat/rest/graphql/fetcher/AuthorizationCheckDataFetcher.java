package org.rutebanken.tiamat.rest.graphql.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.authorization.AuthorizationResponse;
import org.rutebanken.tiamat.netex.id.TypeFromIdResolver;
import org.rutebanken.tiamat.repository.GenericEntityInVersionRepository;
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
    private AuthorizationService authorizationService;

    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) {
        String id = dataFetchingEnvironment.getArgument(ID);

        Class clazz = typeFromIdResolver.resolveClassFromId(id);
        EntityInVersionStructure entityInVersionStructure = genericEntityInVersionRepository.findFirstByNetexIdOrderByVersionDesc(id, clazz);

        if(entityInVersionStructure == null) {
            throw new IllegalArgumentException("Cannot find entity with ID: " + id);
        }

        Set<String> roles = authorizationService.getRelevantRolesForEntity(entityInVersionStructure);

        logger.debug("User is authorized with roles {} for entity {}", roles, id);

        return new AuthorizationResponse(id, roles);
    }
}
