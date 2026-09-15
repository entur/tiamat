package org.rutebanken.tiamat.gcp.writer;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Matches when a deployment runs the write API and has chosen Pub/Sub to carry its jobs.
 * <p>
 * Two properties with two different values need two conditions, which one
 * {@code @ConditionalOnProperty} cannot express.
 * <p>
 * The transport alone is not enough. A broker delivers work whether or not this deployment wants
 * it. So a subscriber gated only on the transport keeps writing after
 * {@code tiamat.write-api.enabled} goes to false.
 * {@link org.rutebanken.tiamat.writer.async.WriteJobTimeoutSweeper} is gated on {@code enabled},
 * so by then nothing guarantees that a job reaches a terminal state.
 */
public class OnPubSubWriteTransport extends AllNestedConditions {

    OnPubSubWriteTransport() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnProperty(name = "tiamat.write-api.enabled", havingValue = "true")
    static class WriteApiIsEnabled {
    }

    @ConditionalOnProperty(name = "tiamat.write-api.transport", havingValue = "pubsub")
    static class PubSubIsTheTransport {
    }
}
