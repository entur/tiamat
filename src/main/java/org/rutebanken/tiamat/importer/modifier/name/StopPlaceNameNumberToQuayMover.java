package org.rutebanken.tiamat.importer.modifier.name;

import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StopPlaceNameNumberToQuayMover {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceNameNumberToQuayMover.class);

    private final Pattern pattern;

    @Autowired
    public StopPlaceNameNumberToQuayMover(@Value("${StopPlaceNameNumberToQuayMover.terms:hpl,spor}") String[] terms) {
        String termsPart = String.join("|", terms);
        pattern = Pattern.compile("(\\w*)\\s((" + termsPart + ")\\.?\\s[0-9]+)");
        logger.info("Terms: {}. Pattern: {}", terms, pattern);
    }

    public StopPlaceNameNumberToQuayMover() {
        this(new String[]{"hpl", "spor"});
    }

    public StopPlace moveNumberEndingToQuay(StopPlace stopPlace) {

        if (stopPlace.getName() == null) {
            return stopPlace;
        }

        String originalStopPlaceName = stopPlace.getName().getValue();

        Matcher matcher = pattern.matcher(originalStopPlaceName);
        if (matcher.matches()) {
            if (matcher.groupCount() == 3) {
                String newStopPlaceName = matcher.group(1);
                String newQuayName = matcher.group(2);

                setQuayName(stopPlace, originalStopPlaceName, newQuayName);

                stopPlace.getName().setValue(newStopPlaceName);
                logger.info("Changing stop place name from '{}' to '{}'. {}", originalStopPlaceName, newStopPlaceName, stopPlace);

            } else {
                logger.info("Stop place name '{}' matches but group count is not as expected.", originalStopPlaceName);
            }

        } else {
            logger.debug("No match in stop place name {}", originalStopPlaceName);
        }

        return stopPlace;
    }

    private void setQuayName(StopPlace stopPlace, String originalStopPlaceName, String newQuayName) {

        if (stopPlace.getQuays().isEmpty()) {
            logger.warn("No quays for stop place. Cannot set quay name to '{}'. {}", newQuayName, stopPlace);
            return;
        }
        if (stopPlace.getQuays().size() > 1) {
            logger.warn("This stop place contains multiple quays. Cannot set quay name to '{}'. {}", newQuayName, stopPlace);
            return;
        }

        Quay quay = stopPlace.getQuays().iterator().next();
        if(quay.getName() == null) {
            logger.info("Quay name is empty. Will set it to '{}'. Stop Place: {}", newQuayName, stopPlace);
            quay.setName(new EmbeddableMultilingualString(newQuayName));
        } else {
            String originalQuayName = quay.getName().getValue();
            if (originalQuayName.equals(originalStopPlaceName)) {
                quay.getName().setValue(newQuayName);
                logger.info("Changing quay name from '{}' to '{}'. Quay: {}", originalQuayName, newQuayName, quay);
            } else {
                logger.warn("Original quay name '{}' is not equal to original stop place name '{}'. Cannot set quay name to '{}'.", originalQuayName, originalStopPlaceName, newQuayName);
            }
        }

    }

}
