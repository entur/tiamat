package org.rutebanken.tiamat.rest.write;

import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.model.job.StopPlaceIdMapping;
import org.rutebanken.tiamat.repository.AsyncStopPlaceJobRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;

@Service
public class JobService {

    private static final String GENERIC_REASON = "An unexpected error occurred.";

    private final AsyncStopPlaceJobRepository repo;
    private final UsernameFetcher usernameFetcher;

    public JobService(AsyncStopPlaceJobRepository repo, UsernameFetcher usernameFetcher) {
        this.repo = repo;
        this.usernameFetcher = usernameFetcher;
    }

    public AsyncStopPlaceJob createJob() {
        var job = new AsyncStopPlaceJob();
        job.setStatus(AsyncStopPlaceJobStatus.PROCESSING);
        job.setCreatedIds(Collections.emptyList());
        job.setCreatedBy(usernameFetcher.getUserNameForAuthenticatedUser());
        job.setCreatedAt(Instant.now());
        return repo.save(job);
    }

    /**
     * Takes ownership of a job before it is processed.
     * <p>
     * Delivery is at least once, and processing is not idempotent: a create mints a fresh NSR id
     * each run, so processing a redelivered job twice would silently produce a second stop place.
     * Claiming makes the job the idempotency key.
     *
     * Runs in its own transaction, deliberately. The claim must be visible to other deliveries
     * immediately, and must not hold a row lock for the duration of the write, so it must not join
     * the transaction that wraps processing and completion.
     *
     * @return true if this caller now owns the job. False means it was already claimed or has
     *         reached a terminal state, and the delivery should be discarded.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean claim(Long jobId) {
        return repo.claim(
                jobId,
                AsyncStopPlaceJobStatus.PROCESSING,
                AsyncStopPlaceJobStatus.IN_PROGRESS,
                Instant.now()
        ) == 1;
    }

    /**
     * Job ids are sequential, so a job is only returned to the principal that submitted it.
     * Jobs belonging to someone else are reported as absent rather than forbidden, so that
     * callers cannot probe which ids exist.
     */
    public Optional<AsyncStopPlaceJob> getJob(Long jobId) {
        String currentUser = usernameFetcher.getUserNameForAuthenticatedUser();
        return repo.findById(jobId)
                .filter(job -> Objects.equals(job.getCreatedBy(), currentUser));
    }

    public void succeed(Long id, List<StopPlaceIdMapping> createdStopPlaceIds) {
        var job = repo.findById(id).orElseThrow();
        job.setStatus(AsyncStopPlaceJobStatus.FINISHED);
        job.setCreatedIds(createdStopPlaceIds);
        repo.save(job);
    }

    public AsyncStopPlaceJob fail(Long id, Exception exception) {
        var job = repo.findById(id).orElseThrow();
        job.setStatus(AsyncStopPlaceJobStatus.FAILED);
        job.setReason(formatException(exception));
        return repo.save(job);
    }

    /**
     * The reason is returned to the caller, so only messages meant for them are surfaced.
     * IllegalArgumentException carries the validation feedback for the submitted payload and is
     * the only explanation a caller gets for a failed write, since the payload is no longer
     * validated on the request thread. Everything else is reported generically, because the
     * message may describe internals.
     */
    private String formatException(Exception e) {
        if (e instanceof IllegalArgumentException) {
            return e.getMessage() != null ? e.getMessage() : GENERIC_REASON;
        } else if (e instanceof AccessDeniedException) {
            return "You do not have permission to perform this operation.";
        } else if (e instanceof RejectedExecutionException) {
            return "The job queue is full. Please try again later.";
        } else if (e instanceof DataIntegrityViolationException) {
            return "A database constraint was violated. This may be due to invalid input data or a conflict with existing data.";
        }
        return GENERIC_REASON;
    }
}
