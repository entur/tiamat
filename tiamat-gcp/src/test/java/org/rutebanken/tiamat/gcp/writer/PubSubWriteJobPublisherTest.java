package org.rutebanken.tiamat.gcp.writer;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.pubsub.v1.PubsubMessage;
import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.writer.async.WriteJobMessage;
import org.rutebanken.tiamat.writer.async.WriteJobRejectedException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.tiamat.writer.JobService;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PubSubWriteJobPublisherTest {

    private static final byte[] PAYLOAD = "<stopPlaces/>".getBytes(StandardCharsets.UTF_8);

    @Mock
    private PubSubTemplate pubSubTemplate;

    @Mock
    private JobService jobService;

    private PubSubWriteJobPublisher publisherWithTimeout(long seconds) {
        return new PubSubWriteJobPublisher(pubSubTemplate, jobService, "write-jobs", seconds, 10);
    }

    private PubSubWriteJobPublisher publisherWithCapacity(int maxConcurrentPublishes) {
        return new PubSubWriteJobPublisher(pubSubTemplate, jobService, "write-jobs", 10, maxConcurrentPublishes);
    }

    /**
     * The point of running the publish call on its own executor: a request thread must not sit
     * blocked on a broker that never answers, since that thread comes from the pool that also
     * serves unrelated GraphQL and REST traffic.
     */
    @Test
    void doesNotBlockTheCallingThread() throws InterruptedException {
        CountDownLatch workerStarted = new CountDownLatch(1);
        CountDownLatch releaseWorker = new CountDownLatch(1);
        when(pubSubTemplate.publish(anyString(), any(PubsubMessage.class)))
                .thenAnswer(invocation -> {
                    workerStarted.countDown();
                    releaseWorker.await();
                    return CompletableFuture.completedFuture("message-1");
                });

        long start = System.nanoTime();
        publisherWithCapacity(1).publish(WriteJobMessage.create(42L, PAYLOAD));
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

        assertThat(elapsedMillis).as("publish() must return before the broker call finishes").isLessThan(1000);
        // Waited out here, not left to race the end of the test: otherwise the worker can still
        // be on its way to the stub when Mockito checks for unused stubbings and calls it unused.
        assertThat(workerStarted.await(1, TimeUnit.SECONDS))
                .as("the worker must actually reach the broker call")
                .isTrue();
        releaseWorker.countDown();
    }

    /**
     * A timeout says nothing about whether the broker has the message, so failing the job here
     * can deny a write that happened. The worker leaves the job PROCESSING for the sweeper.
     */
    @Test
    void aTimeoutDoesNotFailTheJob() {
        // A future that never completes is a broker that has not answered yet.
        when(pubSubTemplate.publish(anyString(), any(PubsubMessage.class)))
                .thenReturn(new CompletableFuture<>());

        publisherWithTimeout(1).publish(WriteJobMessage.create(42L, PAYLOAD));

        verify(jobService, after(1500).never()).fail(anyLong(), any());
    }

    /**
     * An interrupt says nothing about whether the broker has the message, the same as a timeout,
     * so it must not fail the job either.
     */
    @Test
    void anInterruptDoesNotFailTheJob() {
        when(pubSubTemplate.publish(anyString(), any(PubsubMessage.class)))
                .thenAnswer(invocation -> {
                    // The worker thread that is about to block on Future.get() is interrupted,
                    // not the test thread, so this reaches the same catch block a real interrupt
                    // during the wait would.
                    Thread.currentThread().interrupt();
                    return new CompletableFuture<>();
                });

        publisherWithTimeout(10).publish(WriteJobMessage.create(42L, PAYLOAD));

        verify(jobService, after(300).never()).fail(anyLong(), any());
    }

    /**
     * A refusal is different: the broker answered, and nothing has the message. No request
     * thread is waiting for this outcome any more, so the worker records it itself, the way
     * {@link DefaultWriteJobHandler} already records the outcome of the write it performs.
     */
    @Test
    void aRefusalFromTheBrokerFailsTheJob() {
        when(pubSubTemplate.publish(anyString(), any(PubsubMessage.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("topic not found")));

        publisherWithTimeout(10).publish(WriteJobMessage.create(42L, PAYLOAD));

        verify(jobService, timeout(1000)).fail(eq(42L), any());
    }

    @Test
    void anAcknowledgedMessageDoesNotFailTheJob() {
        when(pubSubTemplate.publish(anyString(), any(PubsubMessage.class)))
                .thenReturn(CompletableFuture.completedFuture("message-1"));

        publisherWithTimeout(10).publish(WriteJobMessage.create(42L, PAYLOAD));

        verify(jobService, after(300).never()).fail(anyLong(), any());
    }

    /**
     * Pub/Sub itself has no problem with concurrency, but each in-flight publish holds a worker
     * from this transport's own bounded pool, and that pool is what protects the shared request
     * thread pool from a slow or degraded broker.
     */
    @Test
    void rejectsWhenAllWorkersAreBusy() throws InterruptedException {
        CountDownLatch workerStarted = new CountDownLatch(1);
        CountDownLatch releaseWorker = new CountDownLatch(1);
        when(pubSubTemplate.publish(anyString(), any(PubsubMessage.class)))
                .thenAnswer(invocation -> {
                    workerStarted.countDown();
                    releaseWorker.await();
                    return CompletableFuture.completedFuture("message-1");
                });

        PubSubWriteJobPublisher publisher = publisherWithCapacity(1);
        publisher.publish(WriteJobMessage.create(1L, PAYLOAD));
        assertThat(workerStarted.await(1, TimeUnit.SECONDS))
                .as("the sole worker must have picked up the first job")
                .isTrue();

        assertThatThrownBy(() -> publisher.publish(WriteJobMessage.create(2L, PAYLOAD)))
                .isInstanceOf(WriteJobRejectedException.class)
                .hasCauseInstanceOf(RejectedExecutionException.class);

        releaseWorker.countDown();
    }

    /** Nothing reads the payload to route the message. */
    @Test
    void theJobIdAndOperationTravelAsAttributes() {
        when(pubSubTemplate.publish(anyString(), any(PubsubMessage.class)))
                .thenReturn(CompletableFuture.completedFuture("message-1"));

        publisherWithTimeout(10).publish(WriteJobMessage.update(7L, PAYLOAD));

        ArgumentCaptor<PubsubMessage> captor = ArgumentCaptor.forClass(PubsubMessage.class);
        verify(pubSubTemplate, timeout(1000)).publish(anyString(), captor.capture());
        assertThat(captor.getValue().getAttributesMap())
                .containsEntry("jobId", "7")
                .containsEntry("operation", "UPDATE");
        assertThat(captor.getValue().getData().toByteArray()).isEqualTo(PAYLOAD);
    }
}
