package org.rutebanken.tiamat.rest.write.controllers;

import jakarta.ws.rs.NotFoundException;
import org.junit.Test;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.model.job.JobFailureReason;
import org.rutebanken.tiamat.model.job.WrittenStopPlace;
import org.rutebanken.tiamat.writer.JobService;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JobControllerTest {

    private final JobService jobService = mock(JobService.class);
    private final JobController jobController = new JobControllerImpl(
        jobService
    );

    @Test
    public void aFinishedJobGivesTheResultAndNoFailure() {
        AsyncStopPlaceJob job = mock(AsyncStopPlaceJob.class);
        when(job.getId()).thenReturn(1L);
        when(job.getStatus()).thenReturn(AsyncStopPlaceJobStatus.FINISHED);
        when(job.getWrittenStopPlaces()).thenReturn(
            singletonList(WrittenStopPlace.created("submitted-id", "NSR:StopPlace:1", 1L))
        );
        when(jobService.getJob(1L)).thenReturn(Optional.of(job));

        StopPlaceJobDto result = jobController.getJobStatus(1L);

        assertThat(result.jobId()).isEqualTo(1L);
        assertThat(result.status()).isEqualTo(AsyncStopPlaceJobStatus.FINISHED);
        assertThat(result.result().stopPlaces()).containsExactly(
            WrittenStopPlace.created("submitted-id", "NSR:StopPlace:1", 1L)
        );
        assertThat(result.failure()).isNull();
    }

    @Test
    public void aFailedJobGivesTheFailureAndNoResult() {
        AsyncStopPlaceJob job = mock(AsyncStopPlaceJob.class);
        when(job.getId()).thenReturn(1L);
        when(job.getStatus()).thenReturn(AsyncStopPlaceJobStatus.FAILED);
        when(job.getReason()).thenReturn("Error");
        when(job.getReasonCode()).thenReturn(JobFailureReason.STALE_VERSION);
        when(job.getCurrentVersion()).thenReturn(4L);
        when(jobService.getJob(1L)).thenReturn(Optional.of(job));

        StopPlaceJobDto result = jobController.getJobStatus(1L);

        assertThat(result.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(result.failure().message()).isEqualTo("Error");
        assertThat(result.failure().reasonCode()).isEqualTo(JobFailureReason.STALE_VERSION);
        assertThat(result.failure().currentVersion()).isEqualTo(4L);
        assertThat(result.result())
            .as("a failed job wrote nothing, so there is no result to read")
            .isNull();
    }

    /**
     * A claimed job is reported as PROCESSING, and a job with no outcome yet gives neither
     * object. That is what tells a client to poll again rather than to read an outcome.
     */
    @Test
    public void aJobInProgressGivesNeitherResultNorFailure() {
        AsyncStopPlaceJob job = mock(AsyncStopPlaceJob.class);
        when(job.getId()).thenReturn(1L);
        when(job.getStatus()).thenReturn(AsyncStopPlaceJobStatus.IN_PROGRESS);
        when(jobService.getJob(1L)).thenReturn(Optional.of(job));

        StopPlaceJobDto result = jobController.getJobStatus(1L);

        assertThat(result.status()).isEqualTo(AsyncStopPlaceJobStatus.PROCESSING);
        assertThat(result.result()).isNull();
        assertThat(result.failure()).isNull();
    }

    @Test
    public void shouldThrowErrorIfJobNotFound() {
        assertThatThrownBy(() -> jobController.getJobStatus(1L))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Job with ID 1 not found");
    }
}
