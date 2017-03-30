package org.rutebanken.tiamat.auth.check;

import com.vividsolutions.jts.geom.Polygon;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.model.EntityStructure;

import java.util.List;

import static org.rutebanken.tiamat.auth.AuthorizationConstants.ENTITY_CLASSIFIER_ALL_TYPES;

public class AuthorizationCheck<T extends EntityStructure> {


	protected T entity;
	protected RoleAssignment roleAssignment;
	protected Polygon administrativeZone;

	public AuthorizationCheck(T entity, RoleAssignment roleAssignment, Polygon administrativeZone) {
		this.entity = entity;
		this.roleAssignment = roleAssignment;
		this.administrativeZone = administrativeZone;
	}

	public boolean isAllowed() {
		return matchesTypeAndClassifier() && matchesOrganisation() && matchesAdministrativeZone();
	}

	private boolean matchesTypeAndClassifier() {
		if (roleAssignment.e != null) {
			List<String> authorizedEntityClassifications = roleAssignment.e.get(getEntityTypeName(entity));
			if (authorizedEntityClassifications != null) {
				return authorizedEntityClassifications.stream().anyMatch(c -> c.equals(ENTITY_CLASSIFIER_ALL_TYPES) || isMatchForExplicitClassifier(c));
			}

		}
		return false;
	}


	protected boolean matchesOrganisation() {
		return true;
	}

	protected boolean matchesAdministrativeZone() {
		return true;
	}

	protected String getEntityTypeName(T entity) {
		return entity.getClass().getSimpleName();
	}

	protected boolean isMatchForExplicitClassifier(String classifier) {
		return false;
	}
}
