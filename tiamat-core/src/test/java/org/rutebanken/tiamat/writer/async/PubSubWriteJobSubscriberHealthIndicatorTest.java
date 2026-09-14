package org.rutebanken.tiamat.writer.async;

import com.google.api.core.ApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Status;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PubSubWriteJobSubscriberHealthIndicatorIndicatorTest {

    @Mock
    private PubSubWriteJobSubscriber subscriber;

    private Status statusFor(Optional<ApiService.State> state) {
        when(subscriber.subscriberState()).thenReturn(state);
        return new PubSubWriteJobSubscriberHealthIndicator(subscriber).health().getStatus();
    }

    @Test
    void aRunningSubscriberIsUp() {
        assertThat(statusFor(Optional.of(ApiService.State.RUNNING))).isEqualTo(Status.UP);
    }

    /**
     * The state that matters. A failed subscriber keeps the pod up and the API answering, and
     * takes every job it accepts to TIMED_OUT. Nothing else reports it after the first log line.
     */
    @Test
    void aFailedSubscriberIsDown() {
        assertThat(statusFor(Optional.of(ApiService.State.FAILED))).isEqualTo(Status.DOWN);
    }

    @Test
    void aStoppedSubscriberIsOutOfService() {
        assertThat(statusFor(Optional.of(ApiService.State.TERMINATED)))
                .isEqualTo(Status.OUT_OF_SERVICE);
    }

    @Test
    void aSubscriberThatNeverStartedIsOutOfService() {
        assertThat(statusFor(Optional.empty())).isEqualTo(Status.OUT_OF_SERVICE);
    }

    @Test
    void theStateIsReportedAsADetail() {
        when(subscriber.subscriberState()).thenReturn(Optional.of(ApiService.State.FAILED));

        assertThat(new PubSubWriteJobSubscriberHealthIndicator(subscriber).health().getDetails())
                .containsEntry("state", "FAILED");
    }
}
