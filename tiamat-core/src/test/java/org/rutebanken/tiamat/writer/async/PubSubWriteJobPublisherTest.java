package org.rutebanken.tiamat.writer.async;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.pubsub.v1.PubsubMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PubSubWriteJobPublisherTest {

    private static final byte[] PAYLOAD = "<stopPlaces/>".getBytes(StandardCharsets.UTF_8);

    @Mock
    private PubSubTemplate pubSubTemplate;

    private PubSubWriteJobPublisher publisherWithTimeout(long seconds) {
        return new PubSubWriteJobPublisher(pubSubTemplate, "write-jobs", seconds, 10);
    }

    private PubSubWriteJobPublisher publisherWithCapacity(int maxConcurrentPublishes) {
        return new PubSubWriteJobPublisher(pubSubTemplate, "write-jobs", 10, maxConcurrentPublishes);
    }

    /**
     * A timeout says nothing about whether the broker has the message, so a rejection here can
     * deny a write that happened.
     */
    @Test
    void aTimeoutDoesNotRejectTheJob() {
        // A future that never completes is a broker that has not answered yet.
        when(pubSubTemplate.publish(anyString(), any(PubsubMessage.class)))
                .thenReturn(new CompletableFuture<>());

        assertThatCode(() -> publisherWithTimeout(1)
                .publish(WriteJobMessage.create(42L, PAYLOAD)))
                .doesNotThrowAnyException();
    }

    /**
     * An interrupt says nothing about whether the broker has the message, the same as a timeout.
     * The status is restored rather than swallowed, so callers upstream still see it.
     */
    @Test
    void anInterruptDoesNotRejectTheJob() {
        when(pubSubTemplate.publish(anyString(), any(PubsubMessage.class)))
                .thenReturn(new CompletableFuture<>());

        Thread.currentThread().interrupt();
        try {
            assertThatCode(() -> publisherWithTimeout(10)
                    .publish(WriteJobMessage.create(42L, PAYLOAD)))
                    .doesNotThrowAnyException();
            assertThat(Thread.currentThread().isInterrupted())
                    .as("the interrupt status is restored, not swallowed")
                    .isTrue();
        } finally {
            Thread.interrupted();
        }
    }

    /** A refusal is different: the broker answered, and nothing has the message. */
    @Test
    void aRefusalFromTheBrokerRejectsTheJob() {
        when(pubSubTemplate.publish(anyString(), any(PubsubMessage.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("topic not found")));

        assertThatThrownBy(() -> publisherWithTimeout(10)
                .publish(WriteJobMessage.create(42L, PAYLOAD)))
                .isInstanceOf(WriteJobRejectedException.class);
    }

    @Test
    void anAcknowledgedMessageReturnsNormally() {
        when(pubSubTemplate.publish(anyString(), any(PubsubMessage.class)))
                .thenReturn(CompletableFuture.completedFuture("message-1"));

        assertThatCode(() -> publisherWithTimeout(10)
                .publish(WriteJobMessage.create(42L, PAYLOAD)))
                .doesNotThrowAnyException();
    }

    /**
     * Pub/Sub itself has no problem with concurrency, but a request thread blocked on
     * {@code Future.get()} is a resource this deployment does have to bound.
     */
    @Test
    void rejectsWhenNoPublishPermitIsAvailable() {
        assertThatThrownBy(() -> publisherWithCapacity(0)
                .publish(WriteJobMessage.create(42L, PAYLOAD)))
                .isInstanceOf(WriteJobRejectedException.class)
                .hasCauseInstanceOf(RejectedExecutionException.class);
    }

    /** A permit taken for one publish must not stay taken once that publish is done. */
    @Test
    void releasesThePermitAfterEachPublish() {
        when(pubSubTemplate.publish(anyString(), any(PubsubMessage.class)))
                .thenReturn(CompletableFuture.completedFuture("message-1"));

        PubSubWriteJobPublisher publisher = publisherWithCapacity(1);

        assertThatCode(() -> {
            publisher.publish(WriteJobMessage.create(1L, PAYLOAD));
            publisher.publish(WriteJobMessage.create(2L, PAYLOAD));
        }).doesNotThrowAnyException();
    }

    /** A permit taken for a publish that fails must also come back, not leak. */
    @Test
    void releasesThePermitEvenWhenTheBrokerRefuses() {
        when(pubSubTemplate.publish(anyString(), any(PubsubMessage.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("topic not found")))
                .thenReturn(CompletableFuture.completedFuture("message-1"));

        PubSubWriteJobPublisher publisher = publisherWithCapacity(1);

        assertThatThrownBy(() -> publisher.publish(WriteJobMessage.create(1L, PAYLOAD)))
                .isInstanceOf(WriteJobRejectedException.class);
        assertThatCode(() -> publisher.publish(WriteJobMessage.create(2L, PAYLOAD)))
                .doesNotThrowAnyException();
    }

    /** Nothing reads the payload to route the message. */
    @Test
    void theJobIdAndOperationTravelAsAttributes() {
        when(pubSubTemplate.publish(anyString(), any(PubsubMessage.class)))
                .thenReturn(CompletableFuture.completedFuture("message-1"));

        publisherWithTimeout(10).publish(WriteJobMessage.update(7L, PAYLOAD));

        var captor = org.mockito.ArgumentCaptor.forClass(PubsubMessage.class);
        org.mockito.Mockito.verify(pubSubTemplate).publish(anyString(), captor.capture());
        assertThat(captor.getValue().getAttributesMap())
                .containsEntry("jobId", "7")
                .containsEntry("operation", "UPDATE");
        assertThat(captor.getValue().getData().toByteArray()).isEqualTo(PAYLOAD);
    }
}
