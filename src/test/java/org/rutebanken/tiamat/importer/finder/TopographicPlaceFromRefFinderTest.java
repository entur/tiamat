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

package org.rutebanken.tiamat.importer.finder;

import org.junit.Test;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryTestHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class TopographicPlaceFromRefFinderTest {

    private TopographicPlaceFromRefFinder topographicPlaceFromRefFinder = new TopographicPlaceFromRefFinder();

    @Test
    public void findTopographicPlaceFromListWhenPresent() throws Exception {
        List<TopographicPlace> topographicPlacelist = new ArrayList<>();

        TopographicPlace topographicPlace = new TopographicPlace();
        String topographicNetexId = "NSR:TopographicPlace:1";
        topographicPlace.setNetexId(topographicNetexId);

        TopographicPlaceRefStructure topographicPlaceRef = new TopographicPlaceRefStructure();
        topographicPlaceRef.setRef(topographicNetexId);


        topographicPlacelist.add(topographicPlace);

        Optional<TopographicPlace> actual = topographicPlaceFromRefFinder.findTopographicPlaceFromRef(topographicPlacelist, topographicPlaceRef);
        assertThat(actual).isPresent();
        assertThat(actual.get().getNetexId()).isEqualTo(topographicNetexId);
    }

    @Test
    public void findTopographicPlaceFromListWhenEmpty() throws Exception {
        List<TopographicPlace> topographicPlacelist = new ArrayList<>();

        String id = "1";

        TopographicPlaceRefStructure topographicPlaceRef = new TopographicPlaceRefStructure();
        topographicPlaceRef.setRef(id);

        Optional<TopographicPlace> actual = topographicPlaceFromRefFinder.findTopographicPlaceFromRef(topographicPlacelist, topographicPlaceRef);
        assertThat(actual).isEmpty();
    }

}