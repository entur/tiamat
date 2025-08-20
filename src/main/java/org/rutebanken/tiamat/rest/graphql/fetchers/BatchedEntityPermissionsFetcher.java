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

import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.authorization.EntityPermissions;
import org.rutebanken.tiamat.rest.graphql.dataloader.EntityPermissionsDataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for batch loading entity permissions to solve N+1 query problems.
 * This can be used to preload permissions for a list of entities.
 */
@Component
public class BatchedEntityPermissionsFetcher {

    @Autowired
    private EntityPermissionsDataLoader entityPermissionsDataLoader;

    /**
     * Batch load permissions for a list of entities
     * 
     * @param entities List of entities to load permissions for
     * @return Map of netex ID to EntityPermissions
     */
    public Map<String, EntityPermissions> batchLoadPermissions(List<? extends EntityInVersionStructure> entities) {
        if (entities == null || entities.isEmpty()) {
            return new HashMap<>();
        }
        
        List<String> netexIds = entities.stream()
            .map(EntityInVersionStructure::getNetexId)
            .collect(Collectors.toList());
            
        return entityPermissionsDataLoader.batchLoadEntityPermissions(netexIds);
    }

    /**
     * Enrich entities with their permissions
     * 
     * @param entities List of entities to enrich
     * @return Map of netex ID to enriched entity with permissions
     */
    public <T extends EntityInVersionStructure> Map<String, EnrichedEntity<T>> enrichWithPermissions(List<T> entities) {
        Map<String, EntityPermissions> permissionsMap = batchLoadPermissions(entities);
        
        Map<String, EnrichedEntity<T>> result = new HashMap<>();
        for (T entity : entities) {
            String netexId = entity.getNetexId();
            EntityPermissions permissions = permissionsMap.get(netexId);
            result.put(netexId, new EnrichedEntity<>(entity, permissions));
        }
        
        return result;
    }

    /**
     * Wrapper class for entity with its permissions
     */
    public static class EnrichedEntity<T extends EntityInVersionStructure> {
        private final T entity;
        private final EntityPermissions permissions;

        public EnrichedEntity(T entity, EntityPermissions permissions) {
            this.entity = entity;
            this.permissions = permissions;
        }

        public T getEntity() {
            return entity;
        }

        public EntityPermissions getPermissions() {
            return permissions;
        }
    }
}