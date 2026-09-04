package org.rutebanken.tiamat.writer.async;

import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.pubsub.v1.PubsubMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static org.rutebanken.tiamat.writer.async.PubSubWriteJobPublisher.ATTRIBUTE_JOB_ID;
import static org.rutebanken.tiamat.writer.async.PubSubWriteJobPublisher.ATTRIBUTE_OPERATION;

/**
 * This class receives write jobs from Pub/Sub and gives them to the handler. This is the same
 * handler that the in-process transport uses, and nothing about how to process a job lives here.
 * <p>
 * This class makes one decision only: whether to acknowledge the message. The handler records the
 * outcome of a job itself, and this includes a failure. Thus this class acknowledges a message
 * when the handler returns, because the job is then in a terminal state and a second delivery
 * achieves nothing. It sends a message back only when the handler throws, which means that
 * nothing recorded the outcome and the job is still unfinished.
 * <p>
 * A second delivery is safe, and not merely tolerable. The handler claims a job before it does
 * the work, so it discards a second delivery of a job that already ran. That property is what
 * keeps this class small.
 */
@Component
@ConditionalOnProperty(name = "tiamat.write-api.transport", havingValue = "pubsub")
public class PubSubWriteJobSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(PubSubWriteJobSubscriber.class);

    private final PubSubTemplate pubSubTemplate;
    private final WriteJobHandler handler;
    private final String subscription;
    private Subscriber activeSubscriber;

    public PubSubWriteJobSubscriber(
            PubSubTemplate pubSubTemplate,
            WriteJobHandler handler,
            @Value("${tiamat.write-api.pubsub.subscription:tiamat-write-jobs-sub}") String subscription
    ) {
        this.pubSubTemplate = pubSubTemplate;
        this.handler = handler;
        this.subscription = subscription;
    }

    @PostConstruct
    public void start() {
        activeSubscriber = pubSubTemplate.subscribe(subscription, this::onMessage);
        logger.info("Listening for write jobs on subscription {}", subscription);
    }

    @PreDestroy
    public void stop() {
        if (activeSubscriber != null) {
            activeSubscriber.stopAsync();
        }
    }

    private void onMessage(BasicAcknowledgeablePubsubMessage message) {
        WriteJobMessage writeJob;
        try {
            writeJob = toWriteJob(message.getPubsubMessage());
        } catch (RuntimeException e) {
            // A second delivery cannot help, because the message does not describe a job. To send
            // it back makes a loop that runs until the dead letter policy takes it. Acknowledge
            // instead, and let the job time out, which is an outcome the client already handles.
            logger.error("Discarding a message on {} that is not a write job", subscription, e);
            message.ack();
            return;
        }

        try {
            handler.handle(writeJob);
            message.ack();
        } catch (RuntimeException e) {
            // The handler records its own failures. Thus a throw means that nothing wrote the
            // outcome, and that the job is unfinished. A second delivery is safe, because the
            // claim decides whether the work runs.
            logger.error("Write job {} was not completed, returning it for redelivery", writeJob.jobId(), e);
            message.nack();
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
