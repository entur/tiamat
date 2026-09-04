package org.rutebanken.tiamat.writer;

import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.model.job.StopPlaceIdMapping;
import org.rutebanken.tiamat.repository.AsyncStopPlaceJobRepository;
import org.rutebanken.tiamat.rest.validation.StaleVersionException;
import org.rutebanken.tiamat.writer.async.WriteJobNotOwnedException;
import org.rutebanken.tiamat.writer.async.WriteJobPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;

@Service
public class JobService {

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    private static final int MAX_CAUSE_DEPTH = 10;

    private static final String GENERIC_REASON = "An unexpected error occurred.";

    private final AsyncStopPlaceJobRepository repo;
    private final WriteJobPrincipal principal;

    public JobService(AsyncStopPlaceJobRepository repo, WriteJobPrincipal principal) {
        this.repo = repo;
        this.principal = principal;
    }

    public AsyncStopPlaceJob createJob() {
        var job = new AsyncStopPlaceJob();
        job.setStatus(AsyncStopPlaceJobStatus.PROCESSING);
        job.setCreatedIds(Collections.emptyList());
        job.setCreatedBy(principal.currentSubject());
        job.setPrincipalClaims(principal.capture());
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
     * Moves jobs that have not reached a terminal state within the timeout to TIMED_OUT.
     * <p>
     * Without this a job whose worker died, or whose publish failed before any transport saw it,
     * stays non terminal forever and the client polls indefinitely. Timing out is deliberately not
     * a retry: processing is not idempotent, so the safe outcome is to report that nothing
     * happened and let the client decide whether to resubmit.
     * <p>
     * Timing out a job whose write is still running is safe rather than merely unlikely:
     * completion is conditional on still holding the claim, so the write rolls back. That makes
     * the timeout a matter of wasted work rather than correctness, which is why there is no
     * heartbeat yet.
     */
    @Transactional
    public int timeOutStaleJobs(Duration timeout) {
        int timedOut = repo.timeOutStale(
                List.of(AsyncStopPlaceJobStatus.PROCESSING, AsyncStopPlaceJobStatus.IN_PROGRESS),
                AsyncStopPlaceJobStatus.TIMED_OUT,
                Instant.now().minus(timeout)
        );
        if (timedOut > 0) {
            logger.warn("Timed out {} write job(s) that had not completed within {}", timedOut, timeout);
        }
        return timedOut;
    }

    /**
     * The claims identifying whoever submitted the job, for reinstating them before the write.
     * Not scoped to the caller: this is the processing unit acting on the submitter's behalf, not
     * a client reading someone else's job.
     */
    public Map<String, Object> principalClaimsFor(Long jobId) {
        return repo.findById(jobId)
                .map(AsyncStopPlaceJob::getPrincipalClaims)
                .orElse(Map.of());
    }

    /**
     * Reports a job as having never completed, for reasons unrelated to its payload. Recorded in
     * its own transaction for the same reason as a failure.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void timeOut(Long jobId, String reason) {
        int moved = repo.transition(
                jobId,
                List.of(AsyncStopPlaceJobStatus.PROCESSING, AsyncStopPlaceJobStatus.IN_PROGRESS),
                AsyncStopPlaceJobStatus.TIMED_OUT
        );
        if (moved != 1) {
            logger.warn("Job {} already reached a terminal state, not recording a timeout", jobId);
            return;
        }
        var job = repo.findById(jobId).orElseThrow();
        job.setReason(reason);
        repo.save(job);
    }

    /**
     * Job ids are sequential, so a job is only returned to the principal that submitted it.
     * Jobs belonging to someone else are reported as absent rather than forbidden, so that
     * callers cannot probe which ids exist.
     */
    public Optional<AsyncStopPlaceJob> getJob(Long jobId) {
        String currentSubject = principal.currentSubject();
        return repo.findById(jobId)
                .filter(job -> Objects.equals(job.getCreatedBy(), currentSubject));
    }

    /**
     * Completes a job, but only while it is still claimed by this worker.
     * <p>
     * Joins the caller's transaction deliberately, so that it commits together with the write. If
     * a sweeper timed the job out while the write was running the conditional update matches
     * nothing, and throwing here rolls the write back. That is what lets a client treat TIMED_OUT
     * as "nothing was written, resubmitting is safe" rather than having to go and look.
     */
    public void succeed(Long id, List<StopPlaceIdMapping> createdStopPlaceIds) {
        if (repo.transition(id, List.of(AsyncStopPlaceJobStatus.IN_PROGRESS),
                AsyncStopPlaceJobStatus.FINISHED) != 1) {
            throw new WriteJobNotOwnedException(id);
        }
        // The conditional update holds the row lock for the rest of the transaction, so nothing
        // else can change the job before the created ids are written.
        var job = repo.findById(id).orElseThrow();
        job.setCreatedIds(createdStopPlaceIds);
        repo.save(job);
    }

    /**
     * Records a failure, in its own transaction.
     * <p>
     * The write it belongs to has usually just rolled back, and a rolled back transaction cannot
     * carry the record of why. Failing in a separate transaction means the reason survives.
     * <p>
     * Accepts a job that is either accepted or claimed, since a failure can happen before a
     * transport ever takes the job, but will not overwrite one that already reached a terminal
     * state.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AsyncStopPlaceJob fail(Long id, Exception exception) {
        int moved = repo.transition(
                id,
                List.of(AsyncStopPlaceJobStatus.PROCESSING, AsyncStopPlaceJobStatus.IN_PROGRESS),
                AsyncStopPlaceJobStatus.FAILED
        );
        var job = repo.findById(id).orElseThrow();
        if (moved != 1) {
            logger.warn("Job {} already reached {}, not recording failure", id, job.getStatus());
            return job;
        }
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
        // Matched on the exception itself rather than anywhere in its causes, deliberately. This is
        // the one reason forwarded verbatim, so it must stay limited to the messages this codebase
        // writes for the caller. Unwrapping here would surface text from anywhere in the stack.
        if (e instanceof IllegalArgumentException) {
            return e.getMessage() != null ? e.getMessage() : GENERIC_REASON;
        }
        return constantReasonFor(e);
    }

    /**
     * These reasons are constants, so they are safe to match on a wrapped exception, and they have
     * to be: what reaches the handler is whatever escapes a transactional proxy, and a transport
     * that dispatches jobs itself may add a layer of its own. A miss here is silent, costing the
     * caller a usable reason rather than raising anything.
     */
    private String constantReasonFor(Throwable throwable) {
        Throwable cause = throwable;
        // A stale version is the one failure worth resubmitting, so it says so rather than
        // arriving as another unexplained rejection. The message carries both versions, because
        // the caller needs the current one to read the stop place again.
        // Bounded rather than walked to the end: a cause chain can be cyclic, and no wrapping this
        // has to see through is anywhere near this deep.
        for (int depth = 0; cause != null && depth < MAX_CAUSE_DEPTH; depth++, cause = cause.getCause()) {
            if (cause instanceof StaleVersionException stale) {
                return "The stop place moved to version " + stale.getCurrentVersion()
                        + " while this job was pending. Read it again and reapply the change.";
            }
            if (cause instanceof AccessDeniedException) {
                return "You do not have permission to perform this operation.";
            }
            if (cause instanceof RejectedExecutionException) {
                return "The job queue is full. Please try again later.";
            }
            if (cause instanceof DataIntegrityViolationException) {
                return "A database constraint was violated. This may be due to invalid input data or a conflict with existing data.";
            }
        }
        return GENERIC_REASON;
    }
}
