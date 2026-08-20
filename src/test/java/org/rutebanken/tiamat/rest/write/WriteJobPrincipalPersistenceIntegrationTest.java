package org.rutebanken.tiamat.rest.write;

import org.entur.ror.permission.AuthenticatedUser;
import org.junit.After;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.repository.AsyncStopPlaceJobRepository;
import org.rutebanken.tiamat.rest.write.async.WriteJobPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The principal is stored as JSON and read back before the write is authorized, so the claims have
 * to survive that round trip in the shape their consumers expect.
 * <p>
 * This is not covered by testing capture and restore in memory: JSON has no integer width, so a
 * value written as a long can come back as something narrower, and
 * {@code AuthenticatedUser.of} casts the organisation id to {@code Long} without checking.
 */
public class WriteJobPrincipalPersistenceIntegrationTest extends TiamatIntegrationTest {

    @Autowired
    private JobService jobService;

    @Autowired
    private AsyncStopPlaceJobRepository jobRepository;

    @Autowired
    private WriteJobPrincipal principal;

    @After
    public void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void claimsSurviveTheRoundTripInTheShapeTheirConsumersExpect() {
        Long jobId = persistJobWithClaims(Map.of(
                "sub", "some-client@clients",
                "iss", "https://internal.entur.org/",
                "https://entur.io/organisationID", 1L,
                "permissions", List.of("editStops"),
                "preferred_username", "alice"
        ));

        Map<String, Object> readBack = jobService.principalClaimsFor(jobId);
        principal.restore(readBack);

        var token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        AuthenticatedUser user = AuthenticatedUser.of(token);

        assertThat(user.subject()).isEqualTo("some-client@clients");
        assertThat(user.isClient()).isTrue();
        assertThat(user.isInternal()).isTrue();
        assertThat(user.organisationId())
                .as("the organisation id is cast to Long by the permission store client")
                .isEqualTo(1L);
        assertThat(user.permissions()).containsExactly("editStops");
    }

    /**
     * Expiry is stored as epoch seconds and enforced on restore, so it also has to survive intact.
     */
    @Test
    public void expiryStillBoundsCredentialsAfterTheRoundTrip() {
        Long jobId = persistJobWithClaims(Map.of(
                "sub", "auth0|alice",
                "exp", Instant.now().plusSeconds(600).getEpochSecond()
        ));

        principal.restore(jobService.principalClaimsFor(jobId));

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    private Long persistJobWithClaims(Map<String, Object> claims) {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        job.setStatus(AsyncStopPlaceJobStatus.PROCESSING);
        job.setCreatedAt(Instant.now());
        job.setPrincipalClaims(claims);
        // Saved and read back in separate transactions, so the read genuinely comes from Postgres
        // rather than from the persistence context.
        return jobRepository.save(job).getId();
    }
}
