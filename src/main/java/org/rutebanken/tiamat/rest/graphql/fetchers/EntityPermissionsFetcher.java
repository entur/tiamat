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
import org.rutebanken.tiamat.rest.graphql.dataloader.GraphQLDataLoaderRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class EntityPermissionsFetcher implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(EntityPermissionsFetcher.class);

    @Autowired
    private GenericEntityInVersionRepository genericEntityInVersionRepository;

    @Autowired
    private TypeFromIdResolver typeFromIdResolver;


    @Autowired
    private AuthorizationService authorizationService;
    

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        String netexId = extractNetexId(environment.getSource());

        // Try to use DataLoader first (proper GraphQL way)
        org.dataloader.DataLoader<String, EntityPermissions> dataLoader = 
            environment.getDataLoader(GraphQLDataLoaderRegistryService.ENTITY_PERMISSIONS_LOADER);
        
        if (dataLoader != null) {
            logger.debug("Using DataLoader for entity permissions: {}", netexId);
            return dataLoader.load(netexId);
        }

        // Fallback: individual loading (existing logic)
        logger.debug("No DataLoader available, falling back to individual permissions loading for: {}", netexId);
        return loadPermissionsIndividually(netexId);
    }
    
    /**
     * Extract netex ID from the source object
     */
    private String extractNetexId(Object source) {
        if (source instanceof StopPlace stopPlace) {
            return stopPlace.getNetexId();
        } else if (source instanceof GroupOfStopPlaces groupOfStopPlaces) {
            return groupOfStopPlaces.getNetexId();
        } else {
            throw new IllegalArgumentException("Cannot find entity with unsupported source type: " + 
                (source != null ? source.getClass().getSimpleName() : "null"));
        }
    }
    
    
    /**
     * Load permissions individually (original implementation)
     */
    private EntityPermissions loadPermissionsIndividually(String netexId) throws Exception {
        logger.debug("Loading permissions individually for entity: {}", netexId);
        
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
