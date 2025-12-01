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

import org.rutebanken.tiamat.model.PostalAddress;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POSTAL_ADDRESS_ADDRESS_LINE1;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POSTAL_ADDRESS_POST_CODE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POSTAL_ADDRESS_TOWN;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;

@Component
public class PostalAddressMapper {

    public boolean populatePostalAddressFromInput(StopPlace stopPlace, Map input) {
        PostalAddress newPostalAddress = map(input);
        if (stopPlace.getPostalAddress() != null) {
            if (newPostalAddress == null) {
                // Postal address removed
                stopPlace.setPostalAddress(null);
                return true;
            }
            boolean updated = false;
            if (!Objects.equals(stopPlace.getPostalAddress().getPostCode(), newPostalAddress.getPostCode())) {
                updated = true;
                stopPlace.getPostalAddress().setPostCode(newPostalAddress.getPostCode());
            }
            if (!Objects.equals(stopPlace.getPostalAddress().getAddressLine1(), newPostalAddress.getAddressLine1())) {
                updated = true;
                stopPlace.getPostalAddress().setAddressLine1(newPostalAddress.getAddressLine1());
            }
            if (!Objects.equals(stopPlace.getPostalAddress().getTown(), newPostalAddress.getTown())) {
                updated = true;
                stopPlace.getPostalAddress().setTown(newPostalAddress.getTown());
            }
            if (updated) {
                // Increment version if any field was updated
                stopPlace.getPostalAddress().setVersion(stopPlace.getPostalAddress().getVersion() + 1);
            }
            return updated;
        }
        if (newPostalAddress == null) {
            // No postal address in input and none existing
            return false;
        }
        // Set new postal address
        stopPlace.setPostalAddress(newPostalAddress);
        return true;
    }

    public PostalAddress map(Map input) {
        if (input == null) {
            return null;
        }

        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setVersion(1L);

        if (input.get(POSTAL_ADDRESS_ADDRESS_LINE1) != null) {
            postalAddress.setAddressLine1(getEmbeddableString((Map) input.get(POSTAL_ADDRESS_ADDRESS_LINE1)));
        }

        if (input.get(POSTAL_ADDRESS_TOWN) != null) {
            postalAddress.setTown(getEmbeddableString((Map) input.get(POSTAL_ADDRESS_TOWN)));
        }

        if (input.get(POSTAL_ADDRESS_POST_CODE) != null) {
            postalAddress.setPostCode((String) input.get(POSTAL_ADDRESS_POST_CODE));
        }

        return postalAddress;
    }
}
