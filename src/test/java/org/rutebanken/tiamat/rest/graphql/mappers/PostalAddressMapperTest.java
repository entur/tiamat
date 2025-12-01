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

package org.rutebanken.tiamat.rest.graphql.mappers;

import org.junit.Test;
import org.rutebanken.tiamat.model.PostalAddress;
import org.rutebanken.tiamat.model.StopPlace;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POSTAL_ADDRESS_ADDRESS_LINE1;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POSTAL_ADDRESS_POST_CODE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POSTAL_ADDRESS_TOWN;


public class PostalAddressMapperTest {

    private final PostalAddressMapper postalAddressMapper = new PostalAddressMapper();

    private StopPlace createStopPlaceWithPostalAddress(String addressLine1, String town, String postCode) {
        StopPlace stopPlace = new StopPlace();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setAddressLine1(EmbeddableMultilingualStringMapper.getEmbeddableString(Map.of("value", addressLine1)));
        postalAddress.setTown(EmbeddableMultilingualStringMapper.getEmbeddableString(Map.of("value", town)));
        postalAddress.setPostCode(postCode);
        postalAddress.setVersion(1L);
        stopPlace.setPostalAddress(postalAddress);
        return stopPlace;
    }

    @Test
    public void testStopPlaceNotUpdated() {
        StopPlace stopPlace = new StopPlace();
        boolean updated = postalAddressMapper.populatePostalAddressFromInput(stopPlace, null);
        assertThat(updated).isFalse();
        assertThat(stopPlace.getPostalAddress()).isNull();
    }

    @Test
    public void testStopPlacePostalAddressCreated() {
        StopPlace stopPlace = new StopPlace();
        Map<String, Object> input = Map.of(
                POSTAL_ADDRESS_ADDRESS_LINE1, Map.of("value", "New Address"),
                POSTAL_ADDRESS_TOWN, Map.of("value", "New Town"),
                POSTAL_ADDRESS_POST_CODE, "1111"
        );
        boolean updated = postalAddressMapper.populatePostalAddressFromInput(stopPlace, input);
        assertThat(updated).isTrue();
        assertThat(stopPlace.getPostalAddress()).isNotNull();
        assertThat(stopPlace.getPostalAddress().getAddressLine1().getValue()).isEqualTo("New Address");
        assertThat(stopPlace.getPostalAddress().getTown().getValue()).isEqualTo("New Town");
        assertThat(stopPlace.getPostalAddress().getPostCode()).isEqualTo("1111");
        assertThat(stopPlace.getPostalAddress().getVersion()).isEqualTo(1L);
    }

    @Test
    public void testStopPlacePostalAddressRemoved() {
        StopPlace stopPlace = createStopPlaceWithPostalAddress("Old Address", "Old Town", "0000");
        boolean updated = postalAddressMapper.populatePostalAddressFromInput(stopPlace, null);
        assertThat(updated).isTrue();
        assertThat(stopPlace.getPostalAddress()).isNull();
    }

    @Test
    public void testStopPlacePostalAddressUpdated() {
        StopPlace stopPlace = createStopPlaceWithPostalAddress("Old Address", "Old Town", "0000");
        Map<String, Object> input = Map.of(
                POSTAL_ADDRESS_ADDRESS_LINE1, Map.of("value", "New Address"),
                POSTAL_ADDRESS_TOWN, Map.of("value", "New Town"),
                POSTAL_ADDRESS_POST_CODE, "1111"
        );
        boolean updated = postalAddressMapper.populatePostalAddressFromInput(stopPlace, input);
        assertThat(updated).isTrue();
        assertThat(stopPlace.getPostalAddress()).isNotNull();
        assertThat(stopPlace.getPostalAddress().getAddressLine1().getValue()).isEqualTo("New Address");
        assertThat(stopPlace.getPostalAddress().getTown().getValue()).isEqualTo("New Town");
        assertThat(stopPlace.getPostalAddress().getPostCode()).isEqualTo("1111");
        assertThat(stopPlace.getPostalAddress().getVersion()).isEqualTo(2L);
    }

    @Test
    public void testStopPlacePostalAddressNotUpdated() {
        StopPlace stopPlace = createStopPlaceWithPostalAddress("Old Address", "Old Town", "0000");
        Map<String, Object> input = Map.of(
                POSTAL_ADDRESS_ADDRESS_LINE1, Map.of("value", "Old Address"),
                POSTAL_ADDRESS_TOWN, Map.of("value", "Old Town"),
                POSTAL_ADDRESS_POST_CODE, "0000"
        );
        boolean updated = postalAddressMapper.populatePostalAddressFromInput(stopPlace, input);
        assertThat(updated).isFalse();
        assertThat(stopPlace.getPostalAddress()).isNotNull();
        assertThat(stopPlace.getPostalAddress().getAddressLine1().getValue()).isEqualTo("Old Address");
        assertThat(stopPlace.getPostalAddress().getTown().getValue()).isEqualTo("Old Town");
        assertThat(stopPlace.getPostalAddress().getPostCode()).isEqualTo("0000");
        assertThat(stopPlace.getPostalAddress().getVersion()).isEqualTo(1L);
    }
}

