package org.rutebanken.tiamat.rest.write.async;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The processing unit runs on a different thread, and eventually in a different pod, from the
 * request that accepted the write. Authorization and the audit trail both read the principal from
 * SecurityContextHolder, so it has to be carried across that boundary explicitly.
 */
class WriteJobPrincipalTest {

    private final WriteJobPrincipal principal = new WriteJobPrincipal();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Both role assignment extractors read from the same token and differ only in which claims
     * they use, so capturing the union serves either without branching on which is configured.
     */
    @Test
    void capturesEveryClaimNeededToResolveRolesAndAttributeTheChange() {
        authenticateWith(Map.of(
                "sub", "auth0|alice",
                "iss", "https://ror-entur-dev.eu.auth0.com/",
                "role_assignments", List.of("{\"r\":\"editStops\"}"),
                "permissions", List.of("editStops"),
                "https://entur.io/organisationID", 1L,
                "preferred_username", "alice"
        ));

        Map<String, Object> captured = principal.capture();

        assertThat(captured)
                .containsKeys("sub", "iss", "role_assignments", "permissions",
                        "https://entur.io/organisationID",
                        "preferred_username");
    }

    @Test
    void capturesNothingWhenThereIsNoAuthentication() {
        assertThat(principal.capture()).isEmpty();
    }

    @Test
    void restoreInstallsAnAuthenticationCarryingTheClaims() {
        principal.restore(Map.of("sub", "auth0|alice", "iss", "https://issuer/"));

        var authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getToken().getClaimAsString("sub")).isEqualTo("auth0|alice");
    }

    /**
     * Authorization disabled means nothing was captured, and restoring must then leave the context
     * empty rather than fabricate a principal.
     */
    @Test
    void restoreOfNothingLeavesTheContextEmpty() {
        principal.restore(Map.of());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    /**
     * Persisting claims turns a short lived credential into one sitting in the database. Under the
     * jwt extractor the claims are the authorization, and nothing re-checks them, so expiry is the
     * only bound on how long they stay usable.
     */
    @Test
    void restoreRejectsCredentialsThatHaveExpired() {
        Map<String, Object> expired = Map.of(
                "sub", "auth0|alice",
                "exp", Instant.now().minusSeconds(60).getEpochSecond()
        );

        assertThatThrownBy(() -> principal.restore(expired))
                .isInstanceOf(WriteJobCredentialsExpiredException.class);
    }

    @Test
    void restoreAcceptsCredentialsThatAreStillValid() {
        Map<String, Object> valid = Map.of(
                "sub", "auth0|alice",
                "exp", Instant.now().plusSeconds(600).getEpochSecond()
        );

        principal.restore(valid);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    void clearRemovesTheAuthentication() {
        principal.restore(Map.of("sub", "auth0|alice"));

        principal.clear();

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    private static void authenticateWith(Map<String, Object> claims) {
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "none"), claims);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }
}
