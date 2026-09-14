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

package org.rutebanken.tiamat.service.parking;

import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Verifies that a parking's parentSiteRef points at a stop place that exists. The reference has no
 * foreign key backing it, and checking it here rather than in the authorization path keeps the
 * behaviour the same for every {@link org.rutebanken.tiamat.auth.AuthorizationService}.
 */
@Service
public class ParkingParentSiteRefValidator {

    private final StopPlaceRepository stopPlaceRepository;

    @Autowired
    public ParkingParentSiteRefValidator(StopPlaceRepository stopPlaceRepository) {
        this.stopPlaceRepository = stopPlaceRepository;
    }

    /** @throws IllegalArgumentException if a parent site ref is set but no stop place has that ID. */
    public void validate(Parking parking) {
        if (parking == null) {
            return;
        }

        SiteRefStructure parentSiteRef = parking.getParentSiteRef();
        if (parentSiteRef == null || parentSiteRef.getRef() == null) {
            // A standalone parking is valid.
            return;
        }

        if (stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parentSiteRef.getRef()) == null) {
            throw new IllegalArgumentException("Cannot save parking with parentSiteRef "
                    + parentSiteRef.getRef() + ": no stop place exists with that ID.");
        }
    }
}
