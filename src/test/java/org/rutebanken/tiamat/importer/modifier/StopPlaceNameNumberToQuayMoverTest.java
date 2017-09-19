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
import org.rutebanken.tiamat.importer.modifier.StopPlaceNameNumberToQuayMover;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;

import static org.assertj.core.api.Assertions.assertThat;

public class StopPlaceNameNumberToQuayMoverTest {
    private StopPlaceNameNumberToQuayMover stopPlaceNameNumberToQuayMover = new StopPlaceNameNumberToQuayMover();

    @Test
    public void moveHplNumberEndingToQuay() throws Exception {

        final String originalName = "Stavanger hpl. 13";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(originalName));

        stopPlace.getQuays().add(new Quay(new EmbeddableMultilingualString(originalName)));

        stopPlaceNameNumberToQuayMover.moveNumberEndingToQuay(stopPlace);

        assertThat(stopPlace.getName().getValue()).isEqualTo("Stavanger");

        assertThat(stopPlace.getQuays().iterator().next().getPublicCode()).isEqualTo("13");
    }

    @Test
    public void moveSporNumberEndingToQuay() throws Exception {

        final String originalName = "Bussterminalen spor 6";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(originalName));

        stopPlace.getQuays().add(new Quay(new EmbeddableMultilingualString(originalName)));

        stopPlaceNameNumberToQuayMover.moveNumberEndingToQuay(stopPlace);

        assertThat(stopPlace.getName().getValue()).isEqualTo("Bussterminalen");

        assertThat(stopPlace.getQuays().iterator().next().getPublicCode()).isEqualTo("6");
    }

    @Test
    public void handleSpacesAndDotsInStopPlaceName() {
        final String originalStopPlaceName = "Sandnes rb.st hpl. 20";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(originalStopPlaceName));

        stopPlace.getQuays().add(new Quay(new EmbeddableMultilingualString(originalStopPlaceName)));

        stopPlaceNameNumberToQuayMover.moveNumberEndingToQuay(stopPlace);

        assertThat(stopPlace.getName().getValue()).isEqualTo("Sandnes rb.st");

        assertThat(stopPlace.getQuays().iterator().next().getPublicCode()).isEqualTo("20");
    }

    @Test
    public void quayNameIsUnchanged() throws Exception {

        final String originalStopPlaceName = "Stavanger hpl. 13";
        final String originalQuayName = "Another quay name";

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(originalStopPlaceName));

        stopPlace.getQuays().add(new Quay(new EmbeddableMultilingualString(originalQuayName)));

        stopPlaceNameNumberToQuayMover.moveNumberEndingToQuay(stopPlace);

        assertThat(stopPlace.getName().getValue()).isEqualTo("Stavanger");

        assertThat(stopPlace.getQuays().iterator().next().getName().getValue()).isEqualTo(originalQuayName);
    }

    @Test
    public void quayNameCouldBeNull() throws Exception {

        final String originalStopPlaceName = "Stavanger hpl. 13";

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(originalStopPlaceName));

        stopPlace.getQuays().add(new Quay());

        stopPlaceNameNumberToQuayMover.moveNumberEndingToQuay(stopPlace);

        assertThat(stopPlace.getName().getValue()).isEqualTo("Stavanger");

        assertThat(stopPlace.getQuays().iterator().next().getPublicCode()).isEqualTo("13");
    }
}