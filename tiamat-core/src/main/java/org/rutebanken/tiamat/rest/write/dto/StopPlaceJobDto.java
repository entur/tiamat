package org.rutebanken.tiamat.rest.write.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.model.job.JobFailureReason;
import org.rutebanken.tiamat.model.job.WrittenStopPlace;

import java.util.List;

/**
 * The state of a write job.
 * <p>
 * The outcome sits in one of two objects, and the status says which one to read. A FINISHED job
 * gives {@code result} and no {@code failure}. A FAILED or TIMED_OUT job gives {@code failure} and
 * no {@code result}. A PROCESSING job gives neither, because it has no outcome yet.
 * <p>
 * The two objects exist so that the position of a field says when the field applies. The earlier
 * shape put every field at the top level, which left the reader to learn from the documentation
 * which status filled in which field.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record StopPlaceJobDto(

    @Schema(description = "ID of the job. Poll the job endpoint with this ID.", example = "88991")
    Long jobId,

    @Schema(description = "PROCESSING while the job runs. FINISHED, FAILED or TIMED_OUT when it ends.")
    AsyncStopPlaceJobStatus status,

    @Schema(description = "What the write produced. Present when the status is FINISHED.")
    WriteResult result,

    @Schema(description = "Why the write did not happen. Present when the status is FAILED or TIMED_OUT.")
    WriteFailure failure
) {

    @Schema(description = "What a successful write produced.")
    public record WriteResult(

        @Schema(description = "One entry for every stop place that the job wrote.")
        List<WrittenStopPlace> stopPlaces
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Why a write did not happen.")
    public record WriteFailure(

        @Schema(description = "The kind of failure.")
        JobFailureReason reasonCode,

        @Schema(description = "A description of the failure in English. The wording can change between releases.",
                example = "The stop place moved to version 2 while this job was pending. Read it again and reapply the change.")
        String message,

        @Schema(description = "The version that the stop place is at now. Set when reasonCode is STALE_VERSION.",
                example = "2")
        Long currentVersion
    ) {}

    public static StopPlaceJobDto from(AsyncStopPlaceJob asyncStopPlaceJob) {
        AsyncStopPlaceJobStatus status = reportedStatus(asyncStopPlaceJob.getStatus());
        return new StopPlaceJobDto(
            asyncStopPlaceJob.getId(),
            status,
            resultOf(asyncStopPlaceJob, status),
            failureOf(asyncStopPlaceJob, status)
        );
    }

    private static WriteResult resultOf(AsyncStopPlaceJob job, AsyncStopPlaceJobStatus status) {
        if (status != AsyncStopPlaceJobStatus.FINISHED) {
            return null;
        }
        List<WrittenStopPlace> written = job.getWrittenStopPlaces();
        return new WriteResult(written == null ? List.of() : written);
    }

    private static WriteFailure failureOf(AsyncStopPlaceJob job, AsyncStopPlaceJobStatus status) {
        if (status != AsyncStopPlaceJobStatus.FAILED && status != AsyncStopPlaceJobStatus.TIMED_OUT) {
            return null;
        }
        return new WriteFailure(job.getReasonCode(), job.getReason(), job.getCurrentVersion());
    }

    /**
     * IN_PROGRESS exists so a job can be claimed exactly once. That is an internal distinction, so
     * clients continue to see a claimed job as PROCESSING rather than having the wire contract
     * change for an implementation detail.
     */
    private static AsyncStopPlaceJobStatus reportedStatus(AsyncStopPlaceJobStatus status) {
        return status == AsyncStopPlaceJobStatus.IN_PROGRESS
                ? AsyncStopPlaceJobStatus.PROCESSING
                : status;
    }
}
