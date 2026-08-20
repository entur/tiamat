package org.rutebanken.tiamat.model.job;

/**
 * Persisted as an ordinal, so values must only ever be <em>appended</em>. Inserting or reordering
 * silently reinterprets every existing row.
 */
public enum AsyncStopPlaceJobStatus {
    PROCESSING,
    FINISHED,
    FAILED,
    /**
     * Claimed by a worker. Reported to clients as {@link #PROCESSING}: the distinction is internal,
     * and exists so that a job can be claimed exactly once.
     */
    IN_PROGRESS,
    /**
     * Never completed, for infrastructure reasons rather than anything about the payload. Nothing
     * was written, so resubmitting the same payload is safe.
     */
    TIMED_OUT,
}
