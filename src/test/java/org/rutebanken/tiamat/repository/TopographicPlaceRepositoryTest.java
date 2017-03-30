package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.*;
import org.junit.Test;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TopographicPlaceRepositoryTest extends TiamatIntegrationTest {

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