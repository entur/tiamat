package org.rutebanken.tiamat.rest.write.async;

/**
 * A worker tried to complete a job it no longer owns, because a sweeper timed it out while the
 * write was running.
 * <p>
 * Thrown inside the transaction that wraps processing and completion, so it rolls the write back.
 * That is what makes a job reported as TIMED_OUT mean nothing was written, rather than leaving a
 * stop place behind and telling the client otherwise.
 */
public class WriteJobNotOwnedException extends RuntimeException {

    public WriteJobNotOwnedException(Long jobId) {
        super("Job " + jobId + " is no longer owned by this worker; it was most likely timed out.");
    }
}
