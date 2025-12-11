package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AsyncStopPlaceJobRepository
    extends
        PagingAndSortingRepository<AsyncStopPlaceJob, Long>,
        JpaRepository<AsyncStopPlaceJob, Long> {}
