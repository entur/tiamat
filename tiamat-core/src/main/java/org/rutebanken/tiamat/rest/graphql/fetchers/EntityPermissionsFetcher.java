package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.diff.generic.SubmodeEnumuration;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.authorization.EntityPermissions;
import org.rutebanken.tiamat.netex.id.TypeFromIdResolver;
import org.rutebanken.tiamat.repository.generic.GenericEntityInVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class EntityPermissionsFetcher implements DataFetcher {

    @Autowired
    private GenericEntityInVersionRepository genericEntityInVersionRepository;

    @Autowired
    private TypeFromIdResolver typeFromIdResolver;


    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        String netexId = null;
        if(environment.getSource() instanceof StopPlace stopPlace) {
            netexId= stopPlace.getNetexId();
        } else if (environment.getSource() instanceof GroupOfStopPlaces groupOfStopPlaces) {
            netexId = groupOfStopPlaces.getNetexId();
        } else {
            throw new IllegalArgumentException("Cannot find entity with ID: " + netexId);
        }


        Class clazz = typeFromIdResolver.resolveClassFromId(netexId);
        EntityInVersionStructure entityInVersionStructure = genericEntityInVersionRepository.findFirstByNetexIdOrderByVersionDesc(netexId, clazz);

        if (entityInVersionStructure == null) {
            throw new IllegalArgumentException("Cannot find entity with ID: " + netexId);
        }

        final boolean canEditEntities = authorizationService.canEditEntity(entityInVersionStructure);
        final boolean canDeleteEntity = authorizationService.canDeleteEntity(entityInVersionStructure);
        final Set<StopTypeEnumeration> allowedStopPlaceTypes = authorizationService.getAllowedStopPlaceTypes(entityInVersionStructure);
        final Set<StopTypeEnumeration> bannedStopPlaceTypes = authorizationService.getBannedStopPlaceTypes(entityInVersionStructure);
        final Set<SubmodeEnumuration> allowedSubmode = authorizationService.getAllowedSubmodes(entityInVersionStructure);
        final Set<SubmodeEnumuration> bannedSubmode = authorizationService.getBannedSubmodes(entityInVersionStructure);

        return new EntityPermissions.Builder()
                .canDelete(canDeleteEntity)
                .canEdit(canEditEntities)
                .allowedSubmodes(allowedSubmode)
                .bannedSubmodes(bannedSubmode)
                .allowedStopPlaceTypes(allowedStopPlaceTypes)
                .bannedStopPlaceTypes(bannedStopPlaceTypes)
                .build();
    }
}
