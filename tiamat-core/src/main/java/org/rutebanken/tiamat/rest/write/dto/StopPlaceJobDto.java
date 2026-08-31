package org.rutebanken.tiamat.rest.write.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.model.job.StopPlaceIdMapping;

import java.util.List;

public record StopPlaceJobDto(
    @Schema(description = "ID of the job. Poll the job endpoint with this ID.", example = "88991")
    Long jobId,

    @Schema(description = "PROCESSING while the job runs. FINISHED, FAILED or TIMED_OUT when it ends.")
    AsyncStopPlaceJobStatus status,

    @Schema(description = "For a create job that is FINISHED, the NeTEx ID that you submitted and the ID that the system generated.")
    List<StopPlaceIdMapping> createdIds,

    @Schema(description = "Why the job is FAILED or TIMED_OUT. Null for the other statuses.")
    String errorMessage
) {
    public static StopPlaceJobDto from(AsyncStopPlaceJob asyncStopPlaceJob) {
        return new StopPlaceJobDto(
            asyncStopPlaceJob.getId(),
            reportedStatus(asyncStopPlaceJob.getStatus()),
            asyncStopPlaceJob.getCreatedIds(),
            asyncStopPlaceJob.getReason()
        );
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
