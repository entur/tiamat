package org.rutebanken.tiamat.repository.generic;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.repository.DataManagedObjectStructureRepository;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GenericEntityInVersionRepository {

    private Repositories repositories = null;
    
    @Autowired
    private EntityManager entityManager;

    @Autowired
    public GenericEntityInVersionRepository(ApplicationContext appContext) {
        repositories = new Repositories(appContext);
    }

    public EntityInVersionRepository getRepository(Class<? extends EntityInVersionStructure> clazz) {
        Optional<Object> repositoryFor = repositories.getRepositoryFor(clazz);
        return (EntityInVersionRepository) repositoryFor.orElse(null);
    }

    public <T extends EntityInVersionStructure> T findFirstByNetexIdOrderByVersionDesc(String netexId, Class<T> clazz) {
        return clazz.cast(getRepository(clazz).findFirstByNetexIdOrderByVersionDesc(netexId));
    }

    public <T extends EntityInVersionStructure> T findFirstByNetexIdAndVersion(String netexId, long version, Class<T> clazz) {
        return clazz.cast(getRepository(clazz).findFirstByNetexIdAndVersion(netexId, version));
    }

    public <T extends EntityInVersionStructure> T save(EntityInVersionStructure entityInVersionStructure, Class<T> clazz) {
        return clazz.cast(getRepository(clazz).save(entityInVersionStructure));
    }

    public String findByKeyValue(String key, Set<String> values, Class<? extends DataManagedObjectStructure> clazz) {
        DataManagedObjectStructureRepository repository = (DataManagedObjectStructureRepository) getRepository(clazz);
        String netexId = repository.findFirstByKeyValues(key, values);
        if (netexId != null) {
            return netexId;
        }
        throw new IllegalArgumentException("Cannot find " + clazz.getSimpleName() + " from key: '" + key + "', value: '" + values);
    }
    
    /**
     * Batch load latest version entities by netex IDs for a specific entity type
     * 
     * @param netexIds List of netex IDs to load
     * @param clazz Entity class type
     * @return List of latest version entities
     */
    public <T extends EntityInVersionStructure> List<T> findLatestVersionByNetexIds(List<String> netexIds, Class<T> clazz) {
        if (netexIds == null || netexIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Use a subquery to find the latest version for each netex ID
        String jpql = "SELECT e FROM " + clazz.getSimpleName() + " e WHERE e.netexId IN :netexIds " +
                     "AND e.version = (SELECT MAX(e2.version) FROM " + clazz.getSimpleName() + " e2 WHERE e2.netexId = e.netexId)";
        
        TypedQuery<T> query = entityManager.createQuery(jpql, clazz);
        query.setParameter("netexIds", netexIds);
        
        return query.getResultList();
    }
    
    /**
     * Batch load latest version entities grouped by type
     * 
     * @param netexIdsByType Map of entity class to list of netex IDs
     * @return Map of netex ID to entity
     */
    public Map<String, EntityInVersionStructure> findLatestVersionByNetexIdsGrouped(Map<Class<?>, List<String>> netexIdsByType) {
        Map<String, EntityInVersionStructure> result = new HashMap<>();
        
        for (Map.Entry<Class<?>, List<String>> entry : netexIdsByType.entrySet()) {
            Class<?> entityType = entry.getKey();
            List<String> netexIds = entry.getValue();
            
            if (EntityInVersionStructure.class.isAssignableFrom(entityType)) {
                @SuppressWarnings("unchecked")
                Class<? extends EntityInVersionStructure> entityClass = 
                    (Class<? extends EntityInVersionStructure>) entityType;
                
                List<? extends EntityInVersionStructure> entities = 
                    findLatestVersionByNetexIds(netexIds, entityClass);
                
                for (EntityInVersionStructure entity : entities) {
                    result.put(entity.getNetexId(), entity);
                }
            }
        }
        
        return result;
    }
}
