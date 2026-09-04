package org.rutebanken.tiamat.gcp.writer;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.writer.async.WriteJobHandler;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Every message has to end acknowledged or sent back. One that ends neither keeps its lease for
 * the whole ack extension period, an hour by default. The claimed job behind it is unreachable
 * for all of that time, and only the sweeper ever finishes it.
 */
@ExtendWith(MockitoExtension.class)
class PubSubWriteJobSubscriberSettlementTest {

    @Mock
    private PubSubTemplate pubSubTemplate;

    @Mock
    private WriteJobHandler handler;

    @Mock
    private BasicAcknowledgeablePubsubMessage message;

    private PubSubWriteJobSubscriber subscriber() {
        return new PubSubWriteJobSubscriber(pubSubTemplate, handler, "write-jobs-sub", 30);
    }

    private void deliver(String jobId, String operation) {
        when(message.getPubsubMessage()).thenReturn(PubsubMessage.newBuilder()
                .setData(ByteString.copyFrom("<stopPlaces/>", StandardCharsets.UTF_8))
                .putAllAttributes(Map.of("jobId", jobId, "operation", operation))
                .build());
    }

    /**
     * A payload that exhausts the stack reaches the subscriber, because
     * {@code DefaultWriteJobHandler} catches Exception.
     */
    @Test
    void anErrorFromTheHandlerStillReturnsTheMessage() {
        deliver("42", "CREATE");
        doThrow(new StackOverflowError("deeply nested payload")).when(handler).handle(any());

        assertThatThrownBy(() -> subscriber().onMessage(message))
                .isInstanceOf(StackOverflowError.class);

        verify(message).nack();
        verify(message, never()).ack();
    }

    /**
     * The error goes on rather than being swallowed. Settling the message is all this class can do
     * about it.
     */
    @Test
    void anErrorFromTheParseStillAcknowledgesTheMessage() {
        when(message.getPubsubMessage()).thenThrow(new StackOverflowError("in the client"));

        assertThatThrownBy(() -> subscriber().onMessage(message))
                .isInstanceOf(StackOverflowError.class);

        verify(message).ack();
        verify(message, never()).nack();
    }

    @Test
    void aMessageThatIsNotAJobIsAcknowledgedAndDiscarded() {
        when(message.getPubsubMessage()).thenReturn(PubsubMessage.newBuilder()
                .putAllAttributes(Map.of("nothing", "useful"))
                .build());

        assertThatCode(() -> subscriber().onMessage(message)).doesNotThrowAnyException();

        verify(message).ack();
        verify(handler, never()).handle(any());
    }

    @Test
    void aHandledJobIsAcknowledged() {
        deliver("42", "CREATE");

        subscriber().onMessage(message);

        verify(message).ack();
        verify(message, never()).nack();
    }

    @Test
    void anExceptionFromTheHandlerReturnsTheMessage() {
        deliver("42", "CREATE");
        doThrow(new IllegalStateException("could not record the outcome")).when(handler).handle(any());

        assertThatCode(() -> subscriber().onMessage(message)).doesNotThrowAnyException();

        verify(message).nack();
        verify(message, never()).ack();
    }
}
