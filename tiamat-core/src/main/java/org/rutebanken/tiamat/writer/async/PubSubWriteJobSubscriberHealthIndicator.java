package org.rutebanken.tiamat.writer.async;

import com.google.api.core.ApiService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Reports whether this pod still consumes write jobs.
 * <p>
 * A subscriber that fails keeps the pod up and the API answering. Every job it accepts then
 * reaches TIMED_OUT ten minutes later. The log says so once, at the moment it happens. This says
 * so for as long as it is true, which is what an alert reads.
 * <p>
 * The name of the bean gives the key in the health response. It must not be the name of the
 * subscriber bean itself, because Spring refuses two beans with one name.
 * <p>
 * This contributes to {@code /actuator/health} only. The Kubernetes probes are
 * {@code /health/ready} and {@code /health/live}, which this does not reach: readiness asks
 * whether the database answers, and a pod that cannot consume jobs still serves every read. To
 * take the pod out of service for this stops the reads as well.
 */
@Component
@ConditionalOnProperty(name = "tiamat.write-api.enabled", havingValue = "true")
@ConditionalOnProperty(name = "tiamat.write-api.transport", havingValue = "pubsub")
public class PubSubWriteJobSubscriberHealthIndicator implements HealthIndicator {

    private final PubSubWriteJobSubscriber subscriber;

    public PubSubWriteJobSubscriberHealthIndicator(PubSubWriteJobSubscriber subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public Health health() {
        return subscriber.subscriberState()
                .map(this::healthFor)
                .orElseGet(() -> Health.outOfService()
                        .withDetail("state", "NOT_STARTED")
                        .build());
    }

    private Health healthFor(ApiService.State state) {
        Health.Builder builder = switch (state) {
            case RUNNING -> Health.up();
            case FAILED -> Health.down();
            case NEW, STARTING -> Health.unknown();
            case STOPPING, TERMINATED -> Health.outOfService();
        };
        return builder.withDetail("state", state.name()).build();
    }
}
