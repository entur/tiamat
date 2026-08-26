package org.rutebanken.tiamat.writer.async;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.auth.DefaultAuthorizationClaims;
import org.entur.oauth2.JwtRoleAssignmentExtractor;
import org.entur.ror.permission.AuthenticatedUser;
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

    private final WriteJobPrincipal principal = new WriteJobPrincipal(new DefaultAuthorizationClaims());

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

    /**
     * The whole premise is that a restored token is indistinguishable, to the code that consumes
     * it, from the one the caller presented. Asserting that against the real permission store
     * client rather than against our own idea of it: AuthenticatedUser.of reads the issuer as a
     * URL and casts the organisation id to Long, and its constructor rejects an incomplete
     * identity, so any of those would fail here rather than in production.
     */
    @Test
    void restoredTokenSatisfiesThePermissionStoreClient() {
        principal.restore(Map.of(
                "sub", "some-client@clients",
                "iss", "https://internal.entur.org/",
                "https://entur.io/organisationID", 1L,
                "permissions", List.of("editStops")
        ));

        var token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        AuthenticatedUser user = AuthenticatedUser.of(token);

        assertThat(user.subject()).isEqualTo("some-client@clients");
        assertThat(user.isClient()).isTrue();
        assertThat(user.isInternal()).isTrue();
        assertThat(user.organisationId()).isEqualTo(1L);
    }

    /**
     * The jwt extractor is the other consumer, and reads roles from the token itself rather than
     * looking them up, so a restored token has to carry them.
     */
    @Test
    void restoredTokenSatisfiesTheJwtRoleAssignmentExtractor() {
        principal.restore(Map.of(
                "sub", "auth0|alice",
                "iss", "https://issuer/",
                "role_assignments", List.of("{\"r\":\"editStops\",\"o\":\"OST\"}")
        ));

        var roles = new JwtRoleAssignmentExtractor()
                .getRoleAssignmentsForUser(SecurityContextHolder.getContext().getAuthentication());

        assertThat(roles).hasSize(1);
        assertThat(roles.getFirst().getRole()).isEqualTo("editStops");
    }

    @Test
    void clearRemovesTheAuthentication() {
        principal.restore(Map.of("sub", "auth0|alice"));

        principal.clear();

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    /**
     * A deployment carries whatever its own authorization reads. Nothing here knows the Entur claim
     * names, which is the point: an authorization service that reads something else is served by
     * declaring a bean, not by editing this class.
     */
    @Test
    void carriesTheClaimsTheDeploymentNames() {
        WriteJobPrincipal deploymentPrincipal =
                new WriteJobPrincipal(() -> List.of("https://fintraffic.fi/businessId"));

        authenticateWith(Map.of(
                "sub", "trivore|alice",
                "https://fintraffic.fi/businessId", "1234567-8",
                "https://entur.io/organisationID", 1L
        ));

        assertThat(deploymentPrincipal.capture())
                .containsKeys("sub", "https://fintraffic.fi/businessId")
                .as("a claim this deployment's authorization does not read is not stored")
                .doesNotContainKey("https://entur.io/organisationID");
    }

    /**
     * Empty is a correct answer, not a gap: an authorization service that resolves roles from the
     * subject alone, as Trivore does through remote lookups, needs no claim carried but the
     * standard ones.
     */
    @Test
    void carriesTheStandardClaimsEvenWhenTheDeploymentNamesNone() {
        WriteJobPrincipal deploymentPrincipal = new WriteJobPrincipal(List::of);

        Instant expiry = Instant.now().plusSeconds(3600);
        authenticateWith(Map.of(
                "sub", "trivore|alice",
                "iss", "https://login.trivore.com/",
                "exp", expiry,
                "role_assignments", List.of("{\"r\":\"editStops\"}")
        ));

        Map<String, Object> captured = deploymentPrincipal.capture();

        assertThat(captured)
                .as("identity and expiry are mechanism, so they are carried regardless")
                .containsKeys("sub", "iss", "exp")
                .doesNotContainKey("role_assignments");

        deploymentPrincipal.restore(captured);
        assertThat(deploymentPrincipal.currentSubject()).isEqualTo("trivore|alice");
    }

    private static void authenticateWith(Map<String, Object> claims) {
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600),
                Map.of("alg", "none"), claims);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }
}
