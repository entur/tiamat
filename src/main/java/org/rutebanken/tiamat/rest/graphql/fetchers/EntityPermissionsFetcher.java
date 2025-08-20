package org.rutebanken.tiamat.rest.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.rest.graphql.dataloader.EntityPermissionsDataLoader;
import org.rutebanken.tiamat.diff.generic.SubmodeEnumuration;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.authorization.EntityPermissions;

import java.util.Map;
import org.rutebanken.tiamat.netex.id.TypeFromIdResolver;
import org.rutebanken.tiamat.repository.generic.GenericEntityInVersionRepository;
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
    
    @Autowired
    private EntityPermissionsDataLoader entityPermissionsDataLoader;

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        String netexId = extractNetexId(environment.getSource());

        // Try to get from preloaded cache first (for bbox queries)
        EntityPermissions preloadedPermissions = getPreloadedPermissions(environment, netexId);
        if (preloadedPermissions != null) {
            return preloadedPermissions;
        }

        // Fall back to individual loading (existing logic)
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
     * Get preloaded permissions from GraphQL context if available
     */
    @SuppressWarnings("unchecked")
    private EntityPermissions getPreloadedPermissions(DataFetchingEnvironment environment, String netexId) {
        try {
            Map<String, EntityPermissions> preloadedPermissions = 
                environment.getGraphQlContext().get("preloadedPermissions");
            
            if (preloadedPermissions != null) {
                EntityPermissions permissions = preloadedPermissions.get(netexId);
                if (permissions != null) {
                    // Using debug level to avoid spamming logs, but this shows the cache hit
                    if (logger.isDebugEnabled()) {
                        logger.debug("Using preloaded permissions for entity: {}", netexId);
                    }
                    return permissions;
                }
            }
        } catch (Exception e) {
            logger.warn("Error accessing preloaded permissions for entity {}, falling back to individual loading", netexId, e);
        }
        return null;
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
