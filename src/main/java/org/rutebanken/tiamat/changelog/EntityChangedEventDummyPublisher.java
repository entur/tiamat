package org.rutebanken.tiamat.changelog;

import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@Profile({"!google-pubsub && !test"})
public class EntityChangedEventDummyPublisher extends EntityChangedEventPublisher implements EntityChangedListener {

    private static final Logger log = LoggerFactory.getLogger(EntityChangedEventDummyPublisher.class);

    @Override
    public void onChange(EntityInVersionStructure entity) {
        if (isLoggedEntity(entity)) {
            log.info(entity.toString() + " was changed");
        }

    }

    @Override
    public void onDelete(EntityInVersionStructure entity) {

        if (isLoggedEntity(entity)) {
            log.info(entity.toString() + " was deleted");
        }
    }
}
