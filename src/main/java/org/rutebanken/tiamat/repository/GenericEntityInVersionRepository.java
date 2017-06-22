package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class GenericEntityInVersionRepository {

    private Repositories repositories = null;

    @Autowired
    public GenericEntityInVersionRepository(ApplicationContext appContext) {
        repositories = new Repositories(appContext);
    }

    public EntityInVersionRepository getRepository(Class<? extends EntityInVersionStructure> clazz) {
        return (EntityInVersionRepository) repositories.getRepositoryFor(clazz);
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
        DataManagedObjectStructureRepository repository = (DataManagedObjectStructureRepository) repositories.getRepositoryFor(clazz);
        String netexId = repository.findFirstByKeyValues(key, values);
        if (netexId != null) {
            return netexId;
        }
        throw new IllegalArgumentException("Cannot find " + clazz.getSimpleName() + " from key: '" + key + "', value: '" + values);
    }
}
