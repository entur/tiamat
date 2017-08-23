package org.rutebanken.tiamat.auth;

import org.keycloak.KeycloakPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UsernameFetcher {

    /**
     * Gets username from Spring Security
     * <p>
     * Expects property keycloak.principal-attribute=preferred_username
     */
    public String getUserNameForAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() != null &&
                    authentication.getPrincipal() instanceof KeycloakPrincipal) {
                return ((KeycloakPrincipal) authentication.getPrincipal()).getName();
            }
        }
        return null;
    }
}
