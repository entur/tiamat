package org.rutebanken.tiamat.auth;

import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * For assigned user roles in integration tests. Defaults to full access.
 */
@Service
public class MockedRoleAssignmentExtractor implements RoleAssignmentExtractor {

	private List<RoleAssignment> nextReturnedRoleAssignmentList;


	@Override
	public List<RoleAssignment> getRoleAssignmentsForUser() {
		List<RoleAssignment> ret = nextReturnedRoleAssignmentList;
		if (ret == null) {
			ret = RoleAssignmentListBuilder.builder().withAccessAllAreas().build();
		}

		nextReturnedRoleAssignmentList = null;
		return ret;

	}

	@Override
	public List<RoleAssignment> getRoleAssignmentsForUser(Authentication authentication) {
		return getRoleAssignmentsForUser();
	}

	public void setNextReturnedRoleAssignmentList(List<RoleAssignment> nextReturnedRoleAssignmentList) {
		this.nextReturnedRoleAssignmentList = nextReturnedRoleAssignmentList;
	}
}
