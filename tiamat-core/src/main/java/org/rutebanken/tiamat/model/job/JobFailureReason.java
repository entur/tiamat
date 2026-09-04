package org.rutebanken.tiamat.model.job;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Why a write job did not finish.
 * <p>
 * The distinction that matters is whether the same payload can succeed later.
 * {@link #STALE_VERSION} and {@link #QUEUE_FULL} say that it can. The others say that it cannot,
 * and that the caller must change something first.
 */
@Schema(description = "Why the job did not finish.")
public enum JobFailureReason {

    /** The payload was rejected. The same payload always gives the same result. */
    INVALID_PAYLOAD,

    /** Another client wrote first. Read the stop place again, reapply the edit, and submit it. */
    STALE_VERSION,

    /** The caller can use the write API, but not for this stop place or this stop place type. */
    ACCESS_DENIED,

    /** The system was full. The same payload can succeed later. */
    QUEUE_FULL,

    /** The database refused the write. */
    CONSTRAINT_VIOLATION,

    /** The job did not reach a terminal state in time. Nothing was written. */
    TIMED_OUT,

    /** No other reason matched. */
    UNEXPECTED_ERROR
}
