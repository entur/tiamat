package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.model.job.JobFailureReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;

public interface AsyncStopPlaceJobRepository
    extends
        PagingAndSortingRepository<AsyncStopPlaceJob, Long>,
        JpaRepository<AsyncStopPlaceJob, Long> {

    /**
     * Takes ownership of a job, atomically. The row count is the answer: a read followed by a
     * write would race, whereas this cannot, because the update takes a row lock and concurrent
     * callers serialise on it.
     *
     * @return 1 if this caller now owns the job, 0 if someone else claimed it or it already
     *         reached a terminal state.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update AsyncStopPlaceJob j set j.status = :claimed, j.claimedAt = :claimedAt"
            + " where j.id = :id and j.status = :accepted")
    int claim(@Param("id") Long id,
              @Param("accepted") AsyncStopPlaceJobStatus accepted,
              @Param("claimed") AsyncStopPlaceJobStatus claimed,
              @Param("claimedAt") Instant claimedAt);

    /**
     * Moves a job between states, only if it is currently in one of the expected ones. Guards both
     * completion, which must only happen while the job is still claimed, and failure, which must
     * not overwrite a job that already reached a terminal state.
     *
     * @return 1 if the job moved, 0 if it was not in any of the expected states.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update AsyncStopPlaceJob j set j.status = :to"
            + " where j.id = :id and j.status in :from")
    int transition(@Param("id") Long id,
                   @Param("from") Collection<AsyncStopPlaceJobStatus> from,
                   @Param("to") AsyncStopPlaceJobStatus to);

    /**
     * Moves a job to a terminal state and records why, in one statement. Moves it only if it is
     * currently in one of the expected states.
     * <p>
     * The reason travels with the transition, and not in a later write. A caller that moves the
     * status first, and saves the reason after, leaves a window open. In that window the job
     * reads as terminal and gives no reason for it.
     *
     * @return 1 if the job moved, 0 if it was not in any of the expected states.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update AsyncStopPlaceJob j"
            + " set j.status = :to, j.reason = :reason, j.reasonCode = :reasonCode"
            + " where j.id = :id and j.status in :from")
    int transitionWithReason(@Param("id") Long id,
                             @Param("from") Collection<AsyncStopPlaceJobStatus> from,
                             @Param("to") AsyncStopPlaceJobStatus to,
                             @Param("reason") String reason,
                             @Param("reasonCode") JobFailureReason reasonCode);

    /**
     * Moves jobs that stayed in a non terminal state for too long, and records why.
     * <p>
     * Age comes from the claim if there is one, and from creation if there is not. So a job that
     * no worker ever claimed, because its publish failed, still ages out. And a job that waited a
     * long time in a queue ages from the moment work started on it.
     * <p>
     * A job with neither timestamp keeps its state. Its age is unknown, and an unknown age is not
     * a reason to call it stale.
     * <p>
     * Sets the same two columns as {@link #transitionWithReason}, deliberately. This sweep is how
     * a job reaches TIMED_OUT in almost every case. A sweep that set only the status gives the
     * caller a terminal job and no reason for it.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update AsyncStopPlaceJob j"
            + " set j.status = :timedOut, j.reason = :reason, j.reasonCode = :reasonCode"
            + " where j.status in :nonTerminal"
            + " and coalesce(j.claimedAt, j.createdAt) < :threshold")
    int timeOutStale(@Param("nonTerminal") Collection<AsyncStopPlaceJobStatus> nonTerminal,
                     @Param("timedOut") AsyncStopPlaceJobStatus timedOut,
                     @Param("reason") String reason,
                     @Param("reasonCode") JobFailureReason reasonCode,
                     @Param("threshold") Instant threshold);
}
