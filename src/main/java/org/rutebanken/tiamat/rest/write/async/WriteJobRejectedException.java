package org.rutebanken.tiamat.rest.write.async;

/**
 * A transport could not accept a write job, for instance because its queue is full.
 * <p>
 * Transport neutral on purpose: the caller maps this to 503 without knowing whether the transport
 * is an executor, a broker, or something else. Previously this was the executor's own
 * RejectedExecutionException, which no other transport could produce.
 */
public class WriteJobRejectedException extends RuntimeException {

    public WriteJobRejectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
