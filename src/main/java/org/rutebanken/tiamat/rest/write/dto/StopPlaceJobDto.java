package org.rutebanken.tiamat.rest.write.dto;

import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.model.job.StopPlaceIdMapping;

import java.util.List;

public record StopPlaceJobDto(
    Long jobId,
    AsyncStopPlaceJobStatus status,
    List<StopPlaceIdMapping> createdIds,
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
