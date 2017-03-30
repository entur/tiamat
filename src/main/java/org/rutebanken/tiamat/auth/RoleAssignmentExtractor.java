package org.rutebanken.tiamat.auth;

import org.rutebanken.helper.organisation.RoleAssignment;

import java.util.List;


public interface RoleAssignmentExtractor {

	/**
	 * Extract role assignments for user from security context.
	 *
	 * @return
	 */
	List<RoleAssignment> getRoleAssignmentsForUser();

}
