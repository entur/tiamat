package org.rutebanken.tiamat.repository;

import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Iterator;
import java.util.List;

public interface StopPlaceRepository extends JpaRepository<StopPlace, Long>, StopPlaceRepositoryCustom, IdentifiedEntityRepository<StopPlace> {

    Page<StopPlace> findAllByOrderByChangedDesc(Pageable pageable);

    StopPlace findByNameValueAndCentroid(String name, Point geometryPoint);

    Page<StopPlace> findByNameValueContainingIgnoreCaseOrderByChangedDesc(String name, Pageable pageable);

    @Override
    Iterator<StopPlace> scrollStopPlaces();

    @Override
    Iterator<StopPlace> scrollStopPlaces(List<String> stopPlaceIds);

    @Override
    StopPlace findFirstByNetexIdOrderByVersionDesc(String netexId);

}

