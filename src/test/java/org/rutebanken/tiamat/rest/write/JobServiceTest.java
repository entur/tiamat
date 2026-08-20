package org.rutebanken.tiamat.rest.write;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.model.job.StopPlaceIdMapping;
import org.rutebanken.tiamat.repository.AsyncStopPlaceJobRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JobServiceTest {

    private final AsyncStopPlaceJobRepository repository = mock(
        AsyncStopPlaceJobRepository.class
    );

    private final UsernameFetcher usernameFetcher = mock(UsernameFetcher.class);

    private final JobService jobService = new JobService(repository, usernameFetcher);

    @Test
    public void shouldRecordSubmittingUserOnNewJob() {
        when(usernameFetcher.getUserNameForAuthenticatedUser()).thenReturn("alice");

        jobService.createJob();

        ArgumentCaptor<AsyncStopPlaceJob> captor = ArgumentCaptor.forClass(AsyncStopPlaceJob.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getCreatedBy()).isEqualTo("alice");
    }

    /**
     * Without a creation time the table cannot be purged on a retention policy, and a reaper
     * cannot tell a job that is genuinely in progress from one orphaned by a restart.
     */
    @Test
    public void shouldRecordCreationTimeOnNewJob() {
        Instant before = Instant.now();

        jobService.createJob();

        ArgumentCaptor<AsyncStopPlaceJob> captor = ArgumentCaptor.forClass(AsyncStopPlaceJob.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getCreatedAt())
                .isNotNull()
                .isBetween(before, Instant.now());
    }

    /**
     * The row count is the answer: one caller wins the claim, and any other delivery of the same
     * job is told to do nothing.
     */
    @Test
    public void claimSucceedsWhenTheJobIsStillUnclaimed() {
        when(repository.claim(eq(1L), eq(AsyncStopPlaceJobStatus.PROCESSING),
                eq(AsyncStopPlaceJobStatus.IN_PROGRESS), any(Instant.class))).thenReturn(1);

        assertThat(jobService.claim(1L)).isTrue();
    }

    @Test
    public void claimFailsWhenTheJobWasAlreadyClaimedOrIsTerminal() {
        when(repository.claim(eq(1L), eq(AsyncStopPlaceJobStatus.PROCESSING),
                eq(AsyncStopPlaceJobStatus.IN_PROGRESS), any(Instant.class))).thenReturn(0);

        assertThat(jobService.claim(1L)).isFalse();
    }

    @Test
    public void shouldNotReturnJobSubmittedByAnotherUser() {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        job.setCreatedBy("alice");
        when(repository.findById(1L)).thenReturn(Optional.of(job));
        when(usernameFetcher.getUserNameForAuthenticatedUser()).thenReturn("bob");

        assertThat(jobService.getJob(1L)).isEmpty();
    }

    @Test
    public void shouldReturnJobSubmittedByTheSameUser() {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        job.setCreatedBy("alice");
        when(repository.findById(1L)).thenReturn(Optional.of(job));
        when(usernameFetcher.getUserNameForAuthenticatedUser()).thenReturn("alice");

        assertThat(jobService.getJob(1L)).contains(job);
    }

    /**
     * With authorization disabled there is no username, and jobs are stored without one, so
     * an unauthenticated deployment keeps working. A job that does have an owner stays
     * inaccessible to a caller without one.
     */
    @Test
    public void shouldReturnUnownedJobWhenThereIsNoAuthenticatedUser() {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        when(repository.findById(1L)).thenReturn(Optional.of(job));
        when(usernameFetcher.getUserNameForAuthenticatedUser()).thenReturn(null);

        assertThat(jobService.getJob(1L)).contains(job);
    }

    @Test
    public void shouldNotReturnOwnedJobWhenThereIsNoAuthenticatedUser() {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        job.setCreatedBy("alice");
        when(repository.findById(1L)).thenReturn(Optional.of(job));
        when(usernameFetcher.getUserNameForAuthenticatedUser()).thenReturn(null);

        assertThat(jobService.getJob(1L)).isEmpty();
    }

    @Test
    public void shouldCreateJob() {
        jobService.createJob();

        ArgumentCaptor<AsyncStopPlaceJob> captor = ArgumentCaptor.forClass(
            AsyncStopPlaceJob.class
        );
        verify(repository).save(captor.capture());

        AsyncStopPlaceJob saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(
            AsyncStopPlaceJobStatus.PROCESSING
        );
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
        assertThat(result.get().getStatus()).isEqualTo(
            AsyncStopPlaceJobStatus.PROCESSING
        );
    }

    @Test
    public void shouldMarkJobAsSuccess() {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        job.setId(1L);
        job.setStatus(AsyncStopPlaceJobStatus.PROCESSING);
        when(repository.findById(1L)).thenReturn(Optional.of(job));

        jobService.succeed(
            1L,
            singletonList(new StopPlaceIdMapping("submittedId", "createdId"))
        );

        ArgumentCaptor<AsyncStopPlaceJob> captor = ArgumentCaptor.forClass(
            AsyncStopPlaceJob.class
        );
        verify(repository).save(captor.capture());

        AsyncStopPlaceJob saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(
            AsyncStopPlaceJobStatus.FINISHED
        );
        assertEquals("createdId", saved.getCreatedIds().getFirst().createdId());
    }

    @Test
    public void shouldMarkJobAsFailed() {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        job.setId(1L);
        job.setStatus(AsyncStopPlaceJobStatus.PROCESSING);
        when(repository.findById(1L)).thenReturn(Optional.of(job));

        jobService.fail(1L, new IllegalArgumentException("Error"));

        ArgumentCaptor<AsyncStopPlaceJob> captor = ArgumentCaptor.forClass(
            AsyncStopPlaceJob.class
        );
        verify(repository).save(captor.capture());

        AsyncStopPlaceJob saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(saved.getReason()).isEqualTo("Error");
    }

    @Test
    public void shouldForwardIllegalArgumentReason() {
        IllegalArgumentException exception = new IllegalArgumentException(
            "Invalid input"
        );
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        when(repository.findById(1L)).thenReturn(Optional.of(job));

        jobService.fail(1L, exception);

        ArgumentCaptor<AsyncStopPlaceJob> captor = ArgumentCaptor.forClass(
            AsyncStopPlaceJob.class
        );
        verify(repository).save(captor.capture());

        AsyncStopPlaceJob saved = captor.getValue();
        assertThat(saved.getReason()).isEqualTo("Invalid input");
    }

    /**
     * The reason is the only explanation a caller gets for a failed write, so a message-less
     * exception must not leave it empty.
     */
    @Test
    public void shouldFallBackToGenericReasonWhenExceptionHasNoMessage() {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        when(repository.findById(1L)).thenReturn(Optional.of(job));

        jobService.fail(1L, new IllegalArgumentException());

        ArgumentCaptor<AsyncStopPlaceJob> captor = ArgumentCaptor.forClass(AsyncStopPlaceJob.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getReason()).isEqualTo("An unexpected error occurred.");
    }

    @Test
    public void shouldFormatRejectionExecutionReason() {
        RejectedExecutionException exception = new RejectedExecutionException(
            "Internal description"
        );
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        when(repository.findById(1L)).thenReturn(Optional.of(job));

        jobService.fail(1L, exception);

        ArgumentCaptor<AsyncStopPlaceJob> captor = ArgumentCaptor.forClass(
            AsyncStopPlaceJob.class
        );
        verify(repository).save(captor.capture());

        AsyncStopPlaceJob saved = captor.getValue();
        assertThat(saved.getReason()).isEqualTo(
            "The job queue is full. Please try again later."
        );
    }

    @Test
    public void shouldFormatExceptionReason() {
        RuntimeException exception = new RuntimeException(
            "Internal system error"
        );
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        when(repository.findById(1L)).thenReturn(Optional.of(job));

        jobService.fail(1L, exception);

        ArgumentCaptor<AsyncStopPlaceJob> captor = ArgumentCaptor.forClass(
            AsyncStopPlaceJob.class
        );
        verify(repository).save(captor.capture());

        AsyncStopPlaceJob saved = captor.getValue();
        assertThat(saved.getReason()).isEqualTo(
            "An unexpected error occurred."
        );
    }
}
