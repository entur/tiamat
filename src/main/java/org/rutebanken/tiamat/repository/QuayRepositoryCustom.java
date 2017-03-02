package org.rutebanken.tiamat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.rutebanken.tiamat.model.Quay;

import java.util.Set;

public interface QuayRepositoryCustom {

    Page<Quay> findQuaysWithin(double xMin, double yMin, double xMax, double yMax, Pageable pageable);

    Long findByKeyValue(String key, Set<String> values);
}
