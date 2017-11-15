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

import org.junit.Test;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;

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