package org.rutebanken.tiamat.gcp.writer;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.rutebanken.tiamat.writer.async.WriteJobMessage;
import org.rutebanken.tiamat.writer.async.WriteJobRejectedException;
import org.rutebanken.tiamat.writer.async.WriteJobPublisher;
import org.rutebanken.tiamat.writer.async.WriteJobTimeoutSweeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutionException;
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
 * Waits for the broker to acknowledge the message. Not to wait is faster. But the client treats a
 * return from this method as acceptance, and a job that no transport took stays untouched until
 * {@link WriteJobTimeoutSweeper} times it out.
 */
@Component
@Conditional(OnPubSubWriteTransport.class)
public class PubSubWriteJobPublisher implements WriteJobPublisher {

    private static final Logger logger = LoggerFactory.getLogger(PubSubWriteJobPublisher.class);

    static final String ATTRIBUTE_JOB_ID = "jobId";
    static final String ATTRIBUTE_OPERATION = "operation";

    private final PubSubTemplate pubSubTemplate;
    private final String topic;
    private final long publishTimeoutSeconds;

    public PubSubWriteJobPublisher(
            PubSubTemplate pubSubTemplate,
            @Value("${tiamat.write-api.pubsub.topic:tiamat-write-jobs}") String topic,
            @Value("${tiamat.write-api.pubsub.publish-timeout-seconds:10}") long publishTimeoutSeconds
    ) {
        this.pubSubTemplate = pubSubTemplate;
        this.topic = topic;
        this.publishTimeoutSeconds = publishTimeoutSeconds;
    }

    @Override
    public void publish(WriteJobMessage message) {
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
            // The message can still reach the broker, so this returns and the job stays
            // PROCESSING. A rejection here says nothing was written. If the message does arrive,
            // that is false, and the caller resubmits and gets a second stop place.
            logger.warn("Publishing write job {} to {} took longer than {}s. The job stays "
                            + "PROCESSING until it completes or times out.",
                    message.jobId(), topic, publishTimeoutSeconds, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WriteJobRejectedException("Interrupted while publishing the write job.", e);
        } catch (ExecutionException e) {
            // The broker answered and refused, so nothing has the message. Reported as a full
            // queue, so the caller does not learn which transport failed to take the job.
            throw new WriteJobRejectedException("Could not hand the write job to the broker.", e);
        }
    }
}
