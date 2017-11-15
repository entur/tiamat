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

import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.repository.DataManagedObjectStructureRepository;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
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
