package org.rutebanken.tiamat.rest.write.dto;

import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;

import java.util.List;

public record StopPlaceJobDto(
    Long jobId,
    AsyncStopPlaceJobStatus status,
    List<String> createdIds,
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
