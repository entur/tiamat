package no.rutebanken.tiamat.repository.ifopt;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import uk.org.netex.netex.StopPlace;

public interface StopPlaceRepository extends PagingAndSortingRepository<StopPlace, String>, StopPlaceRepositoryCustom {

    Page<StopPlace> findByNameValueContainingIgnoreCase(String name, Pageable pageable);

    /*@Query("select s from StopPlace s " +
            "left outer join s.centroid sp " +
            "left outer join sp.location l " +
            "where l.latitude like ?1 AND l.longitude like ?2")
    Page<StopPlace> findNearby(BigDecimal latitude, BigDecimal longitude, Pageable pageable);*/

}

