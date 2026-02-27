package org.rutebanken.tiamat.rest.write.controllers;

import jakarta.ws.rs.NotFoundException;
import org.junit.Test;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.rest.write.JobService;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JobControllerTest {

    private final JobService jobService = mock(JobService.class);
    private final JobController jobController = new JobControllerImpl(jobService);

    @Test
    public void shouldGetJobStatus() {
        AsyncStopPlaceJob job = mock(AsyncStopPlaceJob.class);
        when(job.getId()).thenReturn(1L);
        when(job.getStatus()).thenReturn(AsyncStopPlaceJobStatus.FAILED);
        when(job.getReason()).thenReturn("Error");
        when(job.getCreatedIds()).thenReturn(singletonList("id"));
        when(jobService.getJob(1L)).thenReturn(Optional.of(job));

        StopPlaceJobDto result = jobController.getJobStatus(1L);

        assertThat(result).isNotNull();
        assertThat(result.jobId()).isEqualTo(1L);
        assertThat(result.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(result.errorMessage()).isEqualTo("Error");
        assertThat(result.createdIds()).containsExactly("id");
    }

    @Test
    public void shouldThrowErrorIfJobNotFound() {
        assertThatThrownBy(() -> jobController.getJobStatus(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Job with ID 1 not found");
    }
}