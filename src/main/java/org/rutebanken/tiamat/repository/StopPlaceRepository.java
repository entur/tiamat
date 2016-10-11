package org.rutebanken.tiamat.repository;

import com.vividsolutions.jts.geom.Point;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.rutebanken.tiamat.model.StopPlace;

public interface StopPlaceRepository extends PagingAndSortingRepository<StopPlace, Long>, StopPlaceRepositoryCustom {

    Page<StopPlace> findAllByOrderByChangedDesc(Pageable pageable);

    StopPlace findByNameValueAndCentroidLocationGeometryPoint(String name, Point geometryPoint);

    Page<StopPlace> findByNameValueContainingIgnoreCaseOrderByChangedDesc(String name, Pageable pageable);

    @Override
    @CachePut(value = "stopPlace", key = "#p0.getId()", cacheManager = "guavaCacheManager")
    StopPlace save(StopPlace stopPlace);

    @Override
    @Cacheable(value = "stopPlace", key = "#p0", cacheManager = "guavaCacheManager")
    StopPlace findOne(Long stopPlaceId);

}

