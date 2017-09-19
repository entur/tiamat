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
import org.rutebanken.tiamat.importer.modifier.NameToDescriptionMover;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;

import static org.assertj.core.api.Assertions.assertThat;

public class NameToDescriptionMoverTest {

    private NameToDescriptionMover nameToDescriptionMover = new NameToDescriptionMover();

    @Test
    public void kvernstuen() {
        expectNameAndDescriptionToBeMoved("Dam (i Kvernstuveien)", "Dam", "i Kvernstuveien");
    }

    @Test
    public void strømsveien() {
        expectNameAndDescriptionToBeMoved("Alfaset (i Strømsveien)", "Alfaset", "i Strømsveien");
    }

    @Test
    public void linderudsletta() {
        expectNameAndDescriptionToBeMoved("Linderudsletta (nordsiden)", "Linderudsletta", "nordsiden");
    }

    private void expectNameAndDescriptionToBeMoved(String originalName, String expectedName, String expectedDescription) {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(originalName));
        nameToDescriptionMover.updateEntityDescriptionFromName(stopPlace);
        assertThat(stopPlace.getName().getValue()).isEqualTo(expectedName);
        assertThat(stopPlace.getDescription().getValue()).isEqualTo(expectedDescription);
    }
}