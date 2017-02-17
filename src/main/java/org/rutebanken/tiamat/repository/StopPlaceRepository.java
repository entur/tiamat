package org.rutebanken.tiamat.repository;

import com.vividsolutions.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.rutebanken.tiamat.model.StopPlace;

import java.util.concurrent.BlockingQueue;

public interface StopPlaceRepository extends JpaRepository<StopPlace, Long>, StopPlaceRepositoryCustom {

    Page<StopPlace> findAllByOrderByChangedDesc(Pageable pageable);

    StopPlace findByNameValueAndCentroid(String name, Point geometryPoint);

    Page<StopPlace> findByNameValueContainingIgnoreCaseOrderByChangedDesc(String name, Pageable pageable);

    @Override
    StopPlace findOne(Long stopPlaceId);

    @Override
    BlockingQueue<StopPlace> scrollStopPlaces();

}

