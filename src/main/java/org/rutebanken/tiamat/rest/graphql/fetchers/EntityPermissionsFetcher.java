package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.diff.generic.StopPlaceTypeSubmodeEnumuration;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.StopPlace;
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
        final Set<StopPlaceTypeSubmodeEnumuration> allowedStopPlaceTypes = authorizationService.getAllowedStopPlaceTypes(entityInVersionStructure);
        final Set<StopPlaceTypeSubmodeEnumuration> bannedStopPlaceTypes = authorizationService.getBannedStopPlaceTypes(entityInVersionStructure);
        final Set<StopPlaceTypeSubmodeEnumuration> allowedSubmode = authorizationService.getAllowedSubmodes(entityInVersionStructure);
        final Set<StopPlaceTypeSubmodeEnumuration> bannedSubmode = authorizationService.getBannedSubmodes(entityInVersionStructure);

        Set<StopPlaceTypeSubmodeEnumuration> allowedStopPlaceTypeCopy = new HashSet<>(allowedStopPlaceTypes);
        Set<StopPlaceTypeSubmodeEnumuration> bannedStopPlaceTypeCopy = new HashSet<>(bannedStopPlaceTypes);
        Set<StopPlaceTypeSubmodeEnumuration> allowedSubmodeCopy = new HashSet<>(allowedSubmode);
        Set<StopPlaceTypeSubmodeEnumuration> bannedSubmodeCopy = new HashSet<>(bannedSubmode);

        Set<StopPlaceTypeSubmodeEnumuration> duplicateStopPlaceTypes = new HashSet<>(allowedStopPlaceTypes);
        duplicateStopPlaceTypes.retainAll(bannedStopPlaceTypes);
        Set<StopPlaceTypeSubmodeEnumuration> duplicateSubmodes = new HashSet<>(allowedSubmode);
        duplicateSubmodes.retainAll(bannedSubmode);

        allowedStopPlaceTypeCopy.removeAll(duplicateStopPlaceTypes);
        bannedStopPlaceTypeCopy.removeAll(duplicateStopPlaceTypes);
        allowedSubmodeCopy.removeAll(duplicateSubmodes);
        bannedSubmodeCopy.removeAll(duplicateSubmodes);

        if(allowedStopPlaceTypeCopy.isEmpty() && !bannedStopPlaceTypeCopy.contains(StopPlaceTypeSubmodeEnumuration.ALL)) {
                allowedStopPlaceTypeCopy.add(StopPlaceTypeSubmodeEnumuration.ALL);

        }
        if(allowedSubmodeCopy.isEmpty() && !bannedSubmodeCopy.contains(StopPlaceTypeSubmodeEnumuration.ALL)) {
                allowedSubmodeCopy.add(StopPlaceTypeSubmodeEnumuration.ALL);
        }


        return new EntityPermissions(canEditEntities, canDeleteEntity, allowedStopPlaceTypeCopy, bannedStopPlaceTypeCopy, allowedSubmodeCopy, bannedSubmodeCopy);

    }
}
