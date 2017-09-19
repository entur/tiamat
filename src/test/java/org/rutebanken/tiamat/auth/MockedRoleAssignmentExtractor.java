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
