package org.rutebanken.tiamat.repository;

import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.model.IanaCountryTldEnumeration;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.List;

public interface TopographicPlaceRepository extends EntityInVersionRepository<TopographicPlace>, TopographicPlaceRepositoryCustom {

    /**
     * Should only be one per name, country and placeType...
     */
    List<TopographicPlace> findByNameValueAndCountryRefRefAndTopographicPlaceType(String name, IanaCountryTldEnumeration ianCountryTld, TopographicPlaceTypeEnumeration placeType);

    @QueryHints(value = { @QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    @Query("select tp from TopographicPlace tp inner join tp.polygon pp where contains(pp.polygon, :#{#point}) = TRUE AND tp.version = (SELECT MAX(tpv.version) FROM TopographicPlace tpv WHERE tpv.netexId = tp.netexId)")
    List<TopographicPlace> findByPoint(@Param("point") Point point);

    @QueryHints(value = { @QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    @Query("select tp from TopographicPlace tp WHERE tp.version = (SELECT MAX(tpv.version) FROM TopographicPlace tpv WHERE tpv.netexId = tp.netexId)")
    List<TopographicPlace> findAllMaxVersion();

}
