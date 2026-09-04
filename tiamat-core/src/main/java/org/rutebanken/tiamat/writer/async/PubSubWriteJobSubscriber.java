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
 * Receives write jobs from Pub/Sub and hands them to the handler, which is the same handler the
 * in-memory transport uses. Nothing about processing a job lives here.
 * <p>
 * Acknowledgement is the only judgement this class makes. The handler records the outcome of a
 * job itself, including failures, so a message is acknowledged whenever the handler returns:
 * the job has reached a terminal state and redelivering it would achieve nothing. A message is
 * only sent back for redelivery when the handler throws, which means the outcome was never
 * recorded and the job is still waiting for someone to finish it.
 * <p>
 * Redelivery is safe rather than merely tolerable: the handler claims the job before doing any
 * work, so a second delivery of a job that has already been processed is discarded rather than
 * applied twice. That property is what lets this class stay this small.
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
            // Nothing here can be retried into working: the message does not describe a job. Sending
            // it back would loop until the dead letter policy takes it, so acknowledge and let the
            // job time out, which is the outcome the client already handles.
            logger.error("Discarding a message on {} that is not a write job", subscription, e);
            message.ack();
            return;
        }

        try {
            handler.handle(writeJob);
            message.ack();
        } catch (RuntimeException e) {
            // The handler records its own failures, so reaching here means the outcome was never
            // written and the job is still unfinished. Redelivery is safe because claiming is what
            // decides whether the work actually runs.
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
