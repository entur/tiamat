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
			String orgRef = entity.getOrganisationRef().getValue().getRef();
			if (orgRef != null) {
				return orgRef.endsWith(":" + roleAssignment.o);
			}
		}
		return true;
	}

}
