package org.rutebanken.tiamat.writer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.entur.oauth2.JwtRoleAssignmentExtractor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.helper.organisation.DataScopedAuthorizationService;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.auth.DefaultAuthorizationService;
import org.rutebanken.tiamat.auth.TiamatEntityResolver;
import org.rutebanken.tiamat.auth.check.TiamatOriganisationChecker;
import org.rutebanken.tiamat.auth.check.TopographicPlaceChecker;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.repository.AsyncStopPlaceJobRepository;
import org.rutebanken.tiamat.writer.async.WriteJobPrincipal;
import org.rutebanken.tiamat.service.groupofstopplaces.GroupOfStopPlacesMembersResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ENTITY_TYPE;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

/**
 * The write is authorized on the processing thread from claims that were captured on the request
 * thread and read back out of the database. This asserts the only thing that ultimately matters:
 * the decision is the same on both sides.
 * <p>
 * Every other authorization test in this codebase runs against
 * {@code MockedRoleAssignmentExtractor}, which returns a canned answer and never looks at the
 * token. That cannot show whether the claims we carry are sufficient, because it would pass just as
 * happily if we carried none of them. So this test wires up the real
 * {@link JwtRoleAssignmentExtractor} instead, leaving the rest of the authorization stack as the
 * application configures it.
 * <p>
 * Deliberately not {@code @Transactional}: the claims have to come back from Postgres as JSON
 * rather than out of the persistence context, or the round trip being tested is not the one that
 * happens in production.
 * <p>
 * Which extractor this covers, and which it does not: the jwt extractor is the default
 * ({@code tiamat.security.role.assignment.extractor}, {@code matchIfMissing = true}) and reads
 * roles out of the token, so carrying the claims is the whole of the problem and the decision can
 * be asserted here. Entur runs with {@code baba} instead, which reads nothing but
 * {@code AuthenticatedUser.of(token)} and then looks the roles up remotely; its decision cannot be
 * reproduced without standing up the permission store, but its one token-derived input is covered
 * by {@link WriteJobPrincipalPersistenceIntegrationTest}.
 */
public class WriteJobPrincipalAuthorizationIntegrationTest extends TiamatIntegrationTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Converts a raw claim set exactly as the decoder does on a real request, so the captured
     * claims start out in production's shape: the issuer a URL, the expiry an Instant.
     */
    private static final MappedJwtClaimSetConverter CLAIM_SET_CONVERTER =
            MappedJwtClaimSetConverter.withDefaults(Map.of());

    @Autowired
    private JobService jobService;

    @Autowired
    private AsyncStopPlaceJobRepository jobRepository;

    @Autowired
    private WriteJobPrincipal principal;

    @Autowired
    private TiamatOriganisationChecker organisationChecker;

    @Autowired
    private TopographicPlaceChecker topographicPlaceChecker;

    @Autowired
    private TiamatEntityResolver entityResolver;

    @Autowired
    private GroupOfStopPlacesMembersResolver groupOfStopPlacesMembersResolver;

    private AuthorizationService authorizationService;

    @Before
    public void useTheRealRoleAssignmentExtractor() {
        RoleAssignmentExtractor realExtractor = new JwtRoleAssignmentExtractor();
        DataScopedAuthorizationService dataScoped = new ReflectionAuthorizationService(
                realExtractor, true, organisationChecker, topographicPlaceChecker, entityResolver, Map.of());
        authorizationService = new DefaultAuthorizationService(
                dataScoped, true, realExtractor, topographicPlaceChecker, groupOfStopPlacesMembersResolver);
    }

    @After
    public void clearContext() {
        SecurityContextHolder.clearContext();
    }

    /**
     * The case that would break silently: the caller is allowed, but a claim the extractor needs
     * did not survive being carried, so the write is refused after the client was told it was
     * accepted.
     */
    @Test
    public void aCallerAllowedOnTheRequestThreadIsStillAllowedAfterTheRoundTrip() {
        StopPlace onstreetBus = onstreetBus();
        authenticateWithRoleAssignment(editStopsFor("onstreetBus"));

        assertThat(authorizationService.canEditEntity(onstreetBus))
                .as("precondition: this caller must be allowed on the request thread, "
                        + "otherwise the assertion after the round trip proves nothing")
                .isTrue();

        assertThat(decideAgainAfterTheRoundTrip(onstreetBus))
                .as("the same caller, reconstituted on the processing thread")
                .isTrue();
    }

    /**
     * The mirror image, and the reason the test above is not enough on its own: if restoring
     * produced a principal that was allowed to do anything, or if the claims were dropped and
     * something downstream defaulted open, only this direction would notice.
     */
    @Test
    public void aCallerDeniedOnTheRequestThreadIsStillDeniedAfterTheRoundTrip() {
        StopPlace onstreetBus = onstreetBus();
        authenticateWithRoleAssignment(editStopsFor("railStation"));

        assertThat(authorizationService.canEditEntity(onstreetBus))
                .as("precondition: this caller must be denied on the request thread")
                .isFalse();

        assertThat(decideAgainAfterTheRoundTrip(onstreetBus))
                .as("restoring a principal must not widen what it may do")
                .isFalse();
    }

    /**
     * Machine clients carry their roles in {@code permissions} rather than {@code role_assignments},
     * which the extractor reads by a different branch and turns into cross-organisation roles. That
     * is also the claim shape the write API exists to serve, so it is worth carrying intact.
     */
    @Test
    public void aMachineClientKeepsItsRolesAcrossTheRoundTrip() {
        authenticateWith(Map.of(
                "sub", "some-client@clients",
                "iss", "https://internal.entur.org/",
                "permissions", List.of(ROLE_EDIT_STOPS)
        ));

        List<RoleAssignment> onRequestThread = rolesFromCurrentContext();

        restoreOnAFreshThreadContext(persistCapturedClaims());

        assertThat(rolesFromCurrentContext())
                .usingRecursiveComparison()
                .as("the machine client's roles must survive being carried")
                .isEqualTo(onRequestThread);
        assertThat(onRequestThread)
                .as("precondition: the extractor must have found roles to begin with")
                .isNotEmpty();
    }

    /**
     * Captures the caller as the endpoint does, stores the job, drops the security context the way
     * a worker in another pod would never have had it, and restores from what came back.
     */
    private boolean decideAgainAfterTheRoundTrip(StopPlace stopPlace) {
        restoreOnAFreshThreadContext(persistCapturedClaims());
        return authorizationService.canEditEntity(stopPlace);
    }

    private Long persistCapturedClaims() {
        Map<String, Object> captured = principal.capture();
        assertThat(captured).as("nothing was captured, so the round trip is vacuous").isNotEmpty();

        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        job.setStatus(AsyncStopPlaceJobStatus.PROCESSING);
        job.setCreatedAt(Instant.now());
        job.setPrincipalClaims(captured);
        return jobRepository.save(job).getId();
    }

    private void restoreOnAFreshThreadContext(Long jobId) {
        SecurityContextHolder.clearContext();
        principal.restore(jobService.principalClaimsFor(jobId));
    }

    private List<RoleAssignment> rolesFromCurrentContext() {
        return new JwtRoleAssignmentExtractor()
                .getRoleAssignmentsForUser(SecurityContextHolder.getContext().getAuthentication());
    }

    private static StopPlace onstreetBus() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        return stopPlace;
    }

    private static RoleAssignment editStopsFor(String stopPlaceType) {
        return RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", stopPlaceType)
                .build();
    }

    /**
     * User tokens carry role assignments as JSON strings, which is the form the extractor parses
     * and the form that has to survive being stored as JSON itself.
     */
    private void authenticateWithRoleAssignment(RoleAssignment roleAssignment) {
        authenticateWith(Map.of(
                "sub", "auth0|alice",
                "iss", "https://internal.entur.org/",
                "preferred_username", "alice",
                "role_assignments", List.of(asJson(roleAssignment))
        ));
    }

    private void authenticateWith(Map<String, Object> rawClaims) {
        Map<String, Object> withExpiry = new HashMap<>(rawClaims);
        withExpiry.put("exp", Instant.now().plusSeconds(600).getEpochSecond());

        Map<String, Object> claims = CLAIM_SET_CONVERTER.convert(withExpiry);
        Jwt jwt = new Jwt("token-value", null, (Instant) claims.get("exp"), Map.of("alg", "none"), claims);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    private static String asJson(RoleAssignment roleAssignment) {
        try {
            return MAPPER.writeValueAsString(roleAssignment);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
