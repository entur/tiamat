package org.rutebanken.tiamat.versioning;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.SiteElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VersionIncrementor {

    public static final long INITIAL_VERSION = -1;

    private static final Logger logger = LoggerFactory.getLogger(VersionIncrementor.class);

    public void incrementVersion(EntityInVersionStructure entity) {
        Long version = entity.getVersion();

        if (version == -1L) {
            version = 1L;
        } else {
            version++;
        }

        logger.debug("Incrementing version {} for {}", version, entity);
        entity.setVersion(version);
    }
}
