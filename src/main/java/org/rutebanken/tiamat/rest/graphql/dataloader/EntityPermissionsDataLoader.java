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

package org.rutebanken.tiamat.rest.graphql.dataloader;

import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.diff.generic.SubmodeEnumuration;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.authorization.EntityPermissions;
import org.rutebanken.tiamat.netex.id.TypeFromIdResolver;
import org.rutebanken.tiamat.repository.generic.GenericEntityInVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for batching entity permissions lookups to solve N+1 query problem
 */
@Component
public class EntityPermissionsDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(EntityPermissionsDataLoader.class);

    private final GenericEntityInVersionRepository genericEntityInVersionRepository;
    private final TypeFromIdResolver typeFromIdResolver;
    private final AuthorizationService authorizationService;

    public EntityPermissionsDataLoader(
            GenericEntityInVersionRepository genericEntityInVersionRepository,
            TypeFromIdResolver typeFromIdResolver,
            AuthorizationService authorizationService) {
        this.genericEntityInVersionRepository = genericEntityInVersionRepository;
        this.typeFromIdResolver = typeFromIdResolver;
        this.authorizationService = authorizationService;
    }

    /**
     * Batch load entity permissions for multiple netex IDs
     * 
     * @param netexIds List of netex IDs to load permissions for
     * @return Map of netex ID to EntityPermissions
     */
    public Map<String, EntityPermissions> batchLoadEntityPermissions(List<String> netexIds) {
        if (netexIds == null || netexIds.isEmpty()) {
            return Map.of();
        }

        logger.debug("Batch loading entity permissions for {} entities", netexIds.size());
        
        Map<String, EntityPermissions> resultMap = new HashMap<>();
        
        // Group netex IDs by entity type for more efficient batch loading
        Map<Class<?>, List<String>> idsByType = netexIds.stream()
            .collect(Collectors.groupingBy(netexId -> {
                try {
                    return typeFromIdResolver.resolveClassFromId(netexId);
                } catch (Exception e) {
                    logger.warn("Could not resolve type for netex ID: {}", netexId, e);
                    return Object.class; // Fallback type
                }
            }));

        // Use the new batch loading method
        try {
            Map<String, EntityInVersionStructure> entities = 
                genericEntityInVersionRepository.findLatestVersionByNetexIdsGrouped(idsByType);
            
            // Create permissions for each entity
            for (Map.Entry<String, EntityInVersionStructure> entry : entities.entrySet()) {
                String netexId = entry.getKey();
                EntityInVersionStructure entity = entry.getValue();
                EntityPermissions permissions = createEntityPermissions(entity);
                resultMap.put(netexId, permissions);
            }
            
            // Handle any missing entities
            Set<String> foundIds = entities.keySet();
            for (String netexId : netexIds) {
                if (!foundIds.contains(netexId)) {
                    logger.warn("Entity not found for netex ID: {}", netexId);
                    // Could create a default "no permissions" entity here if needed
                }
            }
            
        } catch (Exception e) {
            logger.error("Error batch loading entities, falling back to individual loading", e);
            
            // Fallback to individual loading
            for (String netexId : netexIds) {
                try {
                    Class<?> entityType = typeFromIdResolver.resolveClassFromId(netexId);
                    if (entityType != Object.class && EntityInVersionStructure.class.isAssignableFrom(entityType)) {
                        @SuppressWarnings("unchecked")
                        Class<? extends EntityInVersionStructure> entityClass = 
                            (Class<? extends EntityInVersionStructure>) entityType;
                        EntityInVersionStructure entity = genericEntityInVersionRepository
                            .findFirstByNetexIdOrderByVersionDesc(netexId, entityClass);
                        if (entity != null) {
                            EntityPermissions permissions = createEntityPermissions(entity);
                            resultMap.put(netexId, permissions);
                        }
                    }
                } catch (Exception individualError) {
                    logger.error("Failed to load entity {} individually", netexId, individualError);
                }
            }
        }
        
        return resultMap;
    }

    /**
     * Create EntityPermissions for a given entity
     */
    private EntityPermissions createEntityPermissions(EntityInVersionStructure entity) {
        final boolean canEditEntities = authorizationService.canEditEntity(entity);
        final boolean canDeleteEntity = authorizationService.canDeleteEntity(entity);
        final Set<StopTypeEnumeration> allowedStopPlaceTypes = authorizationService.getAllowedStopPlaceTypes(entity);
        final Set<StopTypeEnumeration> bannedStopPlaceTypes = authorizationService.getBannedStopPlaceTypes(entity);
        final Set<SubmodeEnumuration> allowedSubmode = authorizationService.getAllowedSubmodes(entity);
        final Set<SubmodeEnumuration> bannedSubmode = authorizationService.getBannedSubmodes(entity);

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