package org.rutebanken.tiamat.auth;

import com.vividsolutions.jts.geom.Polygon;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.model.EntityStructure;

public class AuthorizationTask<T extends EntityStructure> {
	private T entity;
	private RoleAssignment roleAssignment;
	private Polygon administrativeZone;

	public AuthorizationTask(T entity, RoleAssignment roleAssignment, Polygon administrativeZone) {
		this.entity = entity;
		this.roleAssignment = roleAssignment;
		this.administrativeZone = administrativeZone;
	}

	public T getEntity() {
		return entity;
	}

	public RoleAssignment getRoleAssignment() {
		return roleAssignment;
	}

	public Polygon getAdministrativeZone() {
		return administrativeZone;
	}
}
