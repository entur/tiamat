package org.rutebanken.tiamat.importer.modifier;

import org.junit.Test;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.repository.ReferenceResolver;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TopographicPlaceNameRemoverTest {

    @Test
    public void removeTopographicPlaceName() {

        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("Klæbu"));

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Haugum Klæbu"));

        stopPlace.setTopographicPlace(topographicPlace);

        TopographicPlaceNameRemover topographicPlaceNameRemover = new TopographicPlaceNameRemover(mock(ReferenceResolver.class));

        StopPlace actual = topographicPlaceNameRemover.removeIfmatch(stopPlace);

        assertThat(actual.getName().getValue()).isEqualTo("Haugum");

    }

    @Test
    public void doNotRemoveNameStartingWithTopographicPlaceName() {

        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("Klæbu"));

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Klæbu sentrum"));

        stopPlace.setTopographicPlace(topographicPlace);

        TopographicPlaceNameRemover topographicPlaceNameRemover = new TopographicPlaceNameRemover(mock(ReferenceResolver.class));

        StopPlace actual = topographicPlaceNameRemover.removeIfmatch(stopPlace);

        assertThat(actual.getName().getValue()).isEqualTo("Klæbu sentrum");

    }

    @Test
    public void doNotRemoveNameIfNameSameAsTopographicPlaceName() {

        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("Asker"));

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Asker"));

        stopPlace.setTopographicPlace(topographicPlace);

        TopographicPlaceNameRemover topographicPlaceNameRemover = new TopographicPlaceNameRemover(mock(ReferenceResolver.class));

        StopPlace actual = topographicPlaceNameRemover.removeIfmatch(stopPlace);

        assertThat(actual.getName().getValue()).isEqualTo("Asker");

    }

    @Test
    public void doNotRemoveNameIfNoSpace() {

        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("Klæbu"));

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("SomethingKlæbu"));

        stopPlace.setTopographicPlace(topographicPlace);

        TopographicPlaceNameRemover topographicPlaceNameRemover = new TopographicPlaceNameRemover(mock(ReferenceResolver.class));

        StopPlace actual = topographicPlaceNameRemover.removeIfmatch(stopPlace);

        assertThat(actual.getName().getValue()).isEqualTo("SomethingKlæbu");

    }

    @Test
    public void removeTopographicPlaceCountyAndTownName() {

        TopographicPlace county = new TopographicPlace(new EmbeddableMultilingualString("Akershus"));

        TopographicPlace municipality = new TopographicPlace(new EmbeddableMultilingualString("Asker"));

        TopographicPlaceRefStructure countyRef = new TopographicPlaceRefStructure(county);

        municipality.setParentTopographicPlaceRef(countyRef);

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Nesbru Asker Akershus"));

        stopPlace.setTopographicPlace(municipality);

        ReferenceResolver referenceResolver = mock(ReferenceResolver.class);

        when(referenceResolver.resolve(any(TopographicPlaceRefStructure.class))).thenReturn(county);

        TopographicPlaceNameRemover topographicPlaceNameRemover = new TopographicPlaceNameRemover(referenceResolver);

        StopPlace actual = topographicPlaceNameRemover.removeIfmatch(stopPlace);

        assertThat(actual.getName().getValue()).isEqualTo("Nesbru");

    }

}