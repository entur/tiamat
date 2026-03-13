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
            asyncStopPlaceJob.getStatus(),
            asyncStopPlaceJob.getCreatedIds(),
            asyncStopPlaceJob.getReason()
        );
    }
}
