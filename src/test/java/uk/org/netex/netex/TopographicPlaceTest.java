package uk.org.netex.netex;

import no.rutebanken.tiamat.TiamatApplication;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import no.rutebanken.tiamat.repository.ifopt.TopographicPlaceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class TopographicPlaceTest {


    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Test
    public void stopPlaceShouldBeReferencingTopographicalPlace() {

        TopographicPlace nedreEiker = new TopographicPlace();
        nedreEiker.setName(new MultilingualString("Nedre Eiker", "no", ""));

        topographicPlaceRepository.save(nedreEiker);

        TopographicPlaceRefStructure topographicPlaceRefStructure = new TopographicPlaceRefStructure();
        topographicPlaceRefStructure.setRef(nedreEiker.getId());

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString("Steinberg", "no", ""));
        stopPlace.setTopographicPlaceRef(topographicPlaceRefStructure);

        stopPlaceRepository.save(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findOne(stopPlace.getId());
        assertThat(actualStopPlace.getTopographicPlaceRef()).isNotNull();
        assertThat(actualStopPlace.getTopographicPlaceRef().getRef()).isEqualTo(topographicPlaceRefStructure.getRef());
    }

    @Test
    public void stopPlacesShouldBeNestable() {

        TopographicPlace buskerud = new TopographicPlace();
        buskerud.setName(new MultilingualString("Buskerud", "no", ""));

        topographicPlaceRepository.save(buskerud);

        TopographicPlaceRefStructure buskerudReference = new TopographicPlaceRefStructure();
        buskerudReference.setRef(buskerud.getId());

        TopographicPlace nedreEiker = new TopographicPlace();
        nedreEiker.setName(new MultilingualString("Nedre Eiker", "no", ""));
        nedreEiker.setParentTopographicPlaceRef(buskerudReference);

        topographicPlaceRepository.save(nedreEiker);

        TopographicPlace actualNedreEiker = topographicPlaceRepository.findOne(nedreEiker.getId());

        assertThat(actualNedreEiker).isNotNull();
        assertThat(actualNedreEiker.getParentTopographicPlaceRef()).isNotNull();
        assertThat(actualNedreEiker.getParentTopographicPlaceRef().getRef()).isEqualTo(buskerudReference.getRef());

    }

}