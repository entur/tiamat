package no.rutebanken.tiamat.ifopt.transfer.assembler;

import no.rutebanken.tiamat.ifopt.transfer.dto.StopPlaceDTO;
import no.rutebanken.tiamat.repository.ifopt.TopographicPlaceRepository;
import org.junit.Test;
import uk.org.netex.netex.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StopPlaceAssemblerTest {

    @Test
    public void testAssembleMunicipality() throws Exception {

        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);

        StopPlaceAssembler stopPlaceAssembler = new StopPlaceAssembler(mock(SimplePointAssembler.class), topographicPlaceRepository, mock(QuayAssembler.class));

        String municipalityName = "Asker";

        TopographicPlace municipality = topographicPlace("id", municipalityName);

        TopographicPlaceRefStructure municipalityReference = topographicRef(municipality.getId());

        StopPlace stopPlace = stopPlaceWithRef(municipalityReference);

        when(topographicPlaceRepository.findOne(municipalityReference.getRef())).thenReturn(municipality);

        List<TopographicPlace> topographicPlaces = new ArrayList<>(Arrays.asList(municipality));
        when(topographicPlaceRepository.findByNameValueAndCountryRefRefAndTopographicPlaceType(municipalityName, IanaCountryTldEnumeration.NO, TopographicPlaceTypeEnumeration.TOWN)).thenReturn(topographicPlaces);

        StopPlaceDTO stopPlaceDTO = stopPlaceAssembler.assembleMunicipalityAndCounty(new StopPlaceDTO(), stopPlace);

        assertThat(stopPlaceDTO.municipality).isEqualTo(municipalityName);
    }

    @Test
    public void testAssembleMunicipalityWithCounty() throws Exception {

        TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);
        StopPlaceAssembler stopPlaceAssembler = new StopPlaceAssembler(mock(SimplePointAssembler.class), topographicPlaceRepository, mock(QuayAssembler.class));

        String municipalityName = "Bærum";

        TopographicPlace municipality = topographicPlace("id", municipalityName);

        TopographicPlaceRefStructure municipalityReference = topographicRef(municipality.getId());

        StopPlace stopPlace = stopPlaceWithRef(municipalityReference);

        when(topographicPlaceRepository.findOne(municipalityReference.getRef())).thenReturn(municipality);

        String countyName = "Akershus";

        TopographicPlace county = topographicPlace("id2", countyName);
        TopographicPlaceRefStructure countyRef = topographicRef(county.getId());

        municipality.setParentTopographicPlaceRef(countyRef);

        when(topographicPlaceRepository.findOne(countyRef.getRef())).thenReturn(county);

        List<TopographicPlace> municipalities = new ArrayList<>(Arrays.asList(municipality));
        when(topographicPlaceRepository.findByNameValueAndCountryRefRefAndTopographicPlaceType(municipalityName, IanaCountryTldEnumeration.NO, TopographicPlaceTypeEnumeration.TOWN)).thenReturn(municipalities);

        List<TopographicPlace> counties = new ArrayList<>(Arrays.asList(county));
        when(topographicPlaceRepository.findByNameValueAndCountryRefRefAndTopographicPlaceType(countyName, IanaCountryTldEnumeration.NO, TopographicPlaceTypeEnumeration.COUNTY)).thenReturn(counties);


        StopPlaceDTO stopPlaceDTO = stopPlaceAssembler.assembleMunicipalityAndCounty(new StopPlaceDTO(), stopPlace);

        assertThat(stopPlaceDTO.county).isEqualTo(countyName);
    }

    private StopPlace stopPlaceWithRef(TopographicPlaceRefStructure topographicPlaceRefStructure) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setTopographicPlaceRef(topographicPlaceRefStructure);
        return stopPlace;
    }

    private TopographicPlaceRefStructure topographicRef(String ref) {
        TopographicPlaceRefStructure topographicPlaceRefStructure = new TopographicPlaceRefStructure();
        topographicPlaceRefStructure.setRef(ref);
        return topographicPlaceRefStructure;
    }

    private TopographicPlace topographicPlace(String id, String name) {
        TopographicPlace municipality = new TopographicPlace();
        municipality.setId(id);
        municipality.setName(new MultilingualString(name, "", ""));
        return municipality;
    }

}