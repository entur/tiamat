package org.rutebanken.tiamat.writer.async;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.pubsub.v1.PubsubMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

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
        return new PubSubWriteJobPublisher(pubSubTemplate, "write-jobs", seconds);
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
