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

/**
 * If the stop place name contains platform number, move this to the Quay name.
 */
@Component
public class StopPlaceNameNumberToQuayMover {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceNameNumberToQuayMover.class);

    private final Pattern pattern;

    @Autowired
    public StopPlaceNameNumberToQuayMover(@Value("${StopPlaceNameNumberToQuayMover.terms:hpl,spor,plattform,pl,plf}") String[] terms) {
        String termsPart = String.join("|", terms);
        pattern = Pattern.compile("(.+)\\s(" + termsPart + ")\\.?\\s([0-9]+)");
        logger.info("Terms: {}. Pattern: {}", terms, pattern);
    }

    public StopPlaceNameNumberToQuayMover() {
        this(new String[]{"hpl", "spor", "plattform", "pl", "plf"});
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
                String newPlateCode = matcher.group(3);

                setQuayPlateCode(stopPlace, newPlateCode);

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

    private void setQuayPlateCode(StopPlace stopPlace, String plateCode) {

        if (stopPlace.getQuays().isEmpty()) {
            logger.warn("No quays for stop place. Cannot set quay plate code to '{}'. {}", plateCode, stopPlace);
            return;
        }
        if (stopPlace.getQuays().size() > 1) {
            logger.warn("This stop place contains multiple quays. Cannot set plate code to '{}'. {}", plateCode, stopPlace);
            return;
        }

        // We cannot update more than one quay with the same plate code.
        Quay quay = stopPlace.getQuays().iterator().next();
        if(quay.getPlateCode() != null && !quay.getPlateCode().isEmpty()) {
            logger.warn("Quay plate code '{}' is already set. Cannot set quay plate code to '{}'.", quay.getPlateCode(), plateCode);
        } else {
            logger.info("Quay plate code is empty. Will set it to '{}'. {}", plateCode, quay);
            quay.setPlateCode(plateCode);
        }

    }

}
