package org.rutebanken.tiamat.auth;

import org.rutebanken.tiamat.model.EntityStructure;

import java.util.Collection;

public interface AuthorizationService {
	/**
	 * Verify that user is authorized for operation.
	 *
	 * @param requiredRole the name of the role required for the operation.
	 * @param entities     for which authorization is to be verified
	 */
	void assertAuthorized(String requiredRole, EntityStructure... entities);

	/**
	 * Verify that user is authorized for operation.
	 *
	 * @param requiredRole the name of the role required for the operation.
	 * @param entities     for which authorization is to be verified
	 */
	void assertAuthorized(String requiredRole, Collection<? extends EntityStructure> entities);
}
