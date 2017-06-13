package org.rutebanken.tiamat.importer.modifier;

import org.junit.Test;
import org.rutebanken.tiamat.importer.modifier.StopPlaceNameCleaner;
import org.rutebanken.tiamat.importer.modifier.WordsRemover;
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

    @Test
    public void doNotCleanOsloBussterminal() throws Exception {
        String stopPlaceName =  "Oslo Bussterminal";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlaceNameCleaner.cleanNames(stopPlace);
        assertThat(stopPlace.getName().getValue()).isEqualTo(stopPlaceName.trim());
    }

    @Test
    public void cleanBybanestopp() throws Exception {
        String stopPlaceName =  "Sentrum, bybanestopp";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlaceNameCleaner.cleanNames(stopPlace);
        assertThat(stopPlace.getName().getValue()).isEqualTo("Sentrum");
    }
}