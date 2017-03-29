package org.rutebanken.tiamat.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoleAssignmentExtractor {

	private static final String ATTRIBUTE_NAME_ROLE_ASSIGNMENT = "roles";
	private static ObjectMapper mapper = new ObjectMapper();

	public static List<RoleAssignment> getRoleAssignmentsForUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth instanceof KeycloakAuthenticationToken) {
			KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) auth.getPrincipal();
			AccessToken token = principal.getKeycloakSecurityContext().getToken();
			List<Map> rolesAsMaps = (List) token.getOtherClaims().get(ATTRIBUTE_NAME_ROLE_ASSIGNMENT);
			return rolesAsMaps.stream().map(m -> mapper.convertValue(m, RoleAssignment.class)).collect(Collectors.toList());

		}
		return new ArrayList<>();
	}

}
