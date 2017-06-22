package org.rutebanken.tiamat.auth;

import com.vividsolutions.jts.geom.Polygon;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.tiamat.auth.check.AuthorizationCheckFactory;
import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenericAuthorizationService implements AuthorizationService {

	private static final Logger logger = LoggerFactory.getLogger(GenericAuthorizationService.class);

	@Value("${authorization.enabled:true}")
	protected boolean authorizationEnabled;

	@Value("${administrative.zone.id.prefix:KVE:TopographicPlace}")
	protected String administrativeZoneIdPrefix;

	@Autowired
	protected TopographicPlaceRepository topographicPlaceRepository;

	@Autowired
	private AuthorizationCheckFactory authorizationCheckFactory;

	@Autowired
	private RoleAssignmentExtractor roleAssignmentExtractor;

	@Override
	public void assertAuthorized(String requiredRole, Collection<? extends EntityStructure> entities) {

		final boolean allowed = isAuthorized(requiredRole, entities);
		if (!allowed) {
			throw new AccessDeniedException("Insufficient privileges for operation");
		}
	}

	@Override
	public void assertAuthorized(String requiredRole, EntityStructure... entities) {
		assertAuthorized(requiredRole, Arrays.asList(entities));
	}

	@Override
	public boolean isAuthorized(String requiredRole, EntityStructure... entities) {
		return isAuthorized(requiredRole, Arrays.asList(entities));
	}

	@Override
	public boolean isAuthorized(String requiredRole, Collection<? extends EntityStructure> entities) {
		if (!authorizationEnabled) {
			return true;
		}

		List<RoleAssignment> relevantRoles = roleAssignmentExtractor.getRoleAssignmentsForUser().stream().filter(ra -> requiredRole.equals(ra.r)).collect(Collectors.toList());
		boolean allowed = true;
		for (EntityStructure entity : entities) {
			allowed &= entity == null ||
					relevantRoles.stream().anyMatch(ra -> isAuthorizationForEntity(entity, ra));

		}
		return allowed;
	}

	protected boolean isAuthorizationForEntity(EntityStructure entity, RoleAssignment roleAssignment) {
		Polygon administrativeZone = null;
		if (roleAssignment.z != null) {
			topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(administrativeZoneIdPrefix + roleAssignment.z);
		}
		return authorizationCheckFactory.buildCheck(entity, roleAssignment, administrativeZone).isAllowed();
	}


}
