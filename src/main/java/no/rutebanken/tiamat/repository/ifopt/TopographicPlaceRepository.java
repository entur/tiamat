package no.rutebanken.tiamat.repository.ifopt;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;
import uk.org.netex.netex.IanaCountryTldEnumeration;
import uk.org.netex.netex.TopographicPlace;
import uk.org.netex.netex.TopographicPlaceTypeEnumeration;

import java.util.List;

public interface TopographicPlaceRepository extends PagingAndSortingRepository<TopographicPlace, String> {


    /**
     * Should only be one per name, country and placeType...
     */
    List<TopographicPlace> findByNameValueAndCountryRefRefAndTopographicPlaceType(String name, IanaCountryTldEnumeration ianCountryTld, TopographicPlaceTypeEnumeration placeType);

}

