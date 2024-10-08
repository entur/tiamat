package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.authorization.EntityPermissions;
import org.rutebanken.tiamat.netex.id.TypeFromIdResolver;
import org.rutebanken.tiamat.repository.generic.GenericEntityInVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EntityPermissionsFetcher implements DataFetcher {

    private static final String STOP_PLACE_TYPE = "StopPlaceType";
    private static final String SUBMODE = "Submode";
    @Autowired
    private GenericEntityInVersionRepository genericEntityInVersionRepository;

    @Autowired
    private TypeFromIdResolver typeFromIdResolver;


    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        final String netexId = ((StopPlace) environment.getSource()).getNetexId();

        Class clazz = typeFromIdResolver.resolveClassFromId(netexId);
        EntityInVersionStructure entityInVersionStructure = genericEntityInVersionRepository.findFirstByNetexIdOrderByVersionDesc(netexId, clazz);

        if (entityInVersionStructure == null) {
            throw new IllegalArgumentException("Cannot find entity with ID: " + netexId);
        }

        final boolean canEditEntities = authorizationService.canEditEntity(entityInVersionStructure);
        final boolean canDeleteEntity = authorizationService.canDeleteEntity(entityInVersionStructure);
        final Set<String> allowedStopPlaceTypes = authorizationService.getAllowedStopPlaceTypes(entityInVersionStructure);
        final Set<String> bannedStopPlaceTypes = authorizationService.getBannedStopPlaceTypes(entityInVersionStructure);
        final Set<String> allowedSubmode = authorizationService.getAllowedSubmodes(entityInVersionStructure);
        final Set<String> bannedSubmode = authorizationService.getBannedSubmodes(entityInVersionStructure);


        return new EntityPermissions(canEditEntities, canDeleteEntity, allowedStopPlaceTypes, bannedStopPlaceTypes, allowedSubmode, bannedSubmode);
    }
}