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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TiamatEntityResolver implements EntityResolver {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Override
    public Object resolveCorrectEntity(Object entity) {

        if(entity == null) {
            return null;
        }

        if(entity instanceof Quay) {
            StopPlace stopPlace = stopPlaceRepository.findByQuay((Quay) entity);
            if(stopPlace == null) {
                throw new IllegalArgumentException("Cannot resolve stop place from quay: " + entity);
            }
            return stopPlace;
        }

        if(entity instanceof Parking) {
            Parking parking = (Parking) entity;
            if (parking.getParentSiteRef() != null && parking.getParentSiteRef().getRef() != null) {
                StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parking.getParentSiteRef().getRef());

                if(stopPlace == null) {
                    throw new IllegalArgumentException("Cannot resolve stop place from parking: " + entity);
                }
                return stopPlace;
            }
        }


        return entity;

    }
}
