package org.rutebanken.tiamat.importers;

import org.rutebanken.tiamat.model.GroupOfEntities_VersionStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            entity.getName().setValue(wordsRemover.remove(entity.getName().getValue()));
        }
    }
}
