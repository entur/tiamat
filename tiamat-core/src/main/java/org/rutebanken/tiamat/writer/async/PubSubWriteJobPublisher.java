package org.rutebanken.tiamat.writer.async;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import jakarta.annotation.PreDestroy;
import org.rutebanken.tiamat.writer.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Publishes a write job to Pub/Sub.
 * <p>
 * The payload travels as the message body, and the job id and the operation as attributes, so
 * nothing reads the payload to route the message. That keeps the property
 * {@link org.rutebanken.tiamat.writer.AsyncStopPlaceWriter} establishes: no thread reads a payload
 * until the job runs.
 * <p>
 * Runs the call to the broker on a dedicated, bounded pool rather than the request thread, the
 * same shape {@link InMemoryWriteJobPublisher} already uses for its own transport. Pub/Sub itself
 * has no trouble with concurrency, but a request thread that waited for the broker would come from
 * the pool that also serves unrelated GraphQL and REST traffic, and a slow or degraded broker
 * would then be free to starve that traffic too.
 * <p>
 * The request thread learns only that the broker call was accepted for publishing, not that the
 * broker took the message. A worker that gets a definite refusal records the failure itself, the
 * way {@link DefaultWriteJobHandler} already records the outcome of the write it performs. A
 * worker that cannot tell whether the broker has the message leaves the job PROCESSING for
 * {@link WriteJobTimeoutSweeper}, the same as {@link InMemoryWriteJobPublisher} leaves a job
 * whose outcome its own worker cannot yet report.
 */
@Component
@Conditional(OnPubSubWriteTransport.class)
public class PubSubWriteJobPublisher implements WriteJobPublisher {

    private static final Logger logger = LoggerFactory.getLogger(PubSubWriteJobPublisher.class);

    static final String ATTRIBUTE_JOB_ID = "jobId";
    static final String ATTRIBUTE_OPERATION = "operation";

    private final PubSubTemplate pubSubTemplate;
    private final JobService jobService;
    private final String topic;
    private final long publishTimeoutSeconds;
    private final ThreadPoolTaskExecutor publishExecutor;

    public PubSubWriteJobPublisher(
            PubSubTemplate pubSubTemplate,
            JobService jobService,
            @Value("${tiamat.write-api.pubsub.topic:tiamat-write-jobs}") String topic,
            @Value("${tiamat.write-api.pubsub.publish-timeout-seconds:10}") long publishTimeoutSeconds,
            @Value("${tiamat.write-api.pubsub.max-concurrent-publishes:50}") int maxConcurrentPublishes
    ) {
        this.pubSubTemplate = pubSubTemplate;
        this.jobService = jobService;
        this.topic = topic;
        this.publishTimeoutSeconds = publishTimeoutSeconds;
        this.publishExecutor = buildExecutor(maxConcurrentPublishes);
    }

    private static ThreadPoolTaskExecutor buildExecutor(int maxConcurrentPublishes) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(maxConcurrentPublishes);
        executor.setMaxPoolSize(maxConcurrentPublishes);
        // No waiting room: once every worker is busy, the next publish is rejected immediately
        // rather than queued, so a caller finds out right away rather than after a further wait.
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("PubSubPublisher-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

    @PreDestroy
    void shutdown() {
        publishExecutor.shutdown();
    }

    @Override
    public void publish(WriteJobMessage message) {
        try {
            publishExecutor.execute(() -> publishToBroker(message));
        } catch (RejectedExecutionException e) {
            // The same shape of rejection the in-memory transport reports for its own bound, so
            // JobService classifies it the same way: QUEUE_FULL, safe to resubmit unchanged.
            throw new WriteJobRejectedException("Too many write jobs are already waiting on Pub/Sub.", e);
        }
    }

    private void publishToBroker(WriteJobMessage message) {
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .setData(ByteString.copyFrom(message.payload()))
                .putAllAttributes(Map.of(
                        ATTRIBUTE_JOB_ID, String.valueOf(message.jobId()),
                        ATTRIBUTE_OPERATION, message.operation().name()))
                .build();

        try {
            String messageId = pubSubTemplate.publish(topic, pubsubMessage)
                    .get(publishTimeoutSeconds, TimeUnit.SECONDS);
            logger.debug("Published write job {} to {} as message {}", message.jobId(), topic, messageId);
        } catch (TimeoutException e) {
            // The message can still reach the broker, so this leaves the job PROCESSING. Failing
            // it here can deny a write that happened. If the message does arrive, that is false,
            // and the caller resubmits and gets a second stop place.
            logger.warn("Publishing write job {} to {} took longer than {}s. The job stays "
                            + "PROCESSING until it completes or times out.",
                    message.jobId(), topic, publishTimeoutSeconds, e);
        } catch (InterruptedException e) {
            // An interrupt says nothing about whether the broker has the message, the same as a
            // timeout. Failing the job here can deny a write that the broker still goes on to
            // accept.
            Thread.currentThread().interrupt();
            logger.warn("Interrupted while publishing write job {} to {}. The job stays "
                            + "PROCESSING until it completes or times out.",
                    message.jobId(), topic, e);
        } catch (ExecutionException e) {
            // The broker answered and refused, so nothing has the message. No request thread is
            // waiting on this call any more, so this worker records the failure itself.
            logger.error("Could not hand write job {} to the broker on {}", message.jobId(), topic, e);
            jobService.fail(message.jobId(), e);
        }
    }
}
