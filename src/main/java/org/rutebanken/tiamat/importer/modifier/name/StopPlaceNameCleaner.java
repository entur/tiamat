package org.rutebanken.tiamat.importer.modifier.name;

import org.rutebanken.tiamat.model.GroupOfEntities_VersionStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
