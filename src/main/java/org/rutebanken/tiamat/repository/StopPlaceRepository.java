package org.rutebanken.tiamat.repository;

import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Iterator;

public interface StopPlaceRepository extends StopPlaceRepositoryCustom, EntityInVersionRepository<StopPlace> {

    Page<StopPlace> findAllByOrderByChangedDesc(Pageable pageable);

    StopPlace findByNameValueAndCentroid(String name, Point geometryPoint);

    Page<StopPlace> findByNameValueContainingIgnoreCaseOrderByChangedDesc(String name, Pageable pageable);

    @Override
    Iterator<StopPlace> scrollStopPlaces();

    @Override
    Iterator<StopPlace> scrollStopPlaces(StopPlaceSearch stopPlaceSearch);

}

