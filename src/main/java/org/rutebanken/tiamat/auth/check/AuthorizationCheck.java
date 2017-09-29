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

package org.rutebanken.tiamat.auth.check;

import com.vividsolutions.jts.geom.Polygon;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.model.EntityStructure;
import java.util.List;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ENTITY_CLASSIFIER_ALL_TYPES;

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
