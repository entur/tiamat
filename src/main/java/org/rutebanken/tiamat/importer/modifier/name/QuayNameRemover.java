package org.rutebanken.tiamat.importer.modifier.name;

import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QuayNameRemover {
    private static final Logger logger = LoggerFactory.getLogger(QuayNameRemover.class);

    public StopPlace removeQuayNameIfEqualToStopPlaceName(StopPlace stopPlace) {
        if(stopPlace.getQuays() == null) {
            return stopPlace;
        }

        stopPlace.getQuays().forEach(quay -> {
            if(quay.getName() != null && quay.getName().equals(stopPlace.getName())) {
                logger.debug("Removing name '{}' from quay as it matches parent stop place.", stopPlace.getName());
                quay.setName(null);
            }
        });
        return stopPlace;
    }
}
