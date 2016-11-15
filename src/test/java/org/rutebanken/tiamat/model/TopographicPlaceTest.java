package org.rutebanken.tiamat.model;

import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class TopographicPlaceTest {


    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Test
    public void stopPlaceShouldBeReferencingTopographicalPlace() {

        TopographicPlace nedreEiker = new TopographicPlace();
        nedreEiker.setName(new MultilingualString("Nedre Eiker", "no"));

        topographicPlaceRepository.save(nedreEiker);

        TopographicPlaceRefStructure topographicPlaceRefStructure = new TopographicPlaceRefStructure();
        topographicPlaceRefStructure.setRef(String.valueOf(nedreEiker.getId()));

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString("Steinberg", "no"));
        stopPlace.setTopographicPlaceRef(topographicPlaceRefStructure);

        stopPlaceRepository.save(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findOne(stopPlace.getId());
        assertThat(actualStopPlace.getTopographicPlaceRef()).isNotNull();
        assertThat(actualStopPlace.getTopographicPlaceRef().getRef()).isEqualTo(topographicPlaceRefStructure.getRef());
    }

    @Test
    public void topographicPlacesShouldBeNestable() {

        // County
        TopographicPlace buskerud = new TopographicPlace();
        buskerud.setName(new MultilingualString("Buskerud", "no"));

        topographicPlaceRepository.save(buskerud);

        TopographicPlaceRefStructure buskerudReference = new TopographicPlaceRefStructure();
        buskerudReference.setRef(String.valueOf(buskerud.getId()));

        // Municipality
        TopographicPlace nedreEiker = new TopographicPlace();
        nedreEiker.setName(new MultilingualString("Nedre Eiker", "no"));
        nedreEiker.setParentTopographicPlaceRef(buskerudReference);

        topographicPlaceRepository.save(nedreEiker);

        TopographicPlace actualNedreEiker = topographicPlaceRepository.findOne(nedreEiker.getId());

        assertThat(actualNedreEiker).isNotNull();
        assertThat(actualNedreEiker.getParentTopographicPlaceRef()).isNotNull();
        assertThat(actualNedreEiker.getParentTopographicPlaceRef().getRef()).isEqualTo(buskerudReference.getRef());

    }

    @Test
    public void topographicPlacesShouldBePartOfCountry() {

        CountryRef countryRef = new CountryRef();
        countryRef.setRef(IanaCountryTldEnumeration.NO);

        TopographicPlace akershus = new TopographicPlace();
        akershus.setName(new MultilingualString("Akershus", "no"));
        akershus.setCountryRef(countryRef);

        topographicPlaceRepository.save(akershus);

        TopographicPlace actual = topographicPlaceRepository.findOne(akershus.getId());
        assertThat(actual.getCountryRef()).isNotNull();
        assertThat(actual.getCountryRef().getRef()).isEqualTo(IanaCountryTldEnumeration.NO);
    }

}