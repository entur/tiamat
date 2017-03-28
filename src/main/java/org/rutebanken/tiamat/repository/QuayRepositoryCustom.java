package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.Quay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuayRepositoryCustom extends DataManagedObjectStructureRepository<Quay> {

    Page<Quay> findQuaysWithin(double xMin, double yMin, double xMax, double yMax, Pageable pageable);

}
