package org.rutebanken.tiamat.importers;

import org.rutebanken.tiamat.model.GroupOfEntities_VersionStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class StopPlaceNameCleaner {

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
        int startIndex= name.indexOf(starChar);
        if ( startIndex > 0 && name.indexOf(endChar) == -1) {
            name = name.substring(0, startIndex-1);
        }
        return name;
    }
}
