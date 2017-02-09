package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VersionIncrementor {

    private static final Logger logger = LoggerFactory.getLogger(VersionIncrementor.class);

    public void incrementVersion(EntityInVersionStructure versionedEntity) {
        Long version = tryParseLong(versionedEntity.getVersion());
        version ++;
        logger.debug("Setting version {} for {}", version, versionedEntity);
        versionedEntity.setVersion(version.toString());
    }

    private long tryParseLong(String version) {
        try {
            return Long.parseLong(version);
        } catch(NumberFormatException |NullPointerException e) {
            logger.warn("Could not parse version from string {}. Returning 0", version);
            return 0L;
        }
    }

}
