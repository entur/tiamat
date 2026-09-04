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
 * This class hands a write job to Pub/Sub. Pub/Sub then delivers the job to a subscribed pod.
 * <p>
 * The payload travels as the message body, and everything else travels as attributes. Thus this
 * class carries the request bytes through without a change, and nothing reads them to route the
 * message. This agrees with the rest of the design: nothing reads the payload until the job runs.
 * <p>
 * This class waits for the broker to acknowledge the message. Not to wait is faster. But the
 * client learns that the API accepted its job as soon as this method returns. A job that no
 * transport took stays untouched until the sweeper times it out. The wait makes that silent case
 * into a failure that the caller can see.
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
            // Reported the way the in-process transport reports a full queue. The caller learns
            // only that no transport took the job, and not which transport failed to take it.
            throw new WriteJobRejectedException("Could not hand the write job to the broker.", e);
        }
    }
}
