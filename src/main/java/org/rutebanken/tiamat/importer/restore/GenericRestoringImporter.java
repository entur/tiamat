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

package org.rutebanken.tiamat.importer.restore;


import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.GenericEntityInVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GenericRestoringImporter {

    private static final Logger logger = LoggerFactory.getLogger(GenericRestoringImporter.class);

    @Autowired
    private NetexMapper netexMapper;

    @Autowired
    private GenericEntityInVersionRepository genericEntityInVersionRepository;


    public <T extends EntityInVersionStructure> void importObjects(AtomicInteger objectsImported, List<? extends org.rutebanken.netex.model.DataManagedObjectStructure> dataManagedObjects, Class<T> tiamatType) {

        if (dataManagedObjects.isEmpty()) {
            logger.info("No objects for type {}", tiamatType);
            return;
        }

        logger.info("About to import {} {}", dataManagedObjects.size(), tiamatType.getSimpleName());

        dataManagedObjects.parallelStream()
                .map(dataManagedObject -> netexMapper.getFacade().map(dataManagedObject, tiamatType))
                .forEach(mappedObject -> {
                    logger.debug("Saving version {}, netex ID: {} - {}", mappedObject.getVersion(), mappedObject.getNetexId(), mappedObject);
                    genericEntityInVersionRepository.save(mappedObject, tiamatType);
                    objectsImported.incrementAndGet();
                });
        logger.info("Imported {} {}", objectsImported.get(), tiamatType.getSimpleName());
    }

}
