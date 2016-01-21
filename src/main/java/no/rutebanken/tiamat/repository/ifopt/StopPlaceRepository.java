package no.rutebanken.tiamat.repository.ifopt;

import com.vividsolutions.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import uk.org.netex.netex.StopPlace;

public interface StopPlaceRepository extends PagingAndSortingRepository<StopPlace, String>, StopPlaceRepositoryCustom {

    Page<StopPlace> findAllByOrderByChangedDesc(Pageable pageable);

    StopPlace findByNameValueAndCentroidLocationGeometryPoint(String name, Point geometryPoint);

    Page<StopPlace> findByNameValueContainingIgnoreCaseOrderByChangedDesc(String name, Pageable pageable);
}

