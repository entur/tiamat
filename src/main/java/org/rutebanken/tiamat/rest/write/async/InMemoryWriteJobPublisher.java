package org.rutebanken.tiamat.rest.write.async;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

/**
 * Development reference transport. Not intended for production use, which is why it is selected
 * only when {@code tiamat.write-api.transport} is explicitly set to {@code in-memory}.
 * <p>
 * The queue lives only in the memory of a single pod, so a restart loses queued and in flight
 * writes, and backpressure is per pod: a deployment with several replicas accepts several times
 * the configured queue capacity while the Hazelcast mutate lock still serialises the actual
 * writes.
 * <p>
 * Production is intended to use a message broker behind {@link WriteJobPublisher}. Do not address
 * the shortcomings above by adding durability here; that belongs to the broker backed transport.
 * Note how little there is to implement: everything about processing a job lives in
 * {@link WriteJobHandler}, which is where a second transport also sends its messages.
 */
@Component
@ConditionalOnProperty(name = "tiamat.write-api.transport", havingValue = "in-memory")
public class InMemoryWriteJobPublisher implements WriteJobPublisher {

    private final Executor executor;
    private final WriteJobHandler handler;

    public InMemoryWriteJobPublisher(
            @Qualifier("stopPlaceWriteExecutor") Executor executor,
            WriteJobHandler handler
    ) {
        this.executor = executor;
        this.handler = handler;
    }

    @Override
    public void publish(WriteJobMessage message) {
        try {
            executor.execute(() -> handler.handle(message));
        } catch (RejectedExecutionException e) {
            // Translated here so the caller does not have to know that this transport happens to
            // be an executor. Another transport reports its own backpressure the same way.
            throw new WriteJobRejectedException("Write queue is full.", e);
        }
    }
}
