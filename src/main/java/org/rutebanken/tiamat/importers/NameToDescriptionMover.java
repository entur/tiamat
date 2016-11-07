package org.rutebanken.tiamat.importers;


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

    public void move(StopPlace stopPlace) {

        if(stopPlace.getName() != null && stopPlace.getName().getValue() != null) {

            String name = stopPlace.getName().getValue();
            Matcher matcher = pattern.matcher(name);

            while(matcher.find()) {
                logger.info("Matching {}", name);
                if (matcher.groupCount() > 0) {
                    String description = matcher.group(1).trim();
                    logger.info("Extracted description {}", description);
                    stopPlace.setDescription(new MultilingualString(description));
                }
            }

            stopPlace.getName().setValue(matcher.replaceAll("").trim());
        }
    }

}
