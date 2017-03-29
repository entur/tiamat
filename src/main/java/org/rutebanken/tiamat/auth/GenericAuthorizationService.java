package org.rutebanken.tiamat.auth;

import com.vividsolutions.jts.geom.Polygon;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenericAuthorizationService<T extends EntityStructure> implements AuthorizationService<T> {

	public static final String ENTITY_CLASSIFIER_ALL_TYPES = "*";

	private static final Logger logger = LoggerFactory.getLogger(GenericAuthorizationService.class);

	@Value("${authorization.enabled:false}")
	protected boolean authorizationEnabled;

	@Value("${administrative.zone.id.prefix:KVE:TopographicPlace}")
	protected String administrativeZoneIdPrefix;

	@Autowired
	protected TopographicPlaceRepository topographicPlaceRepository;


	public void assertAuthorized(String requiredRole, T... entities) {
		if (!authorizationEnabled) {
			return;
		}

		List<RoleAssignment> relevantRoles = RoleAssignmentExtractor.getRoleAssignmentsForUser().stream().filter(ra -> requiredRole.equals(ra.r)).collect(Collectors.toList());
		boolean allowed = true;
		for (T entity : entities) {
			allowed &= entity == null ||
					           relevantRoles.stream().anyMatch(ra -> isAuthorizationForEntity(entity, ra));
			if (!allowed) {
				throw new AccessDeniedException("Insufficient privileges for operation");
			}
		}

	}

	protected boolean isAuthorizationForEntity(T entity, RoleAssignment roleAssignment) {
		Polygon administrativeZone = null;
		if (roleAssignment.z != null) {
			topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(administrativeZoneIdPrefix + roleAssignment.z);
		}
		AuthorizationTask<T> task = new AuthorizationTask<T>(entity, roleAssignment, administrativeZone);
		return isAllowed(task);
	}

	public boolean isAllowed(AuthorizationTask<T> task) {
		return matchesTypeAndClassifier(task) && matchesOrganisation(task) && matchesAdministrativeZone(task);
	}

	protected boolean matchesTypeAndClassifier(AuthorizationTask<T> task) {
		if (task.getRoleAssignment().e != null) {
			List<String> authorizedEntityClassifications = task.getRoleAssignment().e.get(getEntityTypeName(task.getEntity()));
			if (authorizedEntityClassifications != null) {
				return authorizedEntityClassifications.stream().anyMatch(c -> c.equals(ENTITY_CLASSIFIER_ALL_TYPES) || isMatchForExplicitClassifier(task, c));
			}

		}
		return false;
	}


	protected boolean matchesOrganisation(AuthorizationTask<T> task) {
		return true;
	}

	protected boolean matchesAdministrativeZone(AuthorizationTask<T> task) {
		return true;
	}

	protected String getEntityTypeName(T entity) {
		return entity.getClass().getSimpleName();
	}

	protected boolean isMatchForExplicitClassifier(AuthorizationTask<T> task, String classifier) {
		return false;
	}


}
