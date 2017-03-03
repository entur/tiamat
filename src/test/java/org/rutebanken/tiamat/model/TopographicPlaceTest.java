package org.rutebanken.tiamat.model;

import org.eclipse.jetty.websocket.jsr356.annotations.JsrParamIdText;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.CommonSpringBootTest;
import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Transactional
public class TopographicPlaceTest extends CommonSpringBootTest {


    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Test
    public void stopPlaceShouldBeReferencingTopographicalPlace() {

        TopographicPlace nedreEiker = new TopographicPlace();
        nedreEiker.setName(new EmbeddableMultilingualString("Nedre Eiker", "no"));

        topographicPlaceRepository.save(nedreEiker);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Steinberg", "no"));
        stopPlace.setTopographicPlace(nedreEiker);

        stopPlaceRepository.save(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findByNetexId(stopPlace.getNetexId());
        assertThat(actualStopPlace.getTopographicPlace()).isNotNull();
        assertThat(actualStopPlace.getTopographicPlace().getNetexId()).isEqualTo(nedreEiker.getNetexId());
        assertThat(actualStopPlace.getTopographicPlace().getName()).isEqualTo(nedreEiker.getName());
    }

    @Test
    public void topographicPlacesShouldBeNestable() {

        // County
        TopographicPlace buskerud = new TopographicPlace();
        buskerud.setName(new EmbeddableMultilingualString("Buskerud", "no"));

        topographicPlaceRepository.save(buskerud);

        // Municipality
        TopographicPlace nedreEiker = new TopographicPlace();
        nedreEiker.setName(new EmbeddableMultilingualString("Nedre Eiker", "no"));
        nedreEiker.setParentTopographicPlace(buskerud);

        topographicPlaceRepository.save(nedreEiker);

        TopographicPlace actualNedreEiker = topographicPlaceRepository.findByNetexId(nedreEiker.getNetexId());

        assertThat(actualNedreEiker).isNotNull();
        assertThat(actualNedreEiker.getParentTopographicPlace()).isNotNull();
        assertThat(actualNedreEiker.getParentTopographicPlace().getNetexId()).isEqualTo(buskerud.getNetexId());

    }

    @Test
    public void topographicPlacesShouldBePartOfCountry() {

        CountryRef countryRef = new CountryRef();
        countryRef.setRef(IanaCountryTldEnumeration.NO);

        TopographicPlace akershus = new TopographicPlace();
        akershus.setName(new EmbeddableMultilingualString("Akershus", "no"));
        akershus.setCountryRef(countryRef);

        topographicPlaceRepository.save(akershus);

        TopographicPlace actual = topographicPlaceRepository.findByNetexId(akershus.getNetexId());
        assertThat(actual.getCountryRef()).isNotNull();
        assertThat(actual.getCountryRef().getRef()).isEqualTo(IanaCountryTldEnumeration.NO);
    }

}