package org.rutebanken.tiamat.importer.restore;


import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
import org.rutebanken.tiamat.repository.GenericDataManagedObjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GenericRestoringImporter<T extends EntityInVersionStructure> {

    private static final Logger logger = LoggerFactory.getLogger(GenericRestoringImporter.class);

    @Autowired
    private NetexMapper netexMapper;

    @Autowired
    private GenericDataManagedObjectRepository genericDataManagedObjectRepository;



    public void importObjects(AtomicInteger objectsImported, List<? extends org.rutebanken.netex.model.DataManagedObjectStructure> dataManagedObjects, Class<T> tiamatType) {

        if (dataManagedObjects.isEmpty()) {
            logger.info("No objects for type {}", tiamatType);
            return;
        }

        logger.info("About to import {} {}", dataManagedObjects.size(), tiamatType.getSimpleName());

        dataManagedObjects.parallelStream()
                .map(dataManagedObject -> netexMapper.getFacade().map(dataManagedObject, tiamatType))
                .forEach(mappedObject -> {
                    logger.debug("Saving version {}, netex ID: {} - {}", mappedObject.getVersion(), mappedObject.getNetexId(), mappedObject);
                    genericDataManagedObjectRepository.save(mappedObject, tiamatType);
                    objectsImported.incrementAndGet();
                });
        logger.info("Imported {} {}", objectsImported.get(), tiamatType.getSimpleName());
    }

}
