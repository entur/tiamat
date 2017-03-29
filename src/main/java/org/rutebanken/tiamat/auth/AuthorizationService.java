package org.rutebanken.tiamat.auth;

import org.rutebanken.tiamat.model.EntityStructure;

public interface AuthorizationService<T extends EntityStructure> {
	/**
	 * Verify that user is authorized for operation.
	 *
	 * @param requiredRole the name of the role required for the operation.
	 * @param entities     for which authorization is to be verified
	 */
	void assertAuthorized(String requiredRole, T... entities);
}
