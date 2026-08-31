package org.rutebanken.tiamat.writer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.rutebanken.tiamat.writer.async.WriteJobPrincipal;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.model.job.StopPlaceIdMapping;
import org.rutebanken.tiamat.repository.AsyncStopPlaceJobRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.rutebanken.tiamat.writer.async.WriteJobNotOwnedException;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JobServiceTest {

    private final AsyncStopPlaceJobRepository repository = mock(
        AsyncStopPlaceJobRepository.class
    );

    private final WriteJobPrincipal principal = mock(WriteJobPrincipal.class);

    private final JobService jobService = new JobService(repository, principal);

    /**
     * Completion is conditional on the job still being claimed, so the default for these tests is
     * a job whose claim is intact. The cases that exercise a lost claim override this.
     */
    @Before
    public void jobIsOwnedByDefault() {
        when(repository.transition(anyLong(), anyCollection(), any(AsyncStopPlaceJobStatus.class)))
                .thenReturn(1);
    }

    @Test
    public void shouldRecordSubmittingSubjectOnNewJob() {
        when(principal.currentSubject()).thenReturn("auth0|alice");

        jobService.createJob();

        ArgumentCaptor<AsyncStopPlaceJob> captor = ArgumentCaptor.forClass(AsyncStopPlaceJob.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getCreatedBy()).isEqualTo("auth0|alice");
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

    /**
     * Completion is conditional on still owning the job. If a sweeper timed the job out while the
     * write was running, the completion must fail so the surrounding transaction rolls the write
     * back: a job reported as TIMED_OUT has to mean nothing was written.
     */
    @Test
    public void succeedFailsWhenTheClaimWasLost() {
        when(repository.transition(eq(1L), anyCollection(), eq(AsyncStopPlaceJobStatus.FINISHED)))
                .thenReturn(0);

        assertThatThrownBy(() -> jobService.succeed(1L, null))
                .isInstanceOf(WriteJobNotOwnedException.class);
    }

    @Test
    public void succeedRecordsCreatedIdsWhenTheClaimIsStillHeld() {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        when(repository.transition(eq(1L), anyCollection(), eq(AsyncStopPlaceJobStatus.FINISHED)))
                .thenReturn(1);
        when(repository.findById(1L)).thenReturn(Optional.of(job));

        jobService.succeed(1L, singletonList(new StopPlaceIdMapping("submittedId", "createdId")));

        ArgumentCaptor<AsyncStopPlaceJob> captor = ArgumentCaptor.forClass(AsyncStopPlaceJob.class);
        verify(repository).save(captor.capture());
        assertEquals("createdId", captor.getValue().getCreatedIds().getFirst().createdId());
        // The status moves via the conditional update, not by mutating the entity, so that
        // completion cannot happen unless the job is still claimed.
        verify(repository).transition(eq(1L), anyCollection(), eq(AsyncStopPlaceJobStatus.FINISHED));
    }

    @Test
    public void shouldNotReturnJobSubmittedByAnotherUser() {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        job.setCreatedBy("auth0|alice");
        when(repository.findById(1L)).thenReturn(Optional.of(job));
        when(principal.currentSubject()).thenReturn("auth0|bob");

        assertThat(jobService.getJob(1L)).isEmpty();
    }

    @Test
    public void shouldReturnJobSubmittedByTheSameUser() {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        job.setCreatedBy("auth0|alice");
        when(repository.findById(1L)).thenReturn(Optional.of(job));
        when(principal.currentSubject()).thenReturn("auth0|alice");

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
        when(principal.currentSubject()).thenReturn(null);

        assertThat(jobService.getJob(1L)).contains(job);
    }

    @Test
    public void shouldNotReturnOwnedJobWhenThereIsNoAuthenticatedUser() {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        job.setCreatedBy("auth0|alice");
        when(repository.findById(1L)).thenReturn(Optional.of(job));
        when(principal.currentSubject()).thenReturn(null);

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

        verify(repository).transition(eq(1L), anyCollection(), eq(AsyncStopPlaceJobStatus.FAILED));
        assertThat(captor.getValue().getReason()).isEqualTo("Error");
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

    /**
     * What reaches the handler is whatever escapes a transactional proxy, and a transport that
     * dispatches jobs itself may add a layer of its own, so a reason that only matched the outermost
     * exception would go missing exactly when something went wrong.
     */
    @Test
    public void shouldFormatConstantReasonForAWrappedException() {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        when(repository.findById(1L)).thenReturn(Optional.of(job));

        jobService.fail(1L, new RuntimeException("dispatch failed",
                new DataIntegrityViolationException("could not execute statement")));

        ArgumentCaptor<AsyncStopPlaceJob> captor = ArgumentCaptor.forClass(AsyncStopPlaceJob.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getReason())
                .isEqualTo("A database constraint was violated. This may be due to invalid input data or a conflict with existing data.");
    }

    /**
     * The one reason forwarded verbatim stays matched on the exception itself. A message from
     * somewhere deep in the stack is not written for this caller and should not reach them.
     */
    @Test
    public void shouldNotForwardTheMessageOfAWrappedIllegalArgument() {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        when(repository.findById(1L)).thenReturn(Optional.of(job));

        jobService.fail(1L, new RuntimeException("wrapper",
                new IllegalArgumentException("internal detail from some library")));

        ArgumentCaptor<AsyncStopPlaceJob> captor = ArgumentCaptor.forClass(AsyncStopPlaceJob.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getReason()).isEqualTo("An unexpected error occurred.");
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
