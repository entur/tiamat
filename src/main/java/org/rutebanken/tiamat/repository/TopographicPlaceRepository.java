package org.rutebanken.tiamat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.rutebanken.tiamat.model.IanaCountryTldEnumeration;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;

import java.util.List;

public interface TopographicPlaceRepository extends PagingAndSortingRepository<TopographicPlace, Long>, JpaRepository<TopographicPlace, Long> {


    /**
     * Should only be one per name, country and placeType...
     */
    List<TopographicPlace> findByNameValueAndCountryRefRefAndTopographicPlaceType(String name, IanaCountryTldEnumeration ianCountryTld, TopographicPlaceTypeEnumeration placeType);

}

