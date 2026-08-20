package org.rutebanken.tiamat.rest.write;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.repository.AsyncStopPlaceJobRepository;
import org.rutebanken.tiamat.rest.write.async.WriteJobHandler;
import org.rutebanken.tiamat.rest.write.async.WriteJobMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Who the write is performed as, once it is the broker path performing it.
 * <p>
 * {@code StopPlaceControllerAuthorizationTest} already covers an unauthorized write through this
 * path failing the job, but it does so against {@code MockedRoleAssignmentExtractor}, which returns
 * a canned answer and never looks at the token. Those tests therefore pass unchanged whether the
 * submitter is reinstated correctly, reinstated as somebody else, or never reinstated at all.
 * <p>
 * What is asserted here instead is the identity the write actually ran under, observed through
 * production code: {@code StopPlaceVersionedSaverService} stamps {@code changedBy} from
 * {@link org.rutebanken.tiamat.auth.UsernameFetcher}, which reads the security context of the
 * thread doing the write. So the persisted stop place records which principal was live on the
 * worker, and that is a fact no mocked extractor can fake.
 * <p>
 * Both jobs are handled on one thread on purpose. Worker threads are pooled, so consecutive jobs
 * genuinely do share a thread, and a principal left behind by the previous job would be inherited
 * by the next one: the write would be attributed to, and authorized as, the wrong caller.
 */
public class WriteJobBrokerPathPrincipalIntegrationTest extends TiamatIntegrationTest {

    private static final byte[] CREATE_PAYLOAD = ("""
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="1">
                    <Name>Attributed Stop</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """).getBytes(StandardCharsets.UTF_8);

    @Autowired
    private JobService jobService;

    @Autowired
    private AsyncStopPlaceJobRepository jobRepository;

    @Autowired
    private WriteJobHandler handler;

    @Before
    public void clearJobs() {
        jobRepository.deleteAll();
    }

    @After
    public void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void theWriteIsAttributedToTheCallerThatSubmittedIt() {
        Long jobId = submitAs("alice");

        handleOnAWorkerThread(jobId);

        assertThat(statusOf(jobId)).isEqualTo(AsyncStopPlaceJobStatus.FINISHED);
        assertThat(changedByOfStopPlaceCreatedBy(jobId)).isEqualTo("alice");
    }

    /**
     * The case the {@code finally} in the handler exists for. If the previous caller were left
     * installed, this write would be attributed to alice while bob is the one who asked for it.
     */
    @Test
    public void aSecondJobIsNotAttributedToThePreviousCaller() {
        Long alicesJob = submitAs("alice");
        Long bobsJob = submitAs("bob");

        handleOnAWorkerThread(alicesJob);
        handleOnAWorkerThread(bobsJob);

        assertThat(changedByOfStopPlaceCreatedBy(alicesJob))
                .as("precondition: the first write must be attributed to its own submitter")
                .isEqualTo("alice");
        assertThat(changedByOfStopPlaceCreatedBy(bobsJob))
                .as("the second write must be attributed to bob, not to whoever ran before him")
                .isEqualTo("bob");
    }

    /**
     * Nothing carried, as when authorization is disabled, must not leave the previous caller in
     * place either. Attribution being empty is the honest outcome; inheriting alice is not.
     */
    @Test
    public void aJobThatCarriedNoPrincipalIsNotAttributedToThePreviousCaller() {
        Long alicesJob = submitAs("alice");
        Long anonymousJob = submitWithNoAuthentication();

        handleOnAWorkerThread(alicesJob);
        handleOnAWorkerThread(anonymousJob);

        assertThat(changedByOfStopPlaceCreatedBy(alicesJob))
                .as("precondition: the first write must be attributed to its own submitter")
                .isEqualTo("alice");
        assertThat(changedByOfStopPlaceCreatedBy(anonymousJob))
                .as("an unattributed write must not borrow the previous caller's identity")
                .isNotEqualTo("alice");
    }

    /**
     * Submits the way the endpoint does, on a thread that carries the caller, and then leaves the
     * thread as a worker would find it: with no security context of its own.
     */
    private Long submitAs(String username) {
        authenticateAs(username);
        Long jobId = jobService.createJob().getId();
        SecurityContextHolder.clearContext();
        return jobId;
    }

    private Long submitWithNoAuthentication() {
        SecurityContextHolder.clearContext();
        return jobService.createJob().getId();
    }

    /**
     * The handler is what a transport calls, so this is the broker path from the seam inwards:
     * claim, reinstate the submitter, write, complete.
     */
    private void handleOnAWorkerThread(Long jobId) {
        handler.handle(WriteJobMessage.create(jobId, CREATE_PAYLOAD));
    }

    private void authenticateAs(String username) {
        Jwt jwt = new Jwt(
                "token-value",
                null,
                Instant.now().plusSeconds(600),
                Map.of("alg", "none"),
                Map.of(
                        "sub", "auth0|" + username,
                        "iss", "https://internal.entur.org/",
                        "preferred_username", username
                )
        );
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    private AsyncStopPlaceJobStatus statusOf(Long jobId) {
        return jobRepository.findById(jobId).orElseThrow().getStatus();
    }

    private String changedByOfStopPlaceCreatedBy(Long jobId) {
        AsyncStopPlaceJob job = jobRepository.findById(jobId).orElseThrow();
        assertThat(job.getCreatedIds())
                .as("the job must have created a stop place for there to be anything to attribute")
                .hasSize(1);
        String netexId = job.getCreatedIds().getFirst().createdId();
        return stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(netexId).getChangedBy();
    }
}
