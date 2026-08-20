package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
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
}
