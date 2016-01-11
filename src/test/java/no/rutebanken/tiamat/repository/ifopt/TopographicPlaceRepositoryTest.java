package no.rutebanken.tiamat.repository.ifopt;

import no.rutebanken.tiamat.TiamatApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.org.netex.netex.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class TopographicPlaceRepositoryTest {

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Test
    public void findByTopographicPlaceAndCountry() {

        CountryRef countryRef = new CountryRef();
        countryRef.setRef(IanaCountryTldEnumeration.NO);

        TopographicPlace akershus = new TopographicPlace();
        akershus.setName(new MultilingualString("Akershus", "no", ""));
        akershus.setCountryRef(countryRef);
        akershus.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

        topographicPlaceRepository.save(akershus);

        List<TopographicPlace> places = topographicPlaceRepository.findByNameValueAndCountryRefRefAndTopographicPlaceType("Akershus", IanaCountryTldEnumeration.NO, TopographicPlaceTypeEnumeration.COUNTY);
        assertThat(places).extracting("id", String.class).contains(akershus.getId());
    }
}