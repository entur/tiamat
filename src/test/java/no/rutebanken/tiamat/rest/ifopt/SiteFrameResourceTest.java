package no.rutebanken.tiamat.rest.ifopt;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import no.rutebanken.tiamat.repository.ifopt.TopographicPlaceRepository;
import org.junit.Test;
import uk.org.netex.netex.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SiteFrameResourceTest {


    @Test
    public void testFindOrCreateTopographicalPlace() throws Exception {
        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);

        SiteFrameResource siteFrameResource = new SiteFrameResource(
                mock(StopPlaceRepository.class),
                mock(QuayRepository.class),
                topographicPlaceRepository,
                mock(XmlMapper.class));

        // County
        TopographicPlace county = new TopographicPlace();
        county.setId("1");
        county.setName(new MultilingualString("County name", "no", ""));

        CountryRef countryRef = new CountryRef();
        countryRef.setRef(IanaCountryTldEnumeration.NO);
        county.setCountryRef(countryRef);
        county.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

        when(topographicPlaceRepository.findByNameValueAndCountryRefRefAndTopographicPlaceType(
                county.getName().getValue(),
                county.getCountryRef().getRef(),
                county.getTopographicPlaceType()))
                .thenReturn(Collections.singletonList(county));


        TopographicPlaceRefStructure countyRef = new TopographicPlaceRefStructure();
        countyRef.setRef(county.getId());


        TopographicPlace municipality = new TopographicPlace();
        String municipalityId = "municipalityId";
        municipality.setId(municipalityId);
        municipality.setName(new MultilingualString("Municipality", "no", ""));
        municipality.setParentTopographicPlaceRef(countyRef);
        municipality.setCountryRef(countryRef);


        when(topographicPlaceRepository.findByNameValueAndCountryRefRefAndTopographicPlaceType(
                municipality.getName().getValue(),
                municipality.getCountryRef().getRef(),
                municipality.getTopographicPlaceType()))
                .thenReturn(Collections.singletonList(municipality));


        TopographicPlaceRefStructure municipalityRef = new TopographicPlaceRefStructure();
        municipalityRef.setRef(municipality.getId());

        final AtomicInteger idCounter = new AtomicInteger(10);

        when(topographicPlaceRepository.save(any(TopographicPlace.class)))
                .then(invocationOnMock -> {

                    TopographicPlace topographicPlace = (TopographicPlace) invocationOnMock.getArguments()[0];
                    System.out.println("Saving topographical place " + topographicPlace.getName());
                    topographicPlace.setId(String.valueOf(idCounter.incrementAndGet()));
                    return topographicPlace;
                });


        List<TopographicPlace> places = Arrays.asList(county, municipality);

        TopographicPlace topographicPlace = siteFrameResource.findOrCreateTopographicalPlace(places, municipalityRef).get();

        assertThat(topographicPlace.getName().getValue()).isEqualTo(municipality.getName().getValue());
        assertThat(topographicPlace.getId()).isNotEqualTo(municipalityId);


    }
}