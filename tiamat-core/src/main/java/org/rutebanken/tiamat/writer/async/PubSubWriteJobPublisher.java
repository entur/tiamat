package org.rutebanken.tiamat.writer.async;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Hands a write job to Pub/Sub, which then delivers it to whichever pod is subscribed.
 * <p>
 * The payload travels as the message body and everything else as attributes, so the raw request
 * bytes are carried through untouched and nothing has to parse them to route the message. That
 * matches the rest of the design: the payload is not looked at until the job is being processed.
 * <p>
 * Publishing blocks until the broker has acknowledged the message. It would be faster not to
 * wait, but the client is told its job was accepted the moment this returns, and a job accepted
 * without anything to deliver it would sit untouched until the timeout sweeper found it. Waiting
 * turns that silent case into a failed publish the caller can be told about.
 */
@Component
@ConditionalOnProperty(name = "tiamat.write-api.transport", havingValue = "pubsub")
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WriteJobRejectedException("Interrupted while publishing the write job.", e);
        } catch (ExecutionException | TimeoutException e) {
            // Reported the same way the in-memory transport reports a full queue: the caller knows
            // only that the job could not be handed over, not which transport failed to take it.
            throw new WriteJobRejectedException("Could not hand the write job to the broker.", e);
        }
    }
}
