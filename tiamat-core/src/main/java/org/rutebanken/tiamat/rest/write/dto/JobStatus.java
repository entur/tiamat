package org.rutebanken.tiamat.rest.write.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The state of a write job, as a client sees it.
 * <p>
 * Separate from {@link org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus}, which is what the
 * database holds. That enum has a fifth value, IN_PROGRESS, that marks a job as claimed. The
 * distinction is internal, and a client sees a claimed job as {@link #PROCESSING}.
 * <p>
 * The two types stay apart for two reasons. A generated API document reads the declared type, so a
 * wire field of the stored type advertises a state that no response ever carries. And the stored
 * type is an ordinal, which means its values can only ever be appended: to bind the published
 * contract to it is to make every later change to the job states a change to the database as well.
 */
@Schema(description = "PROCESSING while the job runs. FINISHED, FAILED or TIMED_OUT when it ends.")
public enum JobStatus {

    /** The job is not complete. Poll again. */
    PROCESSING,

    /** The write is committed. Read the result. */
    FINISHED,

    /** The write did not happen. Read the failure. */
    FAILED,

    /** The job did not complete in time. Nothing was written, so the same request can go again. */
    TIMED_OUT
}
