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

package org.rutebanken.tiamat.importer.modifier;


import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.GroupOfEntities_VersionStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Move certain parts of stop place and quay name to description field.
 */
@Component
public class NameToDescriptionMover {

    private static final Logger logger = LoggerFactory.getLogger(NameToDescriptionMover.class);
    private static final Pattern pattern = Pattern.compile("\\((.{3,})\\)");

    public StopPlace updateDescriptionFromName(StopPlace stopPlace) {

        updateEntityDescriptionFromName(stopPlace);
        if(stopPlace.getQuays() != null) {
            stopPlace.getQuays().forEach(quay -> updateEntityDescriptionFromName(quay));
        }
        return stopPlace;
    }

    public void updateEntityDescriptionFromName(GroupOfEntities_VersionStructure entity) {
        if(entity.getName() != null && entity.getName().getValue() != null) {
            String name = entity.getName().getValue();

            Matcher matcher = pattern.matcher(name);

            while (matcher.find()) {
                logger.debug("Matching {}", name);
                if (matcher.groupCount() > 0) {
                    String description = matcher.group(1).trim();
                    logger.debug("Extracted description {}", description);
                    entity.setDescription(new EmbeddableMultilingualString(description));
                }
            }

            entity.getName().setValue(matcher.replaceAll("").trim());
        }
    }
}
