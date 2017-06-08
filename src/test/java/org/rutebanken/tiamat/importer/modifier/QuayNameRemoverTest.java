package org.rutebanken.tiamat.importer.modifier;

import org.junit.Test;
import org.rutebanken.tiamat.importer.modifier.QuayNameRemover;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;

import static org.assertj.core.api.Assertions.assertThat;

public class QuayNameRemoverTest {

    private QuayNameRemover quayNameRemover = new QuayNameRemover();

    @Test
    public void removeQuayNameThatIsEqualToParentStopPlace() throws Exception {
        Quay quay = new Quay(new EmbeddableMultilingualString("Name", "no"));
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Name", "no"));
        stopPlace.getQuays().add(quay);
        StopPlace response = quayNameRemover.removeQuayNameIfEqualToStopPlaceName(stopPlace);
        assertThat(response.getQuays()).extracting(Quay::getName).containsNull();
    }

    @Test
    public void doNotRemoveQuayNameIfNotEqualToParentStopPlace() throws Exception {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Another name", "no"));
        stopPlace.getQuays().add(new Quay(new EmbeddableMultilingualString("Name", "no")));
        StopPlace response = quayNameRemover.removeQuayNameIfEqualToStopPlaceName(stopPlace);
        assertThat(response.getQuays()).extracting(Quay::getName).extracting(EmbeddableMultilingualString::getValue).containsOnly("Name");
    }

    @Test
    public void handleNullQuays() throws Exception {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Whatever", "no"));
        stopPlace.setQuays(null);
        quayNameRemover.removeQuayNameIfEqualToStopPlaceName(stopPlace);
    }

    @Test
    public void toEqualNamesWithDifferntLanguageIsNotEqual() throws Exception {
        Quay quay = new Quay(new EmbeddableMultilingualString("Name", "no"));
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Name", "nn"));
        stopPlace.getQuays().add(quay);
        StopPlace response = quayNameRemover.removeQuayNameIfEqualToStopPlaceName(stopPlace);
        assertThat(response.getQuays()).extracting(Quay::getName).extracting(EmbeddableMultilingualString::getLang).containsOnly("no");
    }
}