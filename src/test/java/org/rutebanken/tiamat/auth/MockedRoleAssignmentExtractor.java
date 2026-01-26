/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.auth;

import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * For assigned user roles in integration tests. Defaults to full access.
 * By default, the mock resets after each call. Use {@link #setPersistent(boolean)}
 * to keep the role assignment across multiple calls within the same test.
 */
@Service
@Primary
public class MockedRoleAssignmentExtractor implements RoleAssignmentExtractor {

	private List<RoleAssignment> nextReturnedRoleAssignmentList;
	private boolean persistent = false;


	@Override
	public List<RoleAssignment> getRoleAssignmentsForUser() {
		List<RoleAssignment> returnValue = nextReturnedRoleAssignmentList;

		if (returnValue == null) {
			returnValue = RoleAssignmentListBuilder.builder().withAccessAllAreas().build();
		}

		if (!persistent) {
			nextReturnedRoleAssignmentList = null;
		}
		return returnValue;

	}

	@Override
	public List<RoleAssignment> getRoleAssignmentsForUser(Authentication authentication) {
		return getRoleAssignmentsForUser();
	}

	public void setNextReturnedRoleAssignment(List<RoleAssignment> nextReturnedRoleAssignmentList) {
		this.nextReturnedRoleAssignmentList = nextReturnedRoleAssignmentList;
	}

	public void setNextReturnedRoleAssignment(RoleAssignment roleAssignment) {
		this.nextReturnedRoleAssignmentList = Collections.singletonList(roleAssignment);
	}

	/**
	 * When persistent is true, the role assignment will not reset after each call.
	 * This is useful for tests where methods internally call getRoleAssignmentsForUser() multiple times.
	 * Remember to call {@link #reset()} after the test to restore default behavior.
	 */
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	/**
	 * Resets the mock to its default state: clears role assignments and disables persistent mode.
	 */
	public void reset() {
		this.nextReturnedRoleAssignmentList = null;
		this.persistent = false;
	}
}