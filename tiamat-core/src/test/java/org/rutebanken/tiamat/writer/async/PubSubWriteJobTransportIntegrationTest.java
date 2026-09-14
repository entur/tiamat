package org.rutebanken.tiamat.writer.async;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.TopicName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.repository.AsyncStopPlaceJobRepository;
import org.rutebanken.tiamat.writer.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PubSubEmulatorContainer;
import org.testcontainers.utility.DockerImageName;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Runs a write job through a real Pub/Sub broker, using the emulator.
 * <p>
 * What this is really testing is the claim the transport seam was built on: that a second
 * transport needs no knowledge of how a job is processed. Nothing here touches authorization,
 * claiming, the write itself or how the outcome is recorded, and none of that had to change to
 * make Pub/Sub work. The assertions are therefore about delivery: a job published on one side
 * arrives on the other, is processed exactly once, and ends up in a terminal state.
 */
@TestPropertySource(properties = {
        "tiamat.write-api.enabled=true",
        "tiamat.write-api.transport=pubsub",
        "tiamat.write-api.pubsub.topic=" + PubSubWriteJobTransportIntegrationTest.TOPIC,
        "tiamat.write-api.pubsub.subscription=" + PubSubWriteJobTransportIntegrationTest.SUBSCRIPTION,
        "spring.cloud.gcp.pubsub.project-id=" + PubSubWriteJobTransportIntegrationTest.PROJECT
})
public class PubSubWriteJobTransportIntegrationTest extends TiamatIntegrationTest {

    static final String PROJECT = "tiamat-test";
    static final String TOPIC = "write-jobs";
    static final String SUBSCRIPTION = "write-jobs-sub";

    /**
     * Pinned. The {@code :emulators} tag is one Google republishes, so a push there changes the
     * emulator under a build that did not change, and a machine holding an older layer does not
     * reproduce it.
     */
    private static final PubSubEmulatorContainer EMULATOR = new PubSubEmulatorContainer(
            DockerImageName.parse("gcr.io/google.com/cloudsdktool/google-cloud-cli:581.0.0-emulators")
                    .asCompatibleSubstituteFor("gcr.io/google.com/cloudsdktool/google-cloud-cli"));

    private static final byte[] CREATE_PAYLOAD = ("""
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="1">
                    <Name>Delivered By Pub Sub</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """).getBytes(StandardCharsets.UTF_8);

    @Autowired
    private WriteJobPublisher publisher;

    @Autowired
    private AsyncStopPlaceJobRepository jobRepository;

    @Autowired
    private PubSubWriteJobSubscriber subscriber;

    @Autowired
    private JobService jobService;

    private static final MappedJwtClaimSetConverter CLAIM_SET_CONVERTER =
            MappedJwtClaimSetConverter.withDefaults(Map.of());

    private static final String SUBMITTER = "alice";

    @After
    public void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @BeforeClass
    public static void startBrokerAndCreateTopic() throws Exception {
        EMULATOR.start();
        // The emulator starts empty. A publish to a topic that does not exist fails, so the topic
        // and subscription have to exist before the application's subscriber starts.
        ManagedChannel channel = ManagedChannelBuilder.forTarget(EMULATOR.getEmulatorEndpoint())
                .usePlaintext()
                .build();
        try {
            TransportChannelProvider channelProvider =
                    FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));

            try (TopicAdminClient topicAdmin = TopicAdminClient.create(TopicAdminSettings.newBuilder()
                    .setTransportChannelProvider(channelProvider)
                    .setCredentialsProvider(NoCredentialsProvider.create())
                    .build())) {
                topicAdmin.createTopic(TopicName.of(PROJECT, TOPIC));
            }

            try (SubscriptionAdminClient subscriptionAdmin = SubscriptionAdminClient.create(
                    SubscriptionAdminSettings.newBuilder()
                            .setTransportChannelProvider(channelProvider)
                            .setCredentialsProvider(NoCredentialsProvider.create())
                            .build())) {
                subscriptionAdmin.createSubscription(
                        ProjectSubscriptionName.of(PROJECT, SUBSCRIPTION),
                        TopicName.of(PROJECT, TOPIC),
                        PushConfig.getDefaultInstance(),
                        10);
            }
        } finally {
            channel.shutdown();
        }
    }

    @DynamicPropertySource
    static void pointTheClientAtTheEmulator(DynamicPropertyRegistry registry) {
        // One registration. Relaxed binding means emulator-host and emulatorHost are the same
        // property, and two lines that set one value invite an edit to one of them.
        registry.add("spring.cloud.gcp.pubsub.emulator-host", EMULATOR::getEmulatorEndpoint);
    }

    @Test
    public void theSubscriberRunsOnceTheContextIsReady() {
        assertThat(subscriber.isRunning())
                .as("a lifecycle that never started consumes nothing, and every job times out")
                .isTrue();
        assertThat(subscriber.subscriberState()).contains(com.google.api.core.ApiService.State.RUNNING);
    }

    @Test
    public void aJobPublishedToTheBrokerIsProcessedAndCompleted() {
        Long jobId = acceptedJob();

        publisher.publish(WriteJobMessage.create(jobId, CREATE_PAYLOAD));

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                assertThat(statusOf(jobId))
                        .as("the job should have been delivered, processed and completed")
                        .isEqualTo(AsyncStopPlaceJobStatus.FINISHED));

        assertThat(jobRepository.findById(jobId).orElseThrow().getWrittenStopPlaces())
                .as("processing a create mints a stop place id and reports it")
                .hasSize(1);
    }

    /**
     * The one risk a cross-process transport adds. The claims are captured on the request thread,
     * cross the broker on the job row, and are reinstated on a subscriber thread that never saw
     * the caller. Nothing else in this suite crosses a process boundary with a principal.
     * <p>
     * The security context is cleared before the publish, so the subscriber thread has nothing to
     * inherit. What attributes the write is what came back out of the database.
     */
    @Test
    public void theSubmittersPrincipalCrossesTheBrokerAndAttributesTheWrite() {
        Long jobId = acceptedJob();
        SecurityContextHolder.clearContext();

        publisher.publish(WriteJobMessage.create(jobId, CREATE_PAYLOAD));

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                assertThat(statusOf(jobId)).isEqualTo(AsyncStopPlaceJobStatus.FINISHED));

        String netexId = jobRepository.findById(jobId).orElseThrow()
                .getWrittenStopPlaces().getFirst().netexId();

        assertThat(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(netexId).getChangedBy())
                .as("a write with no principal is attributed to nobody")
                .isEqualTo(SUBMITTER);
    }

    /**
     * Delivery is at least once, so the same message can arrive twice. Processing is not
     * idempotent: a create mints a fresh id each run, so a second delivery that was actually
     * processed would produce a duplicate stop place. Claiming is what prevents that, and this
     * asserts the transport does not undo it.
     */
    @Test
    public void aRedeliveredJobIsNotProcessedTwice() {
        Long jobId = acceptedJob();
        WriteJobMessage message = WriteJobMessage.create(jobId, CREATE_PAYLOAD);

        publisher.publish(message);
        await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                assertThat(statusOf(jobId)).isEqualTo(AsyncStopPlaceJobStatus.FINISHED));

        long stopPlacesAfterFirstDelivery = stopPlaceRepository.count();
        publisher.publish(message);

        // Nothing observable happens on a discarded delivery, so there is no state change to
        // await. Give the subscriber time to receive it and prove it changed nothing.
        await().pollDelay(Duration.ofSeconds(3)).atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(stopPlaceRepository.count())
                        .as("a redelivery must be discarded, not applied a second time")
                        .isEqualTo(stopPlacesAfterFirstDelivery));
        assertThat(statusOf(jobId)).isEqualTo(AsyncStopPlaceJobStatus.FINISHED);
    }

    /**
     * Built through {@link JobService#createJob}, and not by hand. A job row assembled in the test
     * carries no principal claims, so {@code WriteJobPrincipal.restore} takes its early return and
     * the write runs with no security context at all. Every assertion here then holds for a
     * transport that lost the principal.
     */
    private Long acceptedJob() {
        authenticateAsSubmitter();
        return jobService.createJob().getId();
    }

    private void authenticateAsSubmitter() {
        Map<String, Object> rawClaims = new HashMap<>(Map.of(
                "sub", "auth0|" + SUBMITTER,
                "iss", "https://internal.entur.org/",
                "preferred_username", SUBMITTER,
                "exp", Instant.now().plusSeconds(600).getEpochSecond()
        ));
        Map<String, Object> claims = CLAIM_SET_CONVERTER.convert(rawClaims);
        Jwt jwt = new Jwt("token-value", null, (Instant) claims.get("exp"), Map.of("alg", "none"), claims);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    private AsyncStopPlaceJobStatus statusOf(Long jobId) {
        return jobRepository.findById(jobId).orElseThrow().getStatus();
    }
}
