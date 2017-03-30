package org.rutebanken.tiamat.auth.check;

import com.vividsolutions.jts.geom.Polygon;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.model.Place;

public class PlaceAuthorizationCheck<T extends Place> extends AuthorizationCheck<T> {

	public PlaceAuthorizationCheck(T entity, RoleAssignment roleAssignment, Polygon administrativeZone) {
		super(entity, roleAssignment, administrativeZone);
	}

	@Override
	protected boolean matchesAdministrativeZone() {
		if (administrativeZone != null) {
			return administrativeZone.contains(entity.getCentroid());
		}
		return true;
	}
}
