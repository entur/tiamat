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

import org.rutebanken.tiamat.model.GroupOfEntities_VersionStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Clean stop place and child quay names.
 */
@Component
public class StopPlaceNameCleaner {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceNameCleaner.class);

    @Autowired
    private final WordsRemover wordsRemover;

    public StopPlaceNameCleaner(WordsRemover wordsRemover) {
        this.wordsRemover = wordsRemover;
    }

    public StopPlace cleanNames(StopPlace stopPlace) {
        cleanAndSetName(stopPlace);
        if(stopPlace.getQuays() != null) {
            stopPlace.getQuays().forEach(quay -> {
                cleanAndSetName(quay);
            });
        }
        return stopPlace;
    }

    private void cleanAndSetName(GroupOfEntities_VersionStructure entity) {
        if(entity.getName() != null && entity.getName().getValue() != null) {
            String name = wordsRemover.remove(entity.getName().getValue());

            name = replaceIfUnclosed(name, '(', ')');
            name = replaceIfUnclosed(name, '[', ']');

            entity.getName().setValue(name.trim());
        }
    }

    private String replaceIfUnclosed(String name, char starChar, char endChar) {
        String newName = name;
        int startIndex= name.indexOf(starChar);
        if ( startIndex > 0 && name.indexOf(endChar) == -1) {
            newName = name.substring(0, startIndex-1);
            logger.info("Name '{}' contains unclosed parenthesis. Removing. New name: '{}'", name, newName);
        }
        return newName;
    }
}
