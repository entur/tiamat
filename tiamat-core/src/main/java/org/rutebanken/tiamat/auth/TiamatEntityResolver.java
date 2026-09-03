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

package org.rutebanken.tiamat.auth;

import org.rutebanken.helper.organisation.EntityResolver;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TiamatEntityResolver implements EntityResolver {

    private static final Logger logger = LoggerFactory.getLogger(TiamatEntityResolver.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Override
    public Object resolveCorrectEntity(Object entity) {

        switch (entity) {
            case null -> {
                return null;
            }
            case Quay quay -> {
                StopPlace stopPlace = stopPlaceRepository.findByQuay(quay);
                if (stopPlace == null) {
                    throw new IllegalArgumentException("Cannot resolve stop place from quay: " + entity);
                }
                return stopPlace;
            }
            case Parking parking -> {
                if (parking.getParentSiteRef() != null && parking.getParentSiteRef().getRef() != null) {
                    StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parking.getParentSiteRef().getRef());

                    if (stopPlace != null) {
                        return stopPlace;
                    }
                    // The referenced parent stop place no longer exists. Fall back to
                    // checking permission on the parking itself instead of throwing -
                    // same as the case below where the parking has no parent site ref.
                    logger.warn("Cannot resolve stop place from parking: {}. Parent site ref {} no longer exists, falling back to parking permission check.",
                            entity, parking.getParentSiteRef().getRef());
                }
            }
            default -> {
            }
        }


        return entity;

    }
}
