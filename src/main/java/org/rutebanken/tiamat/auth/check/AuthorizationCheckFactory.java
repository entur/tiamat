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

package org.rutebanken.tiamat.auth.check;

import com.vividsolutions.jts.geom.Polygon;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationCheckFactory {

	@Autowired
	private StopPlaceRepository stopPlaceRepository;

	public <T extends EntityStructure> AuthorizationCheck buildCheck(T entity, RoleAssignment roleAssignment, Polygon administrativeZone) {
		if (entity instanceof StopPlace) {
			return new StopPlaceAuthorizationCheck((StopPlace) entity, roleAssignment, administrativeZone);
		}
		if (entity instanceof Quay) {
			StopPlace stopPlace = stopPlaceRepository.findByQuay((Quay) entity);
			if (stopPlace != null) {
				return new StopPlaceAuthorizationCheck(stopPlace, roleAssignment, administrativeZone);
			}
		}
		if (entity instanceof Parking) {
			Parking parking = (Parking) entity;
			if (parking.getParentSiteRef() != null && parking.getParentSiteRef().getRef() != null) {
				StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parking.getParentSiteRef().getRef());
				if (stopPlace != null) {
					return new StopPlaceAuthorizationCheck(stopPlace, roleAssignment, administrativeZone);
				}
			}
			return new ParkingAuthorizationCheck(parking, roleAssignment, administrativeZone);
		}
		if (entity instanceof Place) {
			return new PlaceAuthorizationCheck<>((Place) entity, roleAssignment, administrativeZone);
		}
		return new AuthorizationCheck<>(entity, roleAssignment, administrativeZone);
	}

}
