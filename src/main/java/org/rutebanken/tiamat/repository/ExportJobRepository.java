package org.rutebanken.tiamat.repository;


import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.job.ExportJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExportJobRepository extends PagingAndSortingRepository<ExportJob, Long>, JpaRepository<ExportJob, Long> {
}
