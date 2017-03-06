package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.CommonSpringBootTest;
import org.rutebanken.tiamat.model.*;
import org.junit.Test;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TopographicPlaceRepositoryTest extends CommonSpringBootTest {

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Test
    public void findByTopographicPlaceAndCountry() {

        CountryRef countryRef = new CountryRef();
        countryRef.setRef(IanaCountryTldEnumeration.NO);

        TopographicPlace akershus = new TopographicPlace();
        akershus.setName(new EmbeddableMultilingualString("Akershus", "no"));
        akershus.setCountryRef(countryRef);
        akershus.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

        topographicPlaceRepository.save(akershus);

        List<TopographicPlace> places = topographicPlaceRepository.findByNameValueAndCountryRefRefAndTopographicPlaceType("Akershus", IanaCountryTldEnumeration.NO, TopographicPlaceTypeEnumeration.COUNTY);
        assertThat(places).extracting(IdentifiedEntity::getNetexId).contains(akershus.getNetexId());
    }
}