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
	 * Same as {$link {@link AuthorizationService#assertAuthorized(String, EntityStructure...)}, but return true or false
	 * @return whether the current user is authorized with the given role for the list of entities
	 */
	boolean isAuthorized(String requiredRole, EntityStructure... entities);

	/**
	 * Verify that user is authorized for operation.
	 *
	 * @param requiredRole the name of the role required for the operation.
	 * @param entities     for which authorization is to be verified
	 */
	void assertAuthorized(String requiredRole, Collection<? extends EntityStructure> entities);

	/**
	 * Same as {$link {@link AuthorizationService#assertAuthorized(String, Collection)}, but will return true or false
	 * @param requiredRole
	 * @param entities
	 * @return whether the current user is authorized with the given role for the list of entities
	 */
	boolean isAuthorized(String requiredRole, Collection<? extends EntityStructure> entities);
}
