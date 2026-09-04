package org.rutebanken.tiamat.gcp.writer;

import com.google.api.core.ApiService;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.pubsub.v1.PubsubMessage;
import org.rutebanken.tiamat.writer.JobService;
import org.rutebanken.tiamat.writer.async.DefaultWriteJobHandler;
import org.rutebanken.tiamat.writer.async.WriteJobHandler;
import org.rutebanken.tiamat.writer.async.WriteJobMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.rutebanken.tiamat.gcp.writer.PubSubWriteJobPublisher.ATTRIBUTE_JOB_ID;
import static org.rutebanken.tiamat.gcp.writer.PubSubWriteJobPublisher.ATTRIBUTE_OPERATION;

/**
 * Receives write jobs from Pub/Sub and gives them to {@link WriteJobHandler}.
 * <p>
 * The handler records the outcome of a job itself, and this includes a failure. So this class
 * acknowledges a message when the handler returns: the job is terminal, and a second delivery
 * achieves nothing. It sends a message back when the handler throws, which means that nothing
 * recorded the outcome.
 * <p>
 * That second delivery retries the work only when the throw came before the claim. A claim moves
 * the job to IN_PROGRESS, and {@link JobService#claim} matches PROCESSING, so the redelivery of a
 * claimed job is discarded and acknowledged. The job then ends at TIMED_OUT through
 * {@link WriteJobTimeoutSweeper}, which is true: the write rolled back. The message goes back
 * because nothing recorded the outcome, and not because the work is sure to run again.
 * <p>
 * The discard is what makes a second delivery safe. {@link DefaultWriteJobHandler} claims a job
 * before it does the work, so a delivery of a job that already ran writes nothing.
 * <p>
 * A {@link SmartLifecycle} rather than {@code @PostConstruct} and {@code @PreDestroy}. The two
 * ends of a consumer both need the rest of the context, and the annotations give neither.
 * {@code @PostConstruct} runs while the context still builds, so a delivery can reach a write
 * before the beans it needs exist. {@code @PreDestroy} runs during bean destruction, so the pool
 * a running write holds can close underneath it. A lifecycle starts after the refresh finishes
 * and stops before anything is destroyed.
 */
@Component
@Conditional(OnPubSubWriteTransport.class)
public class PubSubWriteJobSubscriber implements SmartLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(PubSubWriteJobSubscriber.class);

    private final PubSubTemplate pubSubTemplate;
    private final WriteJobHandler handler;
    private final String subscription;
    private final long shutdownTimeoutSeconds;
    private volatile Subscriber activeSubscriber;

    public PubSubWriteJobSubscriber(
            PubSubTemplate pubSubTemplate,
            WriteJobHandler handler,
            @Value("${tiamat.write-api.pubsub.subscription:tiamat-write-jobs-sub}") String subscription,
            @Value("${tiamat.write-api.pubsub.shutdown-timeout-seconds:30}") long shutdownTimeoutSeconds
    ) {
        this.pubSubTemplate = pubSubTemplate;
        this.handler = handler;
        this.subscription = subscription;
        this.shutdownTimeoutSeconds = shutdownTimeoutSeconds;
    }

    @Override
    public void start() {
        Subscriber subscriber = pubSubTemplate.subscribe(subscription, this::onMessage);
        subscriber.addListener(new FailureListener(), Runnable::run);
        activeSubscriber = subscriber;
        logger.info("Listening for write jobs on subscription {}", subscription);
    }

    /**
     * Waits for the subscriber to terminate, rather than only asking it to stop. A pod that leaves
     * while a write runs fails that write on a pool that closed under it. The client then holds a
     * job that no pod owns, until a sweeper somewhere else times it out.
     */
    @Override
    public void stop() {
        Subscriber subscriber = activeSubscriber;
        if (subscriber == null) {
            return;
        }
        activeSubscriber = null;
        subscriber.stopAsync();
        try {
            subscriber.awaitTerminated(shutdownTimeoutSeconds, TimeUnit.SECONDS);
            logger.info("Stopped listening on subscription {}", subscription);
        } catch (TimeoutException e) {
            logger.warn("The subscriber on {} did not stop within {}s. A write that is still "
                    + "running ends as a timed out job.", subscription, shutdownTimeoutSeconds, e);
        }
    }

    @Override
    public boolean isRunning() {
        return activeSubscriber != null;
    }

    /**
     * The state of the subscriber, for a health check to report. Read from the subscriber itself
     * and not from what {@link FailureListener} last saw. A failure between the start and the
     * listener is then still visible.
     */
    public Optional<ApiService.State> subscriberState() {
        Subscriber subscriber = activeSubscriber;
        return subscriber == null ? Optional.empty() : Optional.of(subscriber.state());
    }

    /**
     * A subscriber fails on its own thread and says nothing. Without this the pod stays up and
     * answers every request, while it consumes no job at all. Every write it accepted then reaches
     * TIMED_OUT ten minutes later. A subscription that does not exist, and a service account
     * without permission to consume one, both end here.
     */
    private class FailureListener extends ApiService.Listener {
        @Override
        public void failed(ApiService.State from, Throwable cause) {
            logger.error("The subscriber on {} failed from state {}. This pod now consumes no "
                    + "write jobs, and every job it accepts times out.", subscription, from, cause);
        }
    }

    /**
     * Settles every message, whatever leaves this method. An Error is caught for that reason
     * alone, and then goes on its way. A message that is neither acknowledged nor sent back keeps
     * its lease for the whole ack extension period. The claimed job behind it is invisible for all
     * of that time. A payload that exhausts the stack reaches here, because
     * {@link DefaultWriteJobHandler} catches Exception.
     */
    void onMessage(BasicAcknowledgeablePubsubMessage message) {
        WriteJobMessage writeJob;
        try {
            writeJob = toWriteJob(message.getPubsubMessage());
        } catch (RuntimeException e) {
            // The message does not describe a job, so a second delivery makes a loop that runs
            // until the dead letter policy takes it. Acknowledge, and let the job time out.
            logger.error("Discarding a message on {} that is not a write job", subscription, e);
            message.ack();
            return;
        } catch (Error e) {
            message.ack();
            throw e;
        }

        try {
            handler.handle(writeJob);
            message.ack();
        } catch (RuntimeException e) {
            // The handler records its own failures, so a throw means nothing wrote the outcome.
            logger.error("Write job {} was not completed, returning it for redelivery", writeJob.jobId(), e);
            message.nack();
        } catch (Error e) {
            logger.error("Write job {} ended in an error. Returning the message and going on.",
                    writeJob.jobId(), e);
            message.nack();
            throw e;
        }
    }

    private WriteJobMessage toWriteJob(PubsubMessage message) {
        String jobId = message.getAttributesOrThrow(ATTRIBUTE_JOB_ID);
        String operation = message.getAttributesOrThrow(ATTRIBUTE_OPERATION);
        return new WriteJobMessage(
                Long.valueOf(jobId),
                WriteJobMessage.Operation.valueOf(operation),
                message.getData().toByteArray());
    }
}
