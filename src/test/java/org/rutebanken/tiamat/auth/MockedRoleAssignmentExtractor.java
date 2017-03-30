package org.rutebanken.tiamat.auth;

import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.auth.check.AuthorizationCheck;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * For assigned user roles in integration tests
 */
@Service
public class MockedRoleAssignmentExtractor implements RoleAssignmentExtractor {

	private List<RoleAssignment> roleAssignments;

	public MockedRoleAssignmentExtractor() {
		accessAllAreas();
	}

	@Override
	public List<RoleAssignment> getRoleAssignmentsForUser() {
		return roleAssignments;
	}

	public void accessAllAreas() {
		RoleAssignment allStopPlaceAccess = RoleAssignment.builder().withRole(AuthorizationConstants.ROLE_EDIT_STOPS)
				                                    .withOrganisation("NOT_YET_CHECKED")
				                                    .withEntityClassification("StopPlace",AuthorizationConstants.ENTITY_CLASSIFIER_ALL_TYPES).build();

		roleAssignments = Arrays.asList(allStopPlaceAccess);
	}

	public void setRoleAssignments(List<RoleAssignment> roleAssignments) {
		this.roleAssignments = roleAssignments;
	}


}
