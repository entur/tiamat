package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

@Service
public class EntityInVersionStructureRepository {

    private Repositories repositories = null;

    @Autowired
    public EntityInVersionStructureRepository(ApplicationContext appContext) {
        repositories = new Repositories(appContext);
    }

    public IdentifiedEntityRepository getRepository(Class<? extends EntityInVersionStructure> clazz) {
        return (IdentifiedEntityRepository) repositories.getRepositoryFor(clazz);
    }

    public <T extends EntityInVersionStructure> T findFirstByNetexIdOrderByVersionDesc(String netexId, Class<T> clazz) {
        return clazz.cast(getRepository(clazz).findFirstByNetexIdOrderByVersionDesc(netexId));
    }

    public <T extends EntityInVersionStructure> T findFirstByNetexIdAndVersion(String netexId, long version, Class<T> clazz) {
        return clazz.cast(getRepository(clazz).findFirstByNetexIdAndVersion(netexId, version));
    }
}
