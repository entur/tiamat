package org.rutebanken.tiamat.repository;

import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.model.IanaCountryTldEnumeration;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TopographicPlaceRepository extends PagingAndSortingRepository<TopographicPlace, Long>,
        JpaRepository<TopographicPlace, Long>, IdentifiedEntityRepository<TopographicPlace> {


    /**
     * Should only be one per name, country and placeType...
     */
    List<TopographicPlace> findByNameValueAndCountryRefRefAndTopographicPlaceType(String name, IanaCountryTldEnumeration ianCountryTld, TopographicPlaceTypeEnumeration placeType);

    @Query("select tp from TopographicPlace tp inner join tp.polygon pp where contains(pp.polygon, :#{#point}) = TRUE")
    List<TopographicPlace> findByPoint(@Param("point") Point point);

}
