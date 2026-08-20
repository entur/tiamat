package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

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
    @Query("""
            update AsyncStopPlaceJob j
               set j.status = :claimed, j.claimedAt = :claimedAt
             where j.id = :id and j.status = :accepted
            """)
    int claim(@Param("id") Long id,
              @Param("accepted") AsyncStopPlaceJobStatus accepted,
              @Param("claimed") AsyncStopPlaceJobStatus claimed,
              @Param("claimedAt") Instant claimedAt);
}
