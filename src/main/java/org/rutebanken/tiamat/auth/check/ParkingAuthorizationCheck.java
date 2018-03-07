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
import org.rutebanken.tiamat.model.Parking;

public class ParkingAuthorizationCheck extends PlaceAuthorizationCheck<Parking> {


	public ParkingAuthorizationCheck(Parking entity, RoleAssignment roleAssignment, Polygon administrativeZone) {
		super(entity, roleAssignment, administrativeZone);
	}


	@Override
	protected boolean isMatchForExplicitClassifier(String classifier) {
		if (entity.getParkingType() == null) {
			return false;
		}
		return classifier.equals(entity.getParkingType().value());
	}

	@Override
	protected boolean matchesOrganisation() {
		if (entity.getOrganisationRef() != null) {
			String orgRef = entity.getOrganisationRef().getRef();
			if (orgRef != null) {
				return orgRef.endsWith(":" + roleAssignment.o);
			}
		}
		return true;
	}

}
