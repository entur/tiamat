package org.rutebanken.tiamat.rest.write;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.repository.AsyncStopPlaceJobRepository;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JobServiceTest {

    private final AsyncStopPlaceJobRepository repository = mock(AsyncStopPlaceJobRepository.class);

    private final JobService jobService = new JobService(repository);

    @Test
    public void shouldCreateJob() {
        jobService.createJob();

        ArgumentCaptor<AsyncStopPlaceJob> captor = ArgumentCaptor.forClass(AsyncStopPlaceJob.class);
        verify(repository).save(captor.capture());

        AsyncStopPlaceJob saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(AsyncStopPlaceJobStatus.PROCESSING);
        assertThat(saved.getCreatedIds()).isEmpty();
    }

    @Test
    public void shouldGetJob() {
        AsyncStopPlaceJob job = mock(AsyncStopPlaceJob.class);
        when(job.getId()).thenReturn(1L);
        when(job.getStatus()).thenReturn(AsyncStopPlaceJobStatus.PROCESSING);
        when(repository.findById(1L)).thenReturn(Optional.of(job));

        Optional<AsyncStopPlaceJob> result = jobService.getJob(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getStatus()).isEqualTo(AsyncStopPlaceJobStatus.PROCESSING);
    }

    @Test
    public void shouldMarkJobAsSuccess() {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        job.setId(1L);
        job.setStatus(AsyncStopPlaceJobStatus.PROCESSING);
        when(repository.findById(1L)).thenReturn(Optional.of(job));

        jobService.succeed(1L, singletonList("id"));

        ArgumentCaptor<AsyncStopPlaceJob> captor = ArgumentCaptor.forClass(AsyncStopPlaceJob.class);
        verify(repository).save(captor.capture());

        AsyncStopPlaceJob saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(AsyncStopPlaceJobStatus.FINISHED);
        assertThat(saved.getCreatedIds()).containsExactly("id");
    }

    @Test
    public void shouldMarkJobAsFailed() {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        job.setId(1L);
        job.setStatus(AsyncStopPlaceJobStatus.PROCESSING);
        when(repository.findById(1L)).thenReturn(Optional.of(job));

        jobService.fail(1L, "Error");

        ArgumentCaptor<AsyncStopPlaceJob> captor = ArgumentCaptor.forClass(AsyncStopPlaceJob.class);
        verify(repository).save(captor.capture());

        AsyncStopPlaceJob saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(saved.getReason()).isEqualTo("Error");

    }

}