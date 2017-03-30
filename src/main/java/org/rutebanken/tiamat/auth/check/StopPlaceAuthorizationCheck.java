package org.rutebanken.tiamat.auth.check;

import com.vividsolutions.jts.geom.Polygon;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.model.StopPlace;

public class StopPlaceAuthorizationCheck extends PlaceAuthorizationCheck<StopPlace> {


	public StopPlaceAuthorizationCheck(StopPlace entity, RoleAssignment roleAssignment, Polygon administrativeZone) {
		super(entity, roleAssignment, administrativeZone);
	}


	@Override
	protected boolean isMatchForExplicitClassifier(String classifier) {
		if (entity.getStopPlaceType() == null) {
			return false;
		}
		return classifier.equals(entity.getStopPlaceType().value());
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
