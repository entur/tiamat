package org.rutebanken.tiamat.importer;

import org.junit.Test;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;

import static org.assertj.core.api.Assertions.assertThat;

public class StopPlaceNameCleanerTest {

    private StopPlaceNameCleaner stopPlaceNameCleaner = new StopPlaceNameCleaner(new WordsRemover());

    @Test
    public void cleanUnclosedParenthesis() throws Exception {
        String stopPlaceName =  "St. Halvards plass (mot Bispe";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlaceNameCleaner.cleanNames(stopPlace);
        assertThat(stopPlace.getName().getValue()).isEqualTo("St. Halvards plass");
    }

    @Test
    public void doNotCleanClosedParenthesis() throws Exception {
        String stopPlaceName =  "St. Halvards plass (mot Bispelokket) ";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlaceNameCleaner.cleanNames(stopPlace);
        assertThat(stopPlace.getName().getValue()).isEqualTo(stopPlaceName.trim());
    }
}