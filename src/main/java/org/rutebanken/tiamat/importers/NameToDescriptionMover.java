package org.rutebanken.tiamat.importers;


import org.rutebanken.tiamat.model.GroupOfEntities_VersionStructure;
import org.rutebanken.tiamat.model.MultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Move certain parts of name to description field.
 */
@Component
public class NameToDescriptionMover {

    private static final Logger logger = LoggerFactory.getLogger(NameToDescriptionMover.class);
    private static final Pattern pattern = Pattern.compile("\\((.{3,})\\)");

    public StopPlace updateDescriptionFromName(StopPlace stopPlace, String correlationId) {

        updateEntityDescriptionFromName(stopPlace, correlationId);
        if(stopPlace.getQuays() != null) {
            stopPlace.getQuays().forEach(quay -> updateEntityDescriptionFromName(quay, correlationId));
        }
        return stopPlace;
    }

    public void updateEntityDescriptionFromName(GroupOfEntities_VersionStructure entity, String correlationId) {
        if(entity.getName() != null && entity.getName().getValue() != null) {
            String name = entity.getName().getValue();

            Matcher matcher = pattern.matcher(name);

            while (matcher.find()) {
                logger.info("Matching {}. {}", name, correlationId);
                if (matcher.groupCount() > 0) {
                    String description = matcher.group(1).trim();
                    logger.info("Extracted description {}. {}", description, correlationId);
                    entity.setDescription(new MultilingualString(description));
                }
            }

            entity.getName().setValue(matcher.replaceAll("").trim());
        }
    }
}
