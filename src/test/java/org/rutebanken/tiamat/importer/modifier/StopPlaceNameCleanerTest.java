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